package com.fuwafuwa.hitohttp;


import android.content.Context;

import com.fuwafuwa.hitohttp.retrofit.StringGsonFactory;
import com.fuwafuwa.workflow.BuildConfig;
import com.fuwafuwa.utils.NetSuit;
import com.fuwafuwa.utils.PlatformApiTools;
import com.fuwafuwa.utils.RegexHelper;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

/**
 * 引用自 小飞博客
 * Created by fred on 2016/11/2.
 */
public class RetrofitBuilder {

    private static OkHttpClient okHttpClient;

    public static <T> T build(Class<T> service) {
        return build(service, "http://www.example.com");
    }

    public static <T> T build(Class<T> service, String baseUrl) {
        initOkHttp(null);
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(StringGsonFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
                .build();
        return retrofit.create(service);
    }


    /**
     * 初始化okhttp对象
     *
     * @param mContextRef
     * @return
     */
    public static OkHttpClient initOkHttp(SoftReference<Context> mContextRef) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
            }
            Context context = mContextRef.get();
            if (context != null) {
                String pathCache = context.getCacheDir() + File.separator + "eCache";
                File cacheFile = new File(pathCache);
                Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
                Interceptor cacheInterceptor = chain -> {
                    Request request = chain.request();
                    if (!NetSuit.checkEnable(context)) {
                        Request.Builder rBuilder = request.newBuilder()
                                .cacheControl(CacheControl.FORCE_NETWORK);
                        Map<String, String> headers = getHeaders();
                        if (!RegexHelper.isEmpty(headers)) {
                            for (Map.Entry<String, String> entry : headers.entrySet()) {
                                rBuilder.addHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        request = rBuilder.build();
                    } else {
                        Request.Builder rBuilder = request.newBuilder();
                        Map<String, String> headers = getHeaders();
                        if (!RegexHelper.isEmpty(headers)) {
                            for (Map.Entry<String, String> entry : headers.entrySet()) {
                                rBuilder.addHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        request = rBuilder.build();
                    }
                    int tryCount = 0;
                    Response response = chain.proceed(request);
                    try {
                        while (!response.isSuccessful() && tryCount < 1) {
                            tryCount++;
                            response.close();
                            response = chain.proceed(request);
                        }
                        if (!NetSuit.checkEnable(context)) {
                            int maxAge = 60 * 60;
                            // 有网络时, 不缓存, 最大保存时长为0
                            response.newBuilder()
                                    .header("Cache-Control", "public, max-age=" + maxAge)
                                    .removeHeader("Pragma")
                                    .build();
                        }
                    } catch (Exception e) {
                    }
                    return response;
                };
                //设置缓存
                builder.addInterceptor(cacheInterceptor);
                builder.cache(cache);
            }
            //设置超时
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);
            //错误重连
            builder.retryOnConnectionFailure(true);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }


    /**
     * 公用请求头
     *
     * @return
     */
    public static Map<String, String> getHeaders() {
        Map<String, String> map = PlatformApiTools.getMap(String.class, String.class);
//        map.put("Env", EnvConfig.getDeviceHeadEnv());
        return map;
    }


}
