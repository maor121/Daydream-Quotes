package quote.com.quotes.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;


import quote.com.quotes.utils.DummyAdminReciever;
import quote.com.quotes.utils.MotionDetector;

/**
 * A JavaCameraService with motion detection.
 *
 * It puts the device to sleep and keep the camera on. Required admin privilages to run
 *
 * Known issues:
 * 1) When device sleeps, camera frames are all green. (no picture). <--- MAKES IT UNUSABLE
 * 2) When device sleeps, camera frames are really slow (1 per 5 seconds on my device)
 * 3) When device sleeps, TV displays blue screen of death
 *
 * (2) is not a problem.
 * (3) is an issue that may be fixed by putting the TV to sleep when there is no signal
 * (1) is a pain in the ass.
 */
public class JavaCameraServiceDraft extends Service implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = JavaCameraServiceDraft.class.getSimpleName();

    JavaCameraView javaCameraView;
    Bitmap mCacheBitmap;

    boolean isCameraInitialized;
    MotionDetector detector;

    DevicePolicyManager devicePolicyManager;
    ComponentName adminComponent;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    /*OpenCV loaded. Start*/
                    javaCameraView.enableView();

                    //Turn off screen
                    devicePolicyManager.lockNow();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        if (isCameraInitialized)
            releaseCamera();

        Toast.makeText(this, "service stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.d(TAG, "service.onStart: begin");

        adminComponent = new ComponentName(this, DummyAdminReciever.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Log.d(TAG, "service started without admin rights. Closing");
            stopSelf();
            return START_NOT_STICKY;
        }

        detector = new MotionDetector();

        initCamera();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        Toast.makeText(this, "service started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "service.onStart: end");

        return START_NOT_STICKY;
    }

    private void initCamera() {
        //if (!connectCamera(640, 480))
        javaCameraView = new JavaCameraView(this, -1);
        //javaCameraView.setMaxFrameSize(640, 480);
        javaCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        //optional - show fps
        javaCameraView.enableFpsMeter();

        /*Hack*/
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm.addView(javaCameraView, params);
        //javaCameraView.setZOrderOnTop(true);
        javaCameraView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        isCameraInitialized = true;
    }
    private void releaseCamera() {
        javaCameraView.disableView();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(javaCameraView);
    }

    public void onCameraViewStarted(int width, int height) {
        mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mGray = inputFrame.gray();

        //if (detector.proccessFrame(mGray))
        //    Log.d(TAG, "Motion detected!");
        Mat result = detector.proccessFrameDebug(mGray);
        if (result != null)
            result.clone();

        return result;
    }
    /*
    Code to see camera bitmap :
    Mat mRGBA = inputFrame.rgba();
    mCacheBitmap = Bitmap.createBitmap(mRGBA.width(), mRGBA.height(), Bitmap.Config.ARGB_8888);
    Utils.matToBitmap(mRGBA, mCacheBitmap);
    mCacheBitmap
     */
}