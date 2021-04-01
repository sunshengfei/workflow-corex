package com.fuwafuwa.sys.ble;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

public class RxBLE {

   public static void scanDevice(@NonNull Context context){
       new Thread(new Runnable() {
           @Override
           public void run() {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   new BLEConnector(context).log();
               }
           }
       }).start();
   }
}
