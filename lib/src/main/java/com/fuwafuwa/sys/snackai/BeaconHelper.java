package com.fuwafuwa.sys.snackai;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BeaconHelper {
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    public CallBack callBack;

    public interface CallBack {
        void onGranted(boolean isGranted);
    }

    public static boolean isSupported(Context context) {
        if (context == null) return false;
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isHasPermisson(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(@NonNull final AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 29) {
                    context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
//                                    ,Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            },
                            PERMISSION_REQUEST_FINE_LOCATION);
                } else {
                    context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                }
            } else {
//                if (context.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("温馨提示");
//                    builder.setMessage("如果需要开启Beacon设备后台发现，请打开本权限。");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            if (Build.VERSION.SDK_INT >= 29) {
//                                context.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                                        PERMISSION_REQUEST_BACKGROUND_LOCATION);
//                            }
//                        }
//                    });
//                    builder.show();
//                }
                if (callBack != null) {
                    callBack.onGranted(true);
                }
            }

        }
    }

    public void onRequestPermissionsResult(@NonNull Context context, int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (callBack != null) {
                        callBack.onGranted(true);
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("功能限制");
                    builder.setMessage("关闭定位权限将无法使用基于蓝牙的相关功能，比如发现Beacon、开启Beacon信标");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (callBack != null) {
                                callBack.onGranted(false);
                            }
                        }

                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (callBack != null) {
                        callBack.onGranted(true);
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("功能限制");
                    builder.setMessage("关闭此权限，应用运行在后台将无法发现任何Beacon设备");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (callBack != null) {
                                callBack.onGranted(false);
                            }
                        }
                    });
                    builder.show();
                }
            }
        }
    }


    public static String enumOfDistance(double distance) {
        if (distance < 0.01) return "Immediate";
        if (distance < 1) {
            return "Near";
        }
        return "Far";
    }

}