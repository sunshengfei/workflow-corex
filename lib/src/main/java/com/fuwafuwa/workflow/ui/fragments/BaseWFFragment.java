package com.fuwafuwa.workflow.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fuwafuwa.presenter.composer.IBasePresenter;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.ui.acitivities.BaseActivity;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Created by fred on 2017/3/15.
 */

public abstract class BaseWFFragment<T extends IBasePresenter> extends Fragment {

    protected T mPresenter;
    protected Context mContext;
    protected View rootView;
    private boolean isViewPrepared; // 标识fragment视图已经初始化完毕
    private boolean hasFetchData; // 标识已经触发过懒加载数据

    protected Toolbar toolbar;

    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.mContext = mContext;
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayout(), container, false);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        initView(inflater);
        isViewPrepared = true;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyFetchDataIfPrepared();
        }
    }

    private void lazyFetchDataIfPrepared() {
        // 用户可见fragment && 没有加载过数据 && 视图已经准备完毕
        if (getUserVisibleHint()
//                && !hasFetchData
                && isViewPrepared) {
//            hasFetchData = true;
            lazyFetchData();
        }

    }

    public void loading(boolean isShow) {
        if (isShow) {
            ModalComposer.showLoading(mContext);
        } else {
            ModalComposer.hideLoading();
        }
    }

    /**
     * 懒加载的方式获取数据，仅在满足fragment可见和视图已经准备好的时候调用一次
     */
    protected void lazyFetchData() {
    }

    protected void initView(LayoutInflater inflater) {

    }

    protected void initEvent() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // view被销毁后，将可以重新触发数据懒加载，因为在viewpager下，fragment不会再次新建并走onCreate的生命周期流程，将从onCreateView开始
        hasFetchData = false;
        isViewPrepared = false;
        mPresenter = null;
        unSubscribe();
    }

    private CompositeDisposable mCompositeSubscription;

    public void subscribe(Disposable subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        mCompositeSubscription.add(subscription);
    }

    public void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
        }
    }


    public void ttTheme() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).ttTheme();
        }
    }

    public void toast(String message) {
        ModalComposer.showToast(message);
    }

    public void dialog(String message) {
        ModalComposer.showDialog(mContext, mContext.getString(R.string.dialog_title_info), message, null);
    }


    protected abstract int getLayout();


    protected void nextPage(Intent intent) {
        if (mContext != null)
            if (mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).nextPage(intent);
            }
    }

    protected void nextPage(Class clazz) {
        if (mContext != null)
            if (mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).nextPage(clazz);
            }
    }

    public boolean onKeyBack() {
        return false;
    }

    protected void exit() {
        if (getContext() != null && getContext() instanceof BaseActivity) {
            ((BaseActivity) getContext()).onBackPressed();
        }
    }

    public void uiControl(Task payload) {
        if (mContext != null)
            if (mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).uiControl(payload);
            }
    }
}
