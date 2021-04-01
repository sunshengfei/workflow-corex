package com.fuwafuwa.utils;

import android.os.Build;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fred on 2016/11/2.
 */
public class PlatformApiTools {

    /**
     * 获取map
     *
     * @return
     */
    public static <T, M> Map<T, M> getMap(Class<T> tClass, Class<M> tClass2) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return new ArrayMap<>();
        } else {
            return new HashMap<>();
        }
    }

    public static <T, M> Map<T, M> getOrderedMap(Class<T> tClass, Class<M> tClass2) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return new ArrayMap<>();
        } else {
            return new LinkedHashMap<>();
        }
    }
}
