package com.fuwafuwa.presenter;

import com.fuwafuwa.za.ThrowableMessage;
import com.fuwafuwa.presenter.composer.IMQTTProfileComposer;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.mqtt.db.MQTTProfileDBManager;
import com.fuwafuwa.utils.RegexHelper;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by fred on 2018/5/10.
 */

public class MQTTProfilePresenter extends RxJavaMiddleWare<IMQTTProfileComposer.View> implements IMQTTProfileComposer.Presenter {

    private MQTTProfileDBManager mqttProfileDBManager;

    public MQTTProfilePresenter(IMQTTProfileComposer.View mView) {
        this.attachView(mView);
        if (mAppContext != null)
            mqttProfileDBManager = new MQTTProfileDBManager(mAppContext);
    }

    @Override
    public void apply(MQTTConnectUserEntity payload) {
        if (mqttProfileDBManager == null) return;
        String rawID = payload.get_id();
        subscribe(mqttProfileDBManager.select(payload)
                .flatMap((Function<MQTTConnectUserEntity, Flowable<Boolean>>) mqttConnectUserEntity -> {
                    if (mqttConnectUserEntity != null && !RegexHelper.isEmpty(mqttConnectUserEntity.get_id())) {
//                        try {
//                            String text = GsonUtils.toJson(mqttConnectUserEntity);
//                            String dir = Const.SANDBOX_WORKFLOW_PROFILES_TEMPLATE;
//                            FileUtil.mkdir(dir);
//                            FileUtil.rewriteFile(new File(dir, rawID+"___v.txt")
//                                    ,text);
//                        } catch (Exception e) {
//                        }
                        payload.set_id(mqttConnectUserEntity.get_id());
                        return mqttProfileDBManager
                                .delete(mqttConnectUserEntity)
                                .flatMap((Function<Boolean, Flowable<Boolean>>) aBoolean -> mqttProfileDBManager.insert(payload));
                    }
                    payload.set_id(MQTTConnectUserEntity.idGenerator());
                    return mqttProfileDBManager.insert(payload);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(n -> {
                    if (rawID != null) {
                        mView.toast("修改成功");
                    } else {
                        mView.toast("添加成功");
                    }
                    mView.$success();
                }, e -> {
                    mView.loading(false);
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
