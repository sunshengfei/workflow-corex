package com.fuwafuwa.utils;

/**
 * Created by fred on 2018/8/5.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by fred on 2016/12/2.
 */
public class RequestBodyMaker {

    public static final List<String> methods = new ArrayList<>(Arrays.asList("GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"));


    public static RequestBody getJSONBody(Object map) {
        String jsonStr = GsonUtils.toJson(map);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonStr);
    }


    public static RequestBody getFormBody(Map<String, String> map) {
        if (RegexHelper.isEmpty(map)) return null;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public static boolean isNoBodyMethod(String methodStr) {
        if (methodStr == null) return true;
        return methods.indexOf(methodStr.toUpperCase()) < 3;
    }

    public static boolean isValidMethod(String method) {
        if (RegexHelper.isEmpty(method)) return false;
        return methods.contains(method.toUpperCase());
    }
}
