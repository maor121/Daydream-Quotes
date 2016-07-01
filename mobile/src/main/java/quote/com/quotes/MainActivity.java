package quote.com.quotes;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Handler;

import quote.com.quotes.service.JavaCameraServiceDraft;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    DevicePolicyManager devicePolicyManager;
    ComponentName adminComponent;

    int REQUEST_ENABLE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView quoteView = (TextView)findViewById(R.id.txtQuote);
        final TextView sourceView = (TextView)findViewById(R.id.txtSource);

        ((ViewGroup)getWindow().getDecorView().getRootView()).getChildAt(0)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuoteUIHelper.changeQuote(quoteView, sourceView);
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                QuoteUIHelper.changeQuote(quoteView, sourceView);
                //AdminUtils.change_brightness(MainActivity.this, getWindow(), 0);
                //Toast toast =Toast.makeText(MainActivity.this, "Brightness changed", Toast.LENGTH_LONG);
                //toast.show();
            }
        });

        /*
        adminComponent = new ComponentName(this, DummyAdminReciever.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            //Request Admin rights
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(intent, REQUEST_ENABLE);
        } else {
            Log.d(TAG, "already have admin rights.");
            startCameraService();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_ENABLE == requestCode) {
            Log.d(TAG, "admin rights given");
            startCameraService();
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCameraService() {
        //Start camera service manually
        startService(new Intent(this, JavaCameraServiceDraft.class));

    }
}
