package com.fuwafuwa.presenter;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;

import com.fuwafuwa.za.TTColorEvent;
import com.fuwafuwa.presenter.composer.IBasePresenter;
import com.fuwafuwa.presenter.composer.IBaseView;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.workflow.agent.RxEventBus;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;


/**
 * Created by fred on 2016/11/2.
 */

public class RxJavaMiddleWare<T extends IBaseView> implements IBasePresenter<T>, LifecycleOwner {

    protected T mView;
    private CompositeDisposable mCompositeSubscription;
    private LifecycleRegistry mLifecycleRegistry;
    protected Context mAppContext;

    @Override
    public void subscribe(Disposable subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
        }
    }


    @Override
    public void attachView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
        if (mView instanceof View) {
            mAppContext = ((View) mView).getContext();
        } else if (mView instanceof Fragment) {
            mAppContext = ((Fragment) mView).getContext();
        } else if (mView instanceof android.app.Fragment) {
            mAppContext = ((android.app.Fragment) mView).getActivity();
        } else if (mView instanceof ContextWrapper) {
            mAppContext = ((ContextWrapper) mView).getBaseContext();
        }
        if (mAppContext != null) {
            mAppContext = mAppContext.getApplicationContext();
        }
        subscribeThemeChange();
    }


    @Override
    public void detachView() {
        unSubscribe();
        this.mView = null;
    }

    protected void subscribeThemeChange() {
        subscribe(RxEventBus.subscribeEvent(TTColorEvent.class,
                ttColorEvent -> {
                    if (mView != null)
                        mView.ttTheme();
                }, throwable -> {

                }));
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if (mView instanceof Fragment) {
            return ((Fragment) mView).getLifecycle();
        }
        if (mView instanceof AppCompatActivity) {
            return ((AppCompatActivity) mView).getLifecycle();
        }
        if (mView instanceof View) {
            Context context = ((View) mView).getContext();
            if (context instanceof AppCompatActivity) {
                return ((AppCompatActivity) context).getLifecycle();
            }
        }
        if (mLifecycleRegistry == null)
            mLifecycleRegistry = new LifecycleRegistry(this);
        return mLifecycleRegistry;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onLiveCreate() {
        if (mLifecycleRegistry != null)
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        Loger.d("LiveCycle-RxJavaMiddleWare", "onLiveCreate");
    }

    // region : @fred 公共方法 [2017/1/10]

//    protected void setActionNeedLogin() {
//        if (!App.getInstance().isNotShowAuth) {
//            Subscription authSubscription = RxEventBus.prepareObservable(AuthEvent.class)
//                    .subscribe(new Action1<AuthEvent>() {
//                        @Override
//                        public void call(AuthEvent authEvent) {
//                            needLoginCalled(authEvent);
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//
//                        }
//                    });
//            subscribe(authSubscription);
//        }
//    }


    // endregion
}
