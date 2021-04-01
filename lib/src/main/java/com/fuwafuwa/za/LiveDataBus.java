package com.fuwafuwa.za;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.liveeventbus.core.Observable;

public class LiveDataBus {

    private final static String PREFIX = "sago_cloud_iot_";


    protected static <T> Observable<T> simplify(Class<T> clazz) {
        return LiveEventBus
                .get(PREFIX + clazz.getCanonicalName(), clazz);
    }

    protected static <T> Observable<T> simplify(Class<T> clazz, String name) {
        return LiveEventBus
                .get(name, clazz);
    }

    public static <T> void postRaw(Class<T> clazz, String name, T t) {
        simplify(clazz, name)
                .post(t);
    }


    public static <T> void post(Class<T> clazz, T t) {
        simplify(clazz)
                .post(t);
    }

    public static <T> void postDelay(Class<T> clazz, T t, long delay) {
        simplify(clazz)
                .postDelay(t, delay);
    }

    public static <T> void postSingle(Class<T> clazz, T t) {
        simplify(clazz)
                .post(t);
    }

    public static <T> void subscribe(Class<T> clazz, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        simplify(clazz).observe(owner, observer);
    }

    public static <T> void subscribeSticky(Class<T> clazz, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        simplify(clazz).observeSticky(owner, observer);
    }


    public static <T> void subscribeRaw(Class<T> clazz, String name, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        simplify(clazz, name).observe(owner, observer);
    }

    public static <T> void unsubscribeRaw(Class<T> clazz, String name, @NonNull Observer<T> observer) {
        simplify(clazz, name).removeObserver(observer);
    }
}
