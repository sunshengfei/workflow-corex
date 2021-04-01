package com.fuwafuwa.presenter;


import com.fuwafuwa.za.ActionEventType;
import com.fuwafuwa.za.LiveDataBus;
import com.fuwafuwa.za.MQTTClientActionEvent;
import com.fuwafuwa.za.ThrowableMessage;
import com.fuwafuwa.presenter.composer.IMQTTProfilesComposer;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.mqtt.MQTTService;
import com.fuwafuwa.mqtt.db.MQTTProfileDBManager;
import com.fuwafuwa.mqtt.event.MQTTStateEvent;
import com.fuwafuwa.workflow.agent.WorkFlowItemDelegate;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by fred on 2018/5/10.
 */

public class MQTTProfilesPresenter extends RxJavaMiddleWare<IMQTTProfilesComposer.View> implements IMQTTProfilesComposer.Presenter {

    private MQTTProfileDBManager mqttProfileDBManager;

    public MQTTProfilesPresenter(IMQTTProfilesComposer.View mView) {
        this.attachView(mView);
        if (mAppContext != null) {
            mqttProfileDBManager = new MQTTProfileDBManager(mAppContext);
        }
    }


    @Override
    public void subscribeState() {
        LiveDataBus.subscribe(MQTTStateEvent.class, this, event -> {
            mView.connectChange(event.get_id(), event.getIsConnected());
        });
    }

    @Override
    public void apply() {
        if (mAppContext == null) return;
        mView.loading(true);
        subscribe(mqttProfileDBManager
                .query(0, Integer.MAX_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pager -> {
                    mView.$data(pager.getData());
                }, e -> {
                    mView.loading(false);
                    mView.toast(ThrowableMessage.composer(e).getMsg());
                }));
    }

    @Override
    public void delete(List<String> list) {
        if (list == null) return;
        if (mAppContext == null) return;
        subscribe(mqttProfileDBManager.deleteByPrimaryKeys(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pager -> {
                    mView.$deleteOk();
                }, e -> {
                    mView.toast(ThrowableMessage.composer(e).getMsg());
                }));
    }

    @Override
    public void communicate(MQTTConnectUserEntity entryPoint, boolean isNeedConnect) {
        if (isNeedConnect) {
            MQTTService.startService(mAppContext, entryPoint);
        } else {
            MQTTClientActionEvent send = new MQTTClientActionEvent<>();
            send.setEventType(ActionEventType.close);
            LiveDataBus.post(MQTTClientActionEvent.class, send);
        }
    }

    @Override
    public void duplicate(List<MQTTConnectUserEntity> fileList) {
        if (mAppContext == null) return;
        mView.loading(true);
        subscribe(Flowable.fromIterable(fileList)
                .observeOn(Schedulers.io())
                .flatMap(new Function<MQTTConnectUserEntity, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(MQTTConnectUserEntity vor) throws Exception {
                        MQTTConnectUserEntity vo = (MQTTConnectUserEntity) vor.clone();
                        String rowId = WorkFlowItemDelegate.getUUID();
                        vo.set_id(rowId);
                        vo.setSelected(false);
                        vo.setProfileName(vo.getProfileName() + " Copy");
                        return mqttProfileDBManager.insert(vo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(list -> {
                    mView.loading(false);
                    apply();
                }, throwable -> {
                    mView.loading(false);
                    mView.toast(ThrowableMessage.composer(throwable).getMsg());
                }));
    }

    @Override
    public void applyInsertMqtt(MQTTConnectUserEntity payload) {
        if (mAppContext == null) return;
        if (mqttProfileDBManager == null) {
            mqttProfileDBManager = new MQTTProfileDBManager(mAppContext);
        }
        subscribe(mqttProfileDBManager.select(payload)
                .flatMap((Function<MQTTConnectUserEntity, Flowable<Boolean>>) mqttConnectUserEntity -> {
                    payload.set_id(MQTTConnectUserEntity.idGenerator());
                    return mqttProfileDBManager.insert(payload);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(n -> {
                    mView.toast(mAppContext.getString(R.string.import_success));
                    apply();
                }, e -> {
                    mView.toast(ThrowableMessage.composer(e).getMsg());
                }));
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mqttProfileDBManager != null) {
            mqttProfileDBManager = null;
        }
    }
}
