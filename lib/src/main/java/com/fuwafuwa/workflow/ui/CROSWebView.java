package com.fuwafuwa.workflow.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by fred on 2017/9/24.
 */

public class CROSWebView extends WebView {
    public CROSWebView(Context context) {
        super(getFixedContext(context));
        init();
    }

    public CROSWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
        init();
    }

    public CROSWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
        init();
    }

    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        return context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        if (isInEditMode()) {
            return;
        }
        setWebChromeClient(new WebChromeClient() {
        });
        setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                try {
                    handler.proceed();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        WebSettings webSetting = this.getSettings();
        webSetting.setDisplayZoomControls(false);
        //解决issues # android.view.ThreadedRenderer.finalize() timed out after 10 seconds 或 设置android:hardwareAccelerated="true"
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
            }
        }
        webSetting.setSupportZoom(true);
//        ws.setDefaultZoom(ZoomDensity.CLOSE);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        //设置 缓存模式
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSetting.setAllowUniversalAccessFromFileURLs(true);
        }
        webSetting.setDefaultTextEncodingName("UTF-8");
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }


}
