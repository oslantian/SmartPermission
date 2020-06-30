package com.lantian.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class OriginalMethodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        findViewById(R.id.callBtn).setOnClickListener(v -> {
            //检测是否具有权限
            if (ActivityCompat.checkSelfPermission(OriginalMethodActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                //没有就去申请
                ActivityCompat.requestPermissions(OriginalMethodActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
            } else {
                call();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            //检测用户授权结果
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                call();
            }else {
                Toast.makeText(this,"权限被拒绝",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void call() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:1008611"));
        startActivity(intent);
    }
}