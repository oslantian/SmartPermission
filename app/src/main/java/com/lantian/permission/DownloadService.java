package com.lantian.permission;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lantian.smartpermission.annotation.SmartPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private Handler handler = new Handler();
    private long fileLength;
    private long downloadLength;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        download();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SmartPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void download() {
        new Thread(() -> {
            File file = new File(Environment.getExternalStorageDirectory() + "/qq_test.apk");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            InputStream is = null;
            try {
                URL url = new URL("https://d30c38ede51f21db6d3983492e321448.dlied1.cdntips.com/dlied1.qq.com/qqweb/QQlite/Android_apk/qqlite_4.0.1.1060_537064364.apk");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                fileLength = Integer.valueOf(conn.getHeaderField("Content-Length"));
                is = conn.getInputStream();
                int respondCode = conn.getResponseCode();
                if (respondCode == 200) {
                    handler.post(run);
                    byte[] buffer = new byte[1024 * 8];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        downloadLength = downloadLength + len;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Runnable run = new Runnable() {
        public void run() {
            long pro = downloadLength * 100 / fileLength;
            if (pro < 100) {
                Log.i(TAG, "下载中……" + pro + "%");
                handler.postDelayed(this, 1000);
            } else {
                Log.i(TAG, "下载完成");
            }
        }
    };

    @Override
    public void onDestroy() {
        handler.removeCallbacks(run);
        super.onDestroy();
    }
}
