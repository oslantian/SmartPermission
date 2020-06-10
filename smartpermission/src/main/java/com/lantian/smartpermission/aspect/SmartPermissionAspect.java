package com.lantian.smartpermission.aspect;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.lantian.smartpermission.annotation.SmartPermission;
import com.lantian.smartpermission.bean.SmartPermissionResult;
import com.lantian.smartpermission.callback.PermissionRequestCallback;
import com.lantian.smartpermission.fragment.SmartFragment;
import com.lantian.smartpermission.fragment.SmartSupportFragment;
import com.lantian.smartpermission.util.SmartPermissionUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
public class SmartPermissionAspect {
    private static final String TAG_FRAGMENT_SUPPORT = "SmartSupportFragment";
    private static final String TAG_FRAGMENT = "SmartFragment";

    @Pointcut("execution(@com.lantian.smartpermission.annotation.SmartPermission * *(..))")
    public void checkPermission() {

    }

    @Around("checkPermission()")
    public void check(final ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        final Object[] args = point.getArgs();
        final Activity activity = SmartPermissionUtil.getInstance().getTopActivity();
        if (activity == null) {
            return;
        }
        SmartPermission annotation = signature.getMethod().getAnnotation(SmartPermission.class);
        final String[] permissions = annotation.value();
        if (permissions == null || permissions.length == 0) {
            proceed(args, point, new String[]{}, new String[]{}, new String[]{});
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            proceed(args, point, new String[]{}, new String[]{}, permissions);
            return;
        }
        //权限过滤，只申请被拒绝了的
        final List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.size() == 0) {
            proceed(args, point, new String[]{}, new String[]{}, permissions);
            return;
        }
        if (activity instanceof FragmentActivity) {
            supportPermissions(activity, args, point, deniedPermissions, permissions);
        } else {
            permissions(activity, args, point, deniedPermissions, permissions);
        }
    }



    private void supportPermissions(final Activity activity, final Object[] args, final ProceedingJoinPoint point,
                                    List<String> deniedPermissions, final String[] allPermissions) {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        SmartSupportFragment smartSupportFragment = new SmartSupportFragment();
        smartSupportFragment.setPermissionRequestCallback(new PermissionRequestCallback() {
            @Override
            public void onGranted() {
                proceed(args, point, new String[]{}, new String[]{}, allPermissions);
            }
            @Override
            public void onDenied(String[] grantedPermissions, String[] deniedPermissions, String[] dontAskAgainPermissions) {
                String[] gs = getGrantedPermissions(deniedPermissions, dontAskAgainPermissions, allPermissions);
                proceed(args, point, deniedPermissions, dontAskAgainPermissions, gs);
            }
        });
        fm.beginTransaction().add(smartSupportFragment, TAG_FRAGMENT_SUPPORT).commitAllowingStateLoss();
        fm.executePendingTransactions();
        smartSupportFragment.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), 65535);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissions(final Activity activity, final Object[] args, final ProceedingJoinPoint point,
                             List<String> deniedPermissions, final String[] allPermissions) {
        android.app.FragmentManager fm = activity.getFragmentManager();
        SmartFragment smartFragment = new SmartFragment();
        smartFragment.setPermissionRequestCallback(new PermissionRequestCallback() {
            @Override
            public void onGranted() {
                proceed(args, point, new String[]{}, new String[]{}, allPermissions);
            }

            @Override
            public void onDenied(String[] grantedPermissions, String[] deniedPermissions, String[] dontAskAgainPermissions) {
                String[] gs = getGrantedPermissions(deniedPermissions, dontAskAgainPermissions, allPermissions);
                proceed(args, point, deniedPermissions, dontAskAgainPermissions, gs);
            }
        });
        fm.beginTransaction().add(smartFragment, TAG_FRAGMENT).commitAllowingStateLoss();
        fm.executePendingTransactions();
        smartFragment.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), 65535);
    }

    /**
     * 在所有权限中移除被拒绝的和不在询问的就是被允许的
     */
    private String[] getGrantedPermissions(String[] deniedPermissions, String[] dontAskAgainPermissions, String[] allPermissions) {
        String[] result = Arrays.copyOf(deniedPermissions, deniedPermissions.length + dontAskAgainPermissions.length);
        System.arraycopy(dontAskAgainPermissions, 0, result, deniedPermissions.length, dontAskAgainPermissions.length);
        List<String> grantedPermissionList = new ArrayList<>();
        List<String> allDeniedPermissionList = Arrays.asList(result);
        for (String current : allPermissions) {
            if (TextUtils.isEmpty(current)) {
                continue;
            }
            if (!allDeniedPermissionList.contains(current)) {
                grantedPermissionList.add(current);
            }
        }
        return grantedPermissionList.toArray(new String[grantedPermissionList.size()]);
    }

    public void proceed(Object[] args, ProceedingJoinPoint point,
                        String[] deniedPermissions, String[] dontAskAgainPermissions, String[] grantedPermissions) {
        try {
            SmartPermissionResult smartPermissionResult = null;
            if (args != null && args.length != 0) {
                for (Object arg : args) {
                    if (arg instanceof SmartPermissionResult) {
                        smartPermissionResult = (SmartPermissionResult) arg;
                        break;
                    }
                }
            }
            boolean allGranted = deniedPermissions.length == 0 && dontAskAgainPermissions.length == 0;//是否全部允许
            if (smartPermissionResult != null) {
                smartPermissionResult.setAllGranted(allGranted);
                smartPermissionResult.setGrantedPermissions(grantedPermissions);
                smartPermissionResult.setDeniedPermissions(deniedPermissions);
                smartPermissionResult.setDontAskAgainPermissions(dontAskAgainPermissions);
                point.proceed(args);
            } else {
                //这种情况只有在全部权限都被允许的情况下才会回调，只要有一个不被允许，强制跳转设置界面
                if (allGranted) {
                    point.proceed(args);
                } else {
                    Activity topActivity = SmartPermissionUtil.getInstance().getTopActivity();
                    if (topActivity != null) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + topActivity.getPackageName()));
                        topActivity.startActivity(intent);
                    }
                }
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
