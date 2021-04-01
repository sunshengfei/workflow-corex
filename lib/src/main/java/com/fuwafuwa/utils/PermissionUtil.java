package com.fuwafuwa.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * Created by fred on 2016/11/20.
 */
public class PermissionUtil {
    private static PermissionUtil ourInstance = new PermissionUtil();

    public static PermissionUtil getInstance() {
        return ourInstance;
    }

    private PermissionUtil() {
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(final @NonNull Activity activity,
                                  final @NonNull String[] permissions, final int requestCode) {

        ActivityCompat.requestPermissions(activity, permissions, requestCode);
//        activity.requestPermissions(permissions,
//                requestCode);
    }


    public boolean checkPermission(@NonNull Context context, @NonNull String permName) {
        PackageManager pkgManager = context.getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        return pkgManager.checkPermission(permName, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;

    }


    public boolean isSdcardRW(@NonNull Context context) {
        PackageManager pkgManager = context.getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        return pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;

    }

    public boolean isPhoneStateAccess(@NonNull Context context) {
        PackageManager pkgManager = context.getPackageManager();
        // read phone state用于获取 imei 设备信息
        return pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isWriteExternalStorage(@NonNull Context context) {
        PackageManager pkgManager = context.getPackageManager();
        // read phone state用于获取 imei 设备信息
        return pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
}
