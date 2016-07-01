package quote.com.quotes.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by Maor on 24/06/2016.
 *
 * A hackish class to use android camera without an activity (e.g from Service)
 * Also initializes OpenCV
 */
public class BackgroundCameraHelper {
    private static final String TAG = BackgroundCameraHelper.class.getSimpleName();

    CameraBridgeViewBase.CvCameraViewListener2 mListener;
    Runnable mCameraReadyCallback;
    BaseLoaderCallback mLoaderCallback;
    Context mContext;

    JavaCameraView javaCameraView;

    public BackgroundCameraHelper(Context context, CameraBridgeViewBase.CvCameraViewListener2 listener, Runnable cameraReadyCallback) {
        mListener = listener;
        mCameraReadyCallback = cameraReadyCallback;
        mContext = context;

        mLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                    /*OpenCV loaded. Start*/
                        javaCameraView.enableView();

                        mCameraReadyCallback.run();
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
    }

    public boolean init() {
        if (!initializeCamera())
            return false;

        initializeOpenCV();

        return true;
    }

    private void initializeOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, mContext, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean initializeCamera() {
        javaCameraView = new JavaCameraView(mContext, -1);
        //javaCameraView.setMaxFrameSize(640, 480);
        javaCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraView.setCvCameraViewListener(mListener);
        //optional - show fps
        //javaCameraView.enableFpsMeter();

        /*Hack*/
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        wm.addView(javaCameraView, params);
        //javaCameraView.setZOrderOnTop(true);
        javaCameraView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        return true;
    }

    public void releaseCamera() {
        javaCameraView.disableView();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(javaCameraView);
    }
}
