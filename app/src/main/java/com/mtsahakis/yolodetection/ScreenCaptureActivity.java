package com.mtsahakis.yolodetection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ScreenCaptureActivity extends Activity {
    private static final String TAG = "YoloTest";

    private static final int REQUEST_CODE = 100;
    private TextView textView;
    private final ServiceBroadcastReceiver serviceBroadcastReceiver = new ServiceBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // register broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ui_change_service");
        registerReceiver(serviceBroadcastReceiver, intentFilter);

        // start projection
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startProjection();
            }
        });

        // stop projection
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProjection();
            }
        });

        // init text view for result
        textView = findViewById(R.id.tv_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(com.mtsahakis.yolodetection.ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }

    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(com.mtsahakis.yolodetection.ScreenCaptureService.getStopIntent(this));
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + serviceBroadcastReceiver.getClass().getName());
        unregisterReceiver(serviceBroadcastReceiver);
        super.onDestroy();
    }


    public class ServiceBroadcastReceiver extends BroadcastReceiver {

        @SuppressLint("DefaultLocale")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int result_size = bundle.getInt("result_size");
                    int image_processed = bundle.getInt("image_processed");
                    textView.setText(String.format("Detected Object Number: %d, Processed Image Number: %d", result_size, image_processed));
                }
            } else {
                Log.d(TAG, "onReceive: null");
            }
        }
    }
}