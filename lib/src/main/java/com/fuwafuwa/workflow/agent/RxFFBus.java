package com.fuwafuwa.workflow.agent;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;


/**
 * Created by fred on 2016/11/12.
 */

public class RxFFBus {

    private static volatile RxFFBus mRxBus = null;
    private HashMap<String, CompositeDisposable> mSubscriptionMap;
    private final Subject<Object> mSubject;
    /**
     * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
     */
    private CompositeDisposable mRxBusObserverable = new CompositeDisposable();

    public static RxFFBus getBus() {
        if (mRxBus == null) {
            synchronized (RxFFBus.class) {
                if (mRxBus == null) {
                    mRxBus = new RxFFBus();
                }
            }
        }
        return mRxBus;
    }


    public <T> Flowable<T> toObservable(Class<T> eventType) {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER).ofType(eventType);
    }

    public RxFFBus() {
        mSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o) {
        mSubject.onNext(o);
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T> Flowable<T> getObservable(Class<T> type) {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }

    public <T> Flowable<T> getDropObservable(Class<T> type) {
        return mSubject.toFlowable(BackpressureStrategy.DROP)
                .ofType(type);
    }

    public <T> Flowable<T> getObservable(Class<T> type, BackpressureStrategy strategy) {
        return mSubject.toFlowable(strategy)
                .ofType(type);
    }

    /**
     * 一个默认的订阅方法
     *
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 保存订阅后的disposable
     *
     * @param o
     * @param disposable
     */
    public void addSubscription(Object o, Disposable disposable) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = o.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(disposable);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mSubscriptionMap.put(key, disposables);
        }
    }

    /**
     * 取消订阅
     *
     * @param o
     */
    public void unSubscribe(Object o) {
        if (mSubscriptionMap == null) {
            return;
        }

        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).dispose();
        }

        mSubscriptionMap.remove(key);
    }
}
