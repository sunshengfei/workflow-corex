package com.fuwafuwa.presenter.composer;


import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;

/**
 * Created by fred on 2017/3/15.
 */
public interface IMQTTProfileComposer {

    //    显示层回调
    interface View extends IBaseView<Presenter> {
        void $success();
    }

    //    保持层
    interface Presenter extends IBasePresenter<View> {
        //接口服务
        void apply(MQTTConnectUserEntity payload);
    }
}
