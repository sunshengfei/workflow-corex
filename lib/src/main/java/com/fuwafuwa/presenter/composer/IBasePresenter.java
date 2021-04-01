package com.fuwafuwa.presenter.composer;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 公共的保持层协议
 * Created by fred on 2017/3/15.
 */

public interface IBasePresenter<V> {

    //Presenter的绑定操作
    void attachView(V view);

    //Presenter的解绑操作
    void detachView();

    void subscribe(Disposable subscription);

    void unSubscribe();

}
