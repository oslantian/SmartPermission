package com.lantian.smartpermission.bean;

/**
 * （grantedPermissions+deniedPermissions+dontAskAgainPermissions）=一共申请的权限
 * <p>
 * result
 */
public class SmartPermissionResult {
    private boolean allGranted;
    //被拒绝的权限（不包括不再被询问的）
    private String[] deniedPermissions;
    //不再询问的权限
    private String[] dontAskAgainPermissions;
    //通过了的权限
    private String[] grantedPermissions;

    public String[] getDeniedPermissions() {
        return deniedPermissions;
    }

    public void setDeniedPermissions(String[] deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    public String[] getDontAskAgainPermissions() {
        return dontAskAgainPermissions;
    }

    public void setDontAskAgainPermissions(String[] dontAskAgainPermissions) {
        this.dontAskAgainPermissions = dontAskAgainPermissions;
    }

    public String[] getGrantedPermissions() {
        return grantedPermissions;
    }

    public void setGrantedPermissions(String[] grantedPermissions) {
        this.grantedPermissions = grantedPermissions;
    }

    public boolean isAllGranted() {
        return allGranted;
    }

    public void setAllGranted(boolean allGranted) {
        this.allGranted = allGranted;
    }
}
