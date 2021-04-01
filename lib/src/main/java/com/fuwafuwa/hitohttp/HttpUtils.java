package com.fuwafuwa.hitohttp;


import android.content.Context;

import com.fuwafuwa.hitohttp.model.HttpRequest;
import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.RequestBodyMaker;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by fred on 2018/5/8.
 */

public class HttpUtils {

    private static HttpUtils instance;
    private SoftReference<Context> mContextRef;

    public static HttpUtils getInstance(SoftReference<Context> mContextRef) {
        instance = getInstance();
        instance.mContextRef = mContextRef;
        instance.okHttpClient = RetrofitBuilder.initOkHttp(mContextRef);
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private OkHttpClient okHttpClient;


    private HttpUtils() {
    }

    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
        }
        return instance;
    }


    public void request(HttpRequest request, Callback callback) {
        HashMap<String, String> headers = request.getHeaders();
        if (headers == null) {
            headers = new HashMap<>();
        }
        String type = headers.get("Content-Type");
        if (type == null) {
            type = "application/json";
        }
        MediaType contentType = MediaType.get(type);
        String body = request.getBody();
        RequestBody requestBody = null;
        if (RequestBodyMaker.isNoBodyMethod(request.getMethod())) {
            requestBody = null;
        } else {
            if (RegexHelper.isNotEmpty(body)) {
                if ("x-www-form-urlencoded".equalsIgnoreCase(contentType.subtype())) {
                    if (body.startsWith("[") || body.startsWith("{")) {
                        body = nameValuePair(body);
                    }
                } else if ("json".equalsIgnoreCase(contentType.subtype())) {

                }
                requestBody = RequestBody.create(contentType, body);
            } else {
                requestBody = RequestBody.create(contentType, "");
            }

        }

        Request.Builder reqBuilder = new Request.Builder()
                .url(request.getUrl())
                .method(request.getMethod(), requestBody);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Call call = okHttpClient.newCall(reqBuilder.build());
        call.enqueue(callback);
    }

    private String nameValuePair(String body) {
        try {
            HashMap<String, String> nameValuePairHash = GsonUtils.parseJson(body, HashMap.class);
            Set<Map.Entry<String, String>> set = nameValuePairHash.entrySet();
            StringBuilder strings = new StringBuilder();
            for (Map.Entry<String, String> entry : set) {
                strings.append(entry.getKey()).append("=").append(entry.getValue());
                strings.append("&");
            }
            if (strings.length() > 0) {
                strings = strings.deleteCharAt(strings.length() - 1);
            }
            return strings.toString();
        } catch (Exception e) {
            return body;
        }
    }
}
