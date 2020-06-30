package com.lantian.permission;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lantian.smartpermission.annotation.SmartPermission;

public class OldActivity extends Activity {
    private static final String TAG = "OldActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        findViewById(R.id.btn1).setOnClickListener(v -> request());
    }

    @SmartPermission(Manifest.permission.RECORD_AUDIO)
    private void request() {
        Log.i(TAG, "request: ");
    }
}
