package com.fuwafuwa.workflow.agent;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by fred on 2016/12/1.
 */

public class RxEventBus {


    /**
     * 订阅原子事件
     *
     * @param userEventClass
     * @param <T>
     * @return Observable
     */
    public static <T> Flowable<T> prepareObservable(Class<T> userEventClass) {
        return RxFFBus.getBus()
                .toObservable(userEventClass)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static <T> Flowable<T> prepareDropFlowable(Class<T> userEventClass) {
        return RxFFBus.getBus()
                .getDropObservable(userEventClass)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public static <T> Flowable<T> prepareDBObservable(Class<T> userEventClass) {
        return RxFFBus.getBus()
                .toObservable(userEventClass)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

    }


    /**
     * 订阅原子事件
     *
     * @param userEventClass
     * @param <T>
     * @return Subscription
     */
    public static <T> Disposable subscribeEvent(Class<T> userEventClass, Consumer<T> action1, Consumer<Throwable> throwable) {
        return prepareObservable(userEventClass).subscribe(action1, throwable);

    }

    public static <T> Disposable subscribeDropEvent(Class<T> userEventClass, Consumer<T> action1, Consumer<Throwable> throwable) {
        return prepareDropFlowable(userEventClass).subscribe(action1, throwable);

    }

    public static <T> Disposable subscribeIOEvent(Class<T> userEventClass, Consumer<T> action1, Consumer<Throwable> throwable) {
        return prepareDBObservable(userEventClass).subscribe(action1, throwable);

    }


    /**
     * 取消订阅
     *
     * @param subscription
     */
    public static void unsubscribeEvent(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }


    /**
     * 发送原子
     *
     * @param event
     */
    public static void post(Object event) {
        if (event == null) return;
        RxFFBus.getBus().post(event);
    }
}
