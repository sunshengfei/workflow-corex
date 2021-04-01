package com.fuwafuwa.utils;


import android.content.Context;

import com.fuwafuwa.sys.ss.InputServer;
import com.fuwafuwa.workflow.agent.RxEventBus;

import java.io.Serializable;
import java.util.Locale;

public class InputConnect {
    static InputConnect instance;

    private InputServer mServer;

    private InputConnect() {
    }


    public static InputConnect INSTANCE() {
        if (instance == null) {
            synchronized (InputServer.class) {
                instance = new InputConnect();
            }
        }
        return instance;
    }

    public void startServer(Context context) {
        if (mServer == null) {
            mServer = new InputServer(context.getApplicationContext(), (path, jsonObject) -> {
                RxEventBus.post(new InputAsyncBean(jsonObject.optString("js")));
                return false;
            });
            NetSuit.NetInfo netInfo = NetSuit.getNetInfo(context, null);
            mServer.createServer();
            if (netInfo != null) {
                String f = String.format(Locale.ENGLISH, "电脑浏览器打开%s:%d，开启远程编辑\n关闭窗口后同步服务将会关闭", netInfo.localIp, mServer.port);
                ModalComposer.showToast(f);
            } else {
                ModalComposer.showToast("服务开启失败，请确认是否连入局域网");
            }
        }
    }

    public void stopServer() {
        if (mServer != null) {
            mServer.shutDown();
        }
    }


    public static class InputAsyncBean implements Serializable {
        public String input;

        public InputAsyncBean(String input) {
            this.input = input;
        }
    }
}
