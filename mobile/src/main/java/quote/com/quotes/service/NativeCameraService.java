package quote.com.quotes.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * Created by Maor on 23/06/2016.
 *
 * A service that uses camera with native code
 * VideoCapture is not supported in OpenCV 3.0 and therefor it will not work on most devices.
 */
public class NativeCameraService extends Service {
    private static final String TAG = NativeCameraService.class.getSimpleName();
    private boolean mStopThread;
    private Thread mThread;
    private VideoCapture mCamera;
    private int mFrameWidth;
    private int mFrameHeight;
    private int mCameraIndex = -1;
    private Bitmap mCacheBitmap;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    try
                    {
                        if (!connectCamera(640, 480))
                            Log.e(TAG, "Could not connect camera");
                        else
                            Log.d(TAG, "Camera successfully connected");
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, "MyServer.connectCamera throws an exception: " + e.getMessage());
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {

        this.disconnectCamera();

        Toast.makeText(this, "service stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        Log.d(TAG, "service.onStart: begin");

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

    private boolean connectCamera(int width, int height) {
        /* First step - initialize camera connection */
        if (!initializeCamera(width, height))
            return false;

        /* now we can start update thread */
        mThread = new Thread(new CameraWorker());
        mThread.start();

        return true;
    }

    private boolean initializeCamera(int width, int height) {
        synchronized (this) {

            if (mCameraIndex == -1)
                mCamera = new VideoCapture(Videoio.CV_CAP_ANDROID);
            else
                mCamera = new VideoCapture(Videoio.CV_CAP_ANDROID + mCameraIndex);

            if (mCamera == null)
                return false;

            if (mCamera.isOpened() == false)
                return false;

            //java.util.List<Size> sizes = mCamera.getSupportedPreviewSizes();

            /* Select the size that fits surface considering maximum size allowed */
            Size frameSize = new Size(width, height);

            mFrameWidth = (int)frameSize.width;
            mFrameHeight = (int)frameSize.height;

            AllocateCache();

            mCamera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, frameSize.width);
            mCamera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, frameSize.height);
        }

        Log.i(TAG, "Selected camera frame size = (" + mFrameWidth + ", " + mFrameHeight + ")");

        return true;
    }

    protected void AllocateCache()
    {
        mCacheBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
    }

    private void releaseCamera() {
        synchronized (this) {
            if (mCamera != null) {
                mCamera.release();
            }
        }
    }

    private void disconnectCamera() {
        /* 1. We need to stop thread which updating the frames
         * 2. Stop camera and release it
         */
        try {
            mStopThread = true;
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mThread =  null;
            mStopThread = false;
        }

        /* Now release camera */
        releaseCamera();
    }

    protected void deliverAndDrawFrame(NativeCameraFrame frame)
    {
        Mat modified = frame.rgba();

        boolean bmpValid = true;
        if (modified != null) {
            try {
                Utils.matToBitmap(modified, mCacheBitmap);
            } catch(Exception e) {
                Log.e(TAG, "Mat type: " + modified);
                Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
                Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
                bmpValid = false;
            }
        }
    }

    private class NativeCameraFrame
    {
        public Mat rgba() {
            mCapture.retrieve(mRgba, Videoio.CV_CAP_MODE_RGB /*Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA*/);
            return mRgba;
        }

        public Mat gray() {
            mCapture.retrieve(mGray, Videoio.CV_CAP_MODE_GRAY /*Highgui.CV_CAP_ANDROID_GREY_FRAME*/);
            return mGray;
        }

        public NativeCameraFrame(VideoCapture capture) {
            mCapture = capture;
            mGray = new Mat();
            mRgba = new Mat();
        }

        private VideoCapture mCapture;
        private Mat mRgba;
        private Mat mGray;
    };

    private class CameraWorker implements Runnable
    {
        public void run()
        {
            do
            {
                if (!mCamera.grab()) {
                    Log.e(TAG, "Camera frame grab failed");
                    break;
                }

                deliverAndDrawFrame(new NativeCameraFrame(mCamera));

            } while (!mStopThread);
        }
    }
}
