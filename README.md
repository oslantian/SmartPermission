# SmartPermission

## 一行代码实现Android6.0权限请求
[![](https://jitpack.io/v/oslantian/SmartPermission.svg)](https://jitpack.io/#oslantian/SmartPermission)

## 引入
1. 根` build.gradle`添加
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
dependencies {
	classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
}
```
2. app的`build.gradle`添加
```
apply plugin: 'com.android.application'
apply plugin: 'com.hujiang.android-aspectjx'


dependencies {
	implementation 'com.github.oslantian:SmartPermission:1.0.0'
}
```

## 使用示例
1. 最简单的使用（只考虑权限申请成功的情况）
```
    @SmartPermission(Manifest.permission.CALL_PHONE)
    private void call(String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
```
2. 同时申请多个权限
```
    @SmartPermission({Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
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
```
权限请求结果封装在smartPermissionResult中
