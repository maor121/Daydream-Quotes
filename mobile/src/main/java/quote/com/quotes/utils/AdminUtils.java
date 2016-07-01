package quote.com.quotes.utils;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Maor on 24/06/2016.
 */
public class AdminUtils {
    private static final String TAG = AdminUtils.class.getSimpleName();

    /**
     * Cease video output from device to screen.
     *
     * On android devices with screens this is lock, on devices without screens (connected to TV with hdmi)
     * this will cause "No Signal" blue screen to appear.
     *
     * Requires admin permission: Lock screen
     */
    public static void lockScreen(Context context) {
        Log.d(TAG, "lockScreen");
        //ComponentName mDeviceAdminSample = new ComponentName(context, DeviceAdminReceiver.class);;
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        dpm.lockNow();
    }

    /**
     * Changes brightness level of screen device between 0 and 255.
     * Carefull not to be stuck with a black screen.
     *
     * Note: Will only work on android controlled screens. Will not work on HDMI\DVI connected screen.
     */
    public static void change_brightness(Context ctx, Window w, int brightnessLevel) {
        Settings.System.putInt(ctx.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        Settings.System.putInt(ctx.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);

        WindowManager.LayoutParams lp = w.getAttributes();
        lp.screenBrightness = brightnessLevel / (float) 255;
        w.setAttributes(lp);
    }
}
