package com.lantian.smartpermission.callback;

public interface PermissionRequestCallback {
    /**
     * 所有申请的权限都被允许
     */
    void onGranted();

    /**
     * 没有全部通过 （grantedPermissions+deniedPermissions+dontAskAgainPermissions）=一共申请的权限
     * @param grantedPermissions  通过了的权限
     * @param deniedPermissions   被拒绝的权限（不包括不再被询问的）
     * @param dontAskAgainPermissions 不再询问的权限
     */
    void onDenied(String[] grantedPermissions, String[] deniedPermissions, String[] dontAskAgainPermissions);
}
