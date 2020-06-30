package com.lantian.permission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.lantian.smartpermission.annotation.SmartPermission;
import com.lantian.smartpermission.bean.SmartPermissionResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener((v) -> {
            call("1008611");
        });
        findViewById(R.id.btn2).setOnClickListener((v) -> {
            SmartPermissionResult smartPermissionResult = new SmartPermissionResult();
            multiple("arg1", smartPermissionResult, "arg2");
        });
        findViewById(R.id.btn3).setOnClickListener((v) -> {
            startActivity(new Intent(this, OtherActivity.class));
        });
        findViewById(R.id.btn4).setOnClickListener((v) -> {
            startService(new Intent(this, DownloadService.class));
        });
        findViewById(R.id.btn5).setOnClickListener((v) -> {
            startActivity(new Intent(this, OldActivity.class));
        });
        findViewById(R.id.btn6).setOnClickListener((v) -> {
            startActivity(new Intent(this, OriginalMethodActivity.class));
        });
    }

    @SmartPermission({Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA})
    private void multiple(String arg1, SmartPermissionResult smartPermissionResult, String arg2) {
        if (smartPermissionResult.isAllGranted()) {
            Log.i(TAG, "参数=" + arg1 + "---" + arg2);
        } else {
            String[] deniedPermissions = smartPermissionResult.getDeniedPermissions();
            String[] grantedPermissions = smartPermissionResult.getGrantedPermissions();
            String[] dontAskAgainPermissions = smartPermissionResult.getDontAskAgainPermissions();
            Log.i(TAG, "不再询问的权限" + Arrays.toString(dontAskAgainPermissions) + "");
            Log.i(TAG, "允许的权限" + Arrays.toString(grantedPermissions) + "");
            Log.i(TAG, "被拒绝权限" + Arrays.toString(deniedPermissions) + "");
        }
    }

    @SmartPermission(Manifest.permission.CALL_PHONE)
    private void call(String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
