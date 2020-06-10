package com.lantian.smartpermission.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.lantian.smartpermission.callback.PermissionRequestCallback;

import java.util.ArrayList;
import java.util.List;

public class SmartSupportFragment extends Fragment {
    private PermissionRequestCallback callback;

    public void setPermissionRequestCallback(PermissionRequestCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 禁止横竖屏切换时的Fragment的重建
        setRetainInstance(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Activity#requestPermissions()方法中mHasCurrentPermissionsRequest为true时直接返回,此时permissions大小为0
        if (permissions.length == 0 && grantResults.length == 0) {
            return;
        }
        List<String> grantedPermissions = new ArrayList<>();
        List<String> deniedPermissions = new ArrayList<>();
        List<String> dontAskAgainPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults.length <= i || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                    //被拒绝的权限(仅仅是被拒绝，可以再次申请)
                    deniedPermissions.add(permission);
                } else {
                    //Don’t ask again，即使再次申请，也不会弹出提示，需要进入设置页面，手动开启权限
                    dontAskAgainPermissions.add(permission);
                }
            } else {
                grantedPermissions.add(permission);
            }
        }
        if (callback != null) {
            //没有权限被拒绝
            if (permissions.length == grantedPermissions.size()) {
                callback.onGranted();
            } else {
                callback.onDenied(grantedPermissions.toArray(new String[grantedPermissions.size()]),
                        deniedPermissions.toArray(new String[deniedPermissions.size()]),
                        dontAskAgainPermissions.toArray(new String[dontAskAgainPermissions.size()]));
            }
        }
    }
}

