package com.fuwafuwa.presenter.composer;


import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;

import java.util.List;

/**
 * Created by fred on 2017/3/15.
 */
public interface IMQTTProfilesComposer {
    //    显示层回调
    interface View extends IBaseView<Presenter> {
        void $data(List<MQTTConnectUserEntity> list);

        void $deleteOk();

        void connectChange(String id, ConnectActionState connected);
    }

    //    保持层
    interface Presenter extends IBasePresenter<View> {
        void subscribeState();

        //接口服务
        void apply();

        void delete(List<String> list);

        void communicate(MQTTConnectUserEntity item, boolean isNeedConnect);

        void duplicate(List<MQTTConnectUserEntity> list);

        void applyInsertMqtt(MQTTConnectUserEntity obj);
    }
}
