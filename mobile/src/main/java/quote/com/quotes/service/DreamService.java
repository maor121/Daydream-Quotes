package quote.com.quotes.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import quote.com.quotes.utils.BackgroundCameraHelper;
import quote.com.quotes.utils.MotionDetector;
import quote.com.quotes.QuoteUIHelper;
import quote.com.quotes.R;
import quote.com.quotes.utils.UIUpdater;

public class DreamService extends android.service.dreams.DreamService implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = DreamService.class.getSimpleName();

    //UI elements
    ViewGroup pnlQuote;
    TextView quoteView, sourceView;

    //Running in the background
    BackgroundCameraHelper cameraHelper;
    MotionDetector detector;
    UIUpdater quotesUpdater;

    long lastMotionTime = 0;

    //Constants
    private long CAMERA_SLEEP_WHEN_MOTION_DETECTED = 10*1000;
    private long MOTIONLESS_TIME_UNTIL_SLEEP = 10*1000;

    public DreamService() {
        cameraHelper = new BackgroundCameraHelper(this, this, new onOpenCVReady());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.content_main);
        //Mouse move won't wake it up
        setInteractive(true);

        pnlQuote = (ViewGroup)findViewById(R.id.pnlQuote);
        quoteView = (TextView)findViewById(R.id.txtQuote);
        sourceView = (TextView)findViewById(R.id.txtSource);

        ((ViewGroup)getWindow().getDecorView().getRootView()).getChildAt(0)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuoteUIHelper.changeQuote(quoteView, sourceView);
            }
        });

        //Until a motion is detected
        pnlQuote.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();

        quotesUpdater = new UIUpdater(new updateQuoteRunnable(), 10*(1000*60));

        startMotionDetection();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();

        quotesUpdater.stopUpdates();

        stopMotionDetection();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (detector.proccessFrame(inputFrame.gray())) {
            Log.d(TAG, "Motion detected!");

            lastMotionTime = System.currentTimeMillis();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //show quote panel
                    if (!quotesUpdater.isRunning()) {
                        quotesUpdater.startUpdates();
                        pnlQuote.setVisibility(View.VISIBLE);
                        //AdminUtils.lightScreen(DreamService.this, getWindow());
                    }
                    //stop motion detection
                    stopMotionDetection();
                }
            });
            //start motion detection again later
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMotionDetection();
                }
            }, CAMERA_SLEEP_WHEN_MOTION_DETECTED);
        } else { //No motion detected
            if (pnlQuote.getVisibility()== View.VISIBLE &&
                    System.currentTimeMillis()-lastMotionTime > MOTIONLESS_TIME_UNTIL_SLEEP) {
                //Hide quotes
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pnlQuote.setVisibility(View.INVISIBLE);
                        //AdminUtils.change_brightness(getBaseContext(), getWindow(), 0);
                        //AdminUtils.dimScreen2(getWindow());
                        quotesUpdater.stopUpdates();
                    }
                });
            }
        }

        return null;
    }

    public void startMotionDetection() {
        detector = new MotionDetector();
        cameraHelper.init();

        lastMotionTime = System.currentTimeMillis();
    }
    public void stopMotionDetection() {
        cameraHelper.releaseCamera();
        detector.releaseCache();
    }

    class  updateQuoteRunnable implements Runnable {
        @Override
        public void run() {
            QuoteUIHelper.changeQuote(quoteView, sourceView);
        }
    }

    class onOpenCVReady implements Runnable {

        @Override
        public void run() {

        }
    }
}
