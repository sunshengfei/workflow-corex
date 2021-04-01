package com.fuwafuwa.workflow.ui.fragments;

import com.fuwafuwa.presenter.composer.IBasePresenter;
import com.fuwafuwa.presenter.composer.IBaseView;

/**
 * Created by fred on 2017/3/15.
 */
public interface IShinoComposer {

    //    显示层回调
    interface View extends IBaseView<Presenter> {
    }

    //    保持层
    interface Presenter extends IBasePresenter<View> {
        //接口服务
    }

}
