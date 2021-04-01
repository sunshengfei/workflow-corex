package com.fuwafuwa.workflow.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.fuwafuwa.dependences.WebView;
import com.fuwafuwa.utils.MimeTypes;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.ui.CROSWebView;

public class SimpleWebViewFragment extends BaseWFFragment<IShinoComposer.Presenter> implements IShinoComposer.View {

    private static final String URL = "URL";
    private static final String MIME = "MIME";

    protected Context mContext;

    public static SimpleWebViewFragment newInstance(Uri uri, String mimeType) {
        SimpleWebViewFragment fragment = new SimpleWebViewFragment();
        fragment.setArguments(getBundle(uri, mimeType));
        return fragment;
    }

    public static Bundle getBundle(Uri uri, String mimeType) {
        Bundle args = new Bundle();
        args.putParcelable(URL, uri);
        if (mimeType != null)
            args.putString(MIME, mimeType);
        return args;
    }

    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.mContext = mContext;
    }

    public int getLayout() {
        return WebView.hasX5() ? R.layout.x5_webview : R.layout.default_webview;
    }


    private String videoSlot = "<video style='width:100%' controls=\"controls\">\n" +
            "<slot></slot>" +
            "Your browser does not support the video tag.\n" +
            "</video>";


    public void initView(LayoutInflater inflater) {
        if (WebView.hasX5()) {
            com.fuwafuwa.webview.X5WebView x5webView = rootView.findViewById(R.id.x5webView);
            com.tencent.smtt.sdk.WebSettings webSetting = x5webView.getSettings();
            webSetting.setAllowFileAccess(true);
            webSetting.setSupportZoom(false);
            webSetting.setUseWideViewPort(false);
            webSetting.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            x5webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
            if (getArguments() != null) {
                Uri url = getArguments().getParcelable(URL);
                String mimeType = getArguments().getString(MIME);
                if (url != null) {
                    if (MimeTypes.isVideo(mimeType)) {
                        x5webView.loadData(videoSlot.replace("<slot></slot>", "<source src=\"" + url.toString() + "\" type=\"" + mimeType + "\">"), "text/html", "utf-8");
                    } else {
                        x5webView.loadUrl(url.toString());
                    }
                }
            }
        } else {
            toast("这个");
            CROSWebView webView = rootView.findViewById(R.id.webView);
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 7.1.1; OPPO R11 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/63.0.3239.83 Mobile Safari/537.36 T7/10.13 baiduboxapp/10.13.0.11 (Baidu; P1 7.1.1)");
            if (getArguments() != null) {
                Uri url = getArguments().getParcelable(URL);
                String mimeType = getArguments().getString(MIME);
                if (url != null) {
                    if (MimeTypes.isVideo(mimeType)) {
                        webView.loadData(videoSlot.replace("<slot></slot>", "<source src=\"" + url.toString() + "\" type=\"" + mimeType + "\">"), "text/html", "utf-8");
                    } else {
                        webView.loadUrl(url.toString());
                    }
                }
            }
        }

    }

    public void initEvent() {

    }

    @Override
    public void setPresenter(IShinoComposer.Presenter presenter) {
        mPresenter = presenter;
    }
}
