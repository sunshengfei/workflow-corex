package com.fuwafuwa.mqtt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.mqtt.broker.EmqttdBroker;
import com.fuwafuwa.mqtt.broker.PahoMosquittoBroker;
import com.fuwafuwa.mqtt.event.MQTTStateEvent;
import com.fuwafuwa.za.ActionEventType;
import com.fuwafuwa.za.LiveDataBus;
import com.fuwafuwa.za.MQTTClientActionEvent;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MQTTService extends Service implements LifecycleOwner, LifecycleObserver {

    public final static String CONN = "CON_MQTT_CF";


    private List<MQTTMessage> cachedSubscriptionTopics;
    private String cachedSubscriptionId;
    private MQTTConnectUserEntity connectPoint;
    private IMQTTBrokerDelegate mqttBrokerDelegate;
    private LifecycleRegistry mLifecycleRegistry;
    private final AtomicBoolean locker = new AtomicBoolean();
    private boolean isTerminal;

    public static void startService(Context context, MQTTConnectUserEntity point) {
        Intent service = new Intent();
        service.setClass(context, MQTTService.class);
        service.putExtra(CONN, point);
        context.startService(service);
    }

    public static void stopService(Context context) {
        Intent service = new Intent();
        service.setClass(context, MQTTService.class);
        context.stopService(service);
    }

    private void $prepareActionHandler() {
        LiveDataBus.subscribe(MQTTClientActionEvent.class, this, event -> {
            ActionEventType type = event.getEventType();
            Object payload = event.getPayload();
            isTerminal = false;
            switch (type) {
                case connect:
                    mqttBrokerDelegate.connect();
                    break;
                case disconnect:
                    mqttBrokerDelegate.disconnect();
                    break;
                case publish:
                    if (payload instanceof MQTTMessage) {
                        mqttBrokerDelegate.publish((MQTTMessage) payload);
                    }
                    break;
                case subscribe:
                    if (payload instanceof MQTTMessage) {
                        MQTTMessage subscription = (MQTTMessage) payload;
                        try {
                            mqttBrokerDelegate.subscribe(subscription);
                        }catch (Exception e){
                            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), ConnectActionState.DISCONNECTED));
                        }
                        if (cachedSubscriptionTopics == null) {
                            cachedSubscriptionTopics = new ArrayList<>();
                        }
                        Long count = Stream.of(cachedSubscriptionTopics)
                                .filter(item -> subscription.getTopic().equalsIgnoreCase(item.getTopic()))
                                .collect(Collectors.counting());
                        if (count > 0) {
                            return;
                        }
                        cachedSubscriptionTopics.add(subscription);
                    }
                    break;
                case unsubscribe:
                    if (payload instanceof MQTTMessage) {
                        mqttBrokerDelegate.unsubscribe(((MQTTMessage) payload).getTopic());
                    }
                    break;
                case unsubscribe_all:
                    if (payload == null) {
                        mqttBrokerDelegate.unsubscribeAll(null);
                    } else if (payload instanceof String[]) {
                        mqttBrokerDelegate.unsubscribeAll((String[]) payload);
                    }
                    break;
                case terminal:
                    isTerminal = true;
                case close:
                    mqttBrokerDelegate.disconnect();
                    mqttBrokerDelegate.onDestroy();
                    stopSelf();
                    break;
            }

        });
    }

    @Override
    public void onDestroy() {
        try {
            if (mqttBrokerDelegate == null) return;
            mqttBrokerDelegate.disconnect();
            mqttBrokerDelegate.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
            mqttBrokerDelegate = null;
        } finally {
            if (connectPoint != null) {
                connectPoint.setConnected(ConnectActionState.DISCONNECTED);
                if (!isTerminal)
                    LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            MQTTConnectUserEntity locConn = (MQTTConnectUserEntity) intent.getSerializableExtra(CONN);
            if (locConn != null) {
                if (connectPoint != null && connectPoint.get_id().equals(locConn.get_id())) {
                    if (mqttBrokerDelegate != null && !mqttBrokerDelegate.isConnected()) {
                        mqttBrokerDelegate.connect();
                        return super.onStartCommand(intent, flags, startId);
                    }
                }
                connectPoint = locConn;
                if (!connectPoint.get_id().equals(cachedSubscriptionId)) {
                    cachedSubscriptionTopics = null;
                }
                cachedSubscriptionId = connectPoint.get_id();
                synchronized (locker) {
                    locker.set(false);
                    $prepareActionHandler();
                    locker.set(true);
                }
//                String client = SPBase.builder(this).getString(SPKey.MQTT_CLIENT, "");
                if ("Emqx".equals(connectPoint.getBrokerType())) {
                    mqttBrokerDelegate = new EmqttdBroker(this, connectPoint);
                } else {
                    mqttBrokerDelegate = new PahoMosquittoBroker(this, connectPoint);
                }
//                connectPoint.setConnected(ConnectActionState.CONNECTING);
//                LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
                mqttBrokerDelegate.connect();
            } else {
                if (connectPoint != null) {
                    if (!connectPoint.get_id().equals(cachedSubscriptionId)) {
                        cachedSubscriptionTopics = null;
                    }
                    if (mqttBrokerDelegate != null && !mqttBrokerDelegate.isConnected()) {
                        mqttBrokerDelegate.connect();
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if (mLifecycleRegistry == null) {
            mLifecycleRegistry = new LifecycleRegistry(this);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
            mLifecycleRegistry.addObserver(this);
        }
//        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        return mLifecycleRegistry;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onLiveCreate() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        Loger.d("LiveCycle-MQTTService", "onLiveCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onLiveResume() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        mqttBrokerDelegate.onResume();
        Loger.d("LiveCycle-MQTTService", "onLiveResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onLivPause() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        mqttBrokerDelegate.onPause();
        Loger.d("LiveCycle-MQTTService", "onLivPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onLiveDestroy() {
        Loger.d("LiveCycle-MQTTService", "onLiveDestroy");
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (mLifecycleRegistry != null) {
            mLifecycleRegistry.removeObserver(this);
        }
    }


}
