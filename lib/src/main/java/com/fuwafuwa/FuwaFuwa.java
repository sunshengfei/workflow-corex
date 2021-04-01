package com.fuwafuwa;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.UIFrame;

public class FuwaFuwa {

    public static void init(Context context) {
        ModalComposer.initContext(context);
        initQbsdk(context, null);
    }

    public static void initWindow(Activity activity) {
        if (activity == null) return;
        WindowManager wm = activity.getWindowManager();
        if (wm == null) return;
        UIFrame.init(wm);
    }

    public static void initQbsdk(Context context, final Handler.Callback callback) {
        try {
            Class<?> clazz = Class.forName("com.tencent.smtt.sdk.QbSdk.PreInitCallback");
            com.tencent.smtt.sdk.QbSdk.PreInitCallback cb = new com.tencent.smtt.sdk.QbSdk.PreInitCallback() {
                @Override
                public void onViewInitFinished(boolean arg0) {
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Logger.d("app", " onViewInitFinished is " + arg0);
                    if (callback != null) {
                        Message message = new Message();
                        message.what = 0x0;
                        message.arg1 = arg0 ? 0 : 1;
                        callback.handleMessage(message);
                    }
                }

                @Override
                public void onCoreInitFinished() {
                    // TODO Auto-generated method stub
                }
            };
            //x5内核初始化接口
            com.tencent.smtt.sdk.QbSdk.initX5Environment(context.getApplicationContext(), cb);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
    }

}