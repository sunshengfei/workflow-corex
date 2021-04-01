package com.fuwafuwa.presenter;

import com.fuwafuwa.za.ThrowableMessage;
import com.fuwafuwa.presenter.composer.IMQTTDBComposer;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.mqtt.db.MQTTProfileDBManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by fred on 2018/5/10.
 */

public class MQTTDBPresenter extends RxJavaMiddleWare<IMQTTDBComposer.View> implements IMQTTDBComposer.Presenter {

    private MQTTProfileDBManager mqttProfileDBManager;

    public MQTTDBPresenter(IMQTTDBComposer.View mView) {
        this.attachView(mView);
        if (mAppContext != null)
            mqttProfileDBManager = new MQTTProfileDBManager(mAppContext);
    }

    @Override
    public void apply(int actionType, MQTTConnectUserEntity payload) {
        if (actionType == IMQTTDBComposer.ActionType.queryById) {
            if (mqttProfileDBManager != null)
                subscribe(mqttProfileDBManager
                        .select(payload)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            mView.$data(data);
                        }, e -> {
                            mView.toast(ThrowableMessage.composer(e).getMsg());
                        }));
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mqttProfileDBManager != null) {
            mqttProfileDBManager = null;
        }
    }
}
