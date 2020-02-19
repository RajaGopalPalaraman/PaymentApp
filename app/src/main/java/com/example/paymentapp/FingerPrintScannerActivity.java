package com.example.paymentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

public class FingerPrintScannerActivity extends AppCompatActivity implements MFS100Event {

    public static final String ISO_TEMPLATE = "ISOTemplate";

    private static final long THRESHOLD = 1500L;
    private static final int TIME_OUT = 10000;

    private static final int FINGER_PRINT_DATA_WHAT = 100;

    private MFS100 mfs100 = null;

    private long mLastAttTime=0L;
    private long mLastDttTime=0L;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_scanner);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == FINGER_PRINT_DATA_WHAT) {
                    if (msg.obj == null) {
                        setResult(RESULT_OK);
                    } else {
                        FingerData fingerData = (FingerData) msg.obj;
                        setResult(RESULT_OK, new Intent().putExtra(ISO_TEMPLATE, fingerData.ISOTemplate()));
                    }
                    finish();
                }
            }
        };
        mfs100 = mfs100 == null ? new MFS100(this) : mfs100;
        mfs100.SetApplicationContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mfs100.UnInit();
            mfs100.Dispose();
        } catch (Exception ignored) { }
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        if (hasPermission && SystemClock.elapsedRealtime() - mLastAttTime >= THRESHOLD) {
            mLastAttTime = SystemClock.elapsedRealtime();
            try {
                if (vid == 1204 || vid == 11279) {
                    if (pid == 34323) {
                        int code = mfs100.LoadFirmware();
                        if (code != 0) {
                            Toast.makeText(this, R.string.unable_to_load_firmware, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.load_firmware_success, Toast.LENGTH_SHORT).show();
                        }
                    } else if (pid == 4101) {
                        int code = mfs100.Init();
                        if (code != 0) {
                            Toast.makeText(this, R.string.unable_to_initialize_device, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, R.string.device_initialization_success, Toast.LENGTH_SHORT).show();
                            scanFinger();
                        }
                    }
                }
            } catch (Exception ignored) { }
        }
    }

    @Override
    public void OnDeviceDetached() {
        if (SystemClock.elapsedRealtime() - mLastDttTime >= THRESHOLD) {
            try {
                mLastDttTime = SystemClock.elapsedRealtime();
                mfs100.UnInit();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void OnHostCheckFailed(String err) {

    }

    private void scanFinger() {
        new Thread(() -> {
            FingerData fingerData = new FingerData();
            try {
                if (mfs100.AutoCapture(fingerData, TIME_OUT, true) == 0) {
                    handler.obtainMessage(FINGER_PRINT_DATA_WHAT, fingerData).sendToTarget();
                    return;
                }
            } catch (Exception ignored) {

            }
            handler.obtainMessage(FINGER_PRINT_DATA_WHAT, null).sendToTarget();
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mfs100.StopAutoCapture();
        } catch (Exception ignored) {

        }
        setResult(RESULT_CANCELED);
    }
}
