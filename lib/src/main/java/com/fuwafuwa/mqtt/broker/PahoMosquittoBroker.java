package com.fuwafuwa.mqtt.broker;

import android.content.Context;

import androidx.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fuwafuwa.mqtt.IMQTTBrokerDelegate;
import com.fuwafuwa.mqtt.event.MQTTStateEvent;
import com.fuwafuwa.mqtt.event.MQTTTraceEvent;
import com.fuwafuwa.za.LiveDataBus;
import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.NetSuit;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.StringUtils;
import com.fuwafuwa.workflow.agent.event.MQTTMessageEvent;
import com.fuwafuwa.workflow.plugins.mqtt.payload.EventType;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class PahoMosquittoBroker implements IMQTTBrokerDelegate {


    private Context context;
    private MqttAndroidClient mqttAndroidClient;
    private MQTTMessageEvent mQTTConnectEvent;
    private MQTTConnectUserEntity connectPoint;
    private MqttConnectOptions mMqttConnectOptions;
    private boolean isServiceActive = true;
    private Disposable actTimerSubscription;

    private final AtomicBoolean locker = new AtomicBoolean();

    private ExecutorService singleSender;
    private boolean shutDown;
    private Disposable disposter;

    public PahoMosquittoBroker(Context context, MQTTConnectUserEntity connectPoint) {
        this.connectPoint = connectPoint;
        this.context = context;
        shutDown = false;
        singleSender = Executors.newSingleThreadExecutor();
    }

    @Override
    public void prepared() {
        String serverURI = String.format(Locale.ENGLISH, "%s://%s:%s", connectPoint.isWebSocket() ? (connectPoint.isUseSSL() ? "wss" : "ws") : (connectPoint.isUseSSL() ? "ssl" : "tcp"), RegexHelper.isIPv6(connectPoint.getHost()) ? String.format(Locale.ENGLISH, "[%s]", connectPoint.getHost()) : connectPoint.getHost()
                , connectPoint.isWebSocket() ? (connectPoint.isUseSSL() ? connectPoint.getWebSocketSSLPort() : connectPoint.getWebSocketPort()) : (connectPoint.isUseSSL() ? connectPoint.getSslPort() : connectPoint.getPort()));
        if (mqttAndroidClient != null) {
            return;
        }
        mqttAndroidClient = new MqttAndroidClient(context.getApplicationContext(), serverURI, connectPoint.getClientId());
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mqttAndroidClient.setTraceEnabled(true);
        mqttAndroidClient.setTraceCallback(traceCallback);
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setMqttVersion(connectPoint.getVersion());
        mMqttConnectOptions.setMaxInflight(connectPoint.getMaxInflight());
        mMqttConnectOptions.setAutomaticReconnect(connectPoint.isAutoReconnect());
        mMqttConnectOptions.setCleanSession(connectPoint.isClearSession()); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(connectPoint.getConnectTimeout()); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(connectPoint.getTickTime()); //设置心跳包发送间隔，单位：秒
        if (RegexHelper.isAllNotEmpty(connectPoint.getUserName(), connectPoint.getUserPasswort())) {
            mMqttConnectOptions.setUserName(connectPoint.getUserName()); //设置用户名
            mMqttConnectOptions.setPassword(connectPoint.getUserPasswort().toCharArray()); //设置密码
        }
        if (connectPoint.isUseSSL() && connectPoint.getSslProperties() != null) {
            mMqttConnectOptions.setSSLProperties(connectPoint.getSslProperties());
        }
        if (RegexHelper.isNotEmpty(connectPoint.getLwt())) {
            mMqttConnectOptions.setWill(connectPoint.getLwt().getTopic(), connectPoint.getLwt().getMessage().getBytes(), connectPoint.getLwt().getQos(), connectPoint.getLwt().isRetained());
        }
//        disposter = RxBus.getBus()
//                .getObservable(MQTTMessageEvent.class, BackpressureStrategy.DROP)
//                .onBackpressureDrop()
//                .subscribeOn(Schedulers.computation())
//                .observeOn(Schedulers.io())
//                .subscribe(event -> {
//                    LiveDataBus.post(MQTTMessageEvent.class, event);
//                });
    }

    @Override
    public void connect() {
        if (mqttAndroidClient == null) {
            prepared();
        }
        if (!mqttAndroidClient.isConnected() && !connectPoint.isConnected()) {
            try {
                if (NetSuit.checkEnable(context)) {
                    mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
                } else {
                    connectPoint.setConnected(ConnectActionState.NETWORK_ERROR);
                    LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
                }
            } catch (MqttException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disconnect() {
        try {
            synchronized (this) {
                if (mqttAndroidClient == null) return;
            }
            mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Loger.e("🔥-🔥onSuccess:", "");
                    if (connectPoint != null) {
                        connectPoint.setConnected(ConnectActionState.DISCONNECTED);
                        LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Loger.e("🔥-🔥onFailure:", "");
                }
            });
//            mqttAndroidClient.unregisterResources();
//            mqttAndroidClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (connectPoint != null) {
//                connectPoint.setConnected(ConnectActionState.DISCONNECTED);
//                LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
//            }
//            mqttAndroidClient = null;
        }
    }

    IMqttActionListener actionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            subCallback(asyncActionToken, null);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            if (exception == null) {
                exception = new Exception("订阅失败，未知错误，请重试");
            }
            subCallback(asyncActionToken, exception);
        }
    };

    @Override
    public void subscribe(MQTTMessage mqttMessage) {
        try {
            if (mqttAndroidClient == null || mqttMessage == null || mqttMessage.getTopic() == null)
                return;
            MqttMessage.validateQos(mqttMessage.getQos());
//            mqttAndroidClient.subscribe(mqttMessage.getTopic(), mqttMessage.getQos());
            mqttAndroidClient.subscribe(mqttMessage.getTopic(), mqttMessage.getQos(), mqttMessage, actionListener);
        } catch (IllegalArgumentException | MqttException e) {
            e.printStackTrace();
        }
    }


    private void subCallback(IMqttToken asyncActionToken, Throwable exception) {
        for (int i = 0; i < asyncActionToken.getTopics().length; i++) {
            String topic = asyncActionToken.getTopics()[i];
            MQTTMessageEvent event = new MQTTMessageEvent();
            if (asyncActionToken.getUserContext() instanceof MQTTMessage) {
                MQTTMessage raw = (MQTTMessage) asyncActionToken.getUserContext();
                if (raw != null) {
                    event.setId(raw.getId());
                }
            } else {
                event.setId(asyncActionToken.getMessageId());
            }
            event.setEntryId(connectPoint.get_id());
            event.setType(EventType.subCallBack);
            event.setTopic(topic);
            event.setDate(System.currentTimeMillis());
            try {
                if (asyncActionToken.getGrantedQos() != null) {
                    event.setQos(asyncActionToken.getGrantedQos()[i]);
                }
                byte[] payload = asyncActionToken.getResponse().getPayload();
                event.setPayload(payload);
            } catch (Exception e) {
//                e.printStackTrace();
            }
            event.setServer(asyncActionToken.getClient().getServerURI());
            try {
                byte[] header = asyncActionToken.getResponse().getHeader();
                event.setExtras(new String(header, Charset.defaultCharset()));
            } catch (Exception e) {
//                e.printStackTrace();
            }
            if (exception != null) {
                event.setError(exception.getLocalizedMessage());
            }
            LiveDataBus.post(MQTTMessageEvent.class, event);
        }
    }


    private void unsubCallback(IMqttToken asyncActionToken, Throwable exception) {
        if (asyncActionToken.getUserContext() instanceof String) {
            String topic = (String) asyncActionToken.getUserContext();
            MQTTMessageEvent event = new MQTTMessageEvent();
            event.setEntryId(connectPoint.get_id());
            event.setType(EventType.unsubCallBack);
            event.setTopic(topic);
            event.setDate(System.currentTimeMillis());
            event.setServer(asyncActionToken.getClient().getServerURI());
            if (exception != null) {
                event.setError(exception.getLocalizedMessage());
            }
            LiveDataBus.post(MQTTMessageEvent.class, event);
        }
    }

    @Override
    public void subscribeAll(String[] topics, int[] qos) {
        if (RegexHelper.isAnyEmpty(topics, qos)) return;
        if (mqttAndroidClient == null) return;
        if (topics.length != qos.length) return;
        try {
            mqttAndroidClient.subscribe(topics, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private IMqttActionListener unsubactionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            unsubCallback(asyncActionToken, null);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            if (exception == null) {
                exception = new Exception("取消订阅失败，未知错误，请重试");
            }
            unsubCallback(asyncActionToken, exception);
        }
    };

    @Override
    public void unsubscribe(@NonNull String topic) {
        try {
            if (mqttAndroidClient == null || connectPoint == null) return;
            List<MQTTMessage> sub = connectPoint.getSubTopics();
            if (sub != null) {
                List<MQTTMessage> filtered = Stream.of(sub)
                        .filter(item -> !topic.equalsIgnoreCase(item.getTopic()))
                        .collect(Collectors.toList());
                connectPoint.setSubTopics(filtered);
            }
            mqttAndroidClient.unsubscribe(topic, topic, unsubactionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void unsubscribeAll(String[] topics) {
        try {
            if (mqttAndroidClient == null) return;
            if (topics == null) {
                //issues 目前还不支持 提案到 Mqtt v3.1.1
                mqttAndroidClient.unsubscribe("#", null, unsubactionListener);
            } else mqttAndroidClient.unsubscribe(topics, null, unsubactionListener);
            if (connectPoint != null) {
                connectPoint.setSubTopics(null);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(MQTTMessage mqttMessage) {
        try {
            MqttMessage.validateQos(mqttMessage.getQos());
            MqttMessage mqttv3Message = new MqttMessage();
            mqttv3Message.setPayload(mqttMessage.getMessage().getBytes());
            mqttv3Message.setQos(mqttMessage.getQos());
            mqttv3Message.setRetained(mqttMessage.isRetained());
            mqttv3Message.setId(mqttMessage.getId());
            mqttAndroidClient.publish(mqttMessage.getTopic(),
                    mqttv3Message,
                    mqttMessage, null);
        } catch (IllegalArgumentException | MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return mqttAndroidClient != null && mqttAndroidClient.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void release() {

    }

    @Override
    public void onResume() {
        isServiceActive = true;
    }

    @Override
    public void onPause() {
        isServiceActive = false;
    }


    private class SenderTask implements Callable<Void> {

        private MQTTMessageEvent mQTTConnectEvent;

        public SenderTask(MQTTMessageEvent mQTTConnectEvent) {
            this.mQTTConnectEvent = mQTTConnectEvent;
        }

        @Override
        public Void call() throws Exception {
            if (shutDown) return null;
            try {
                Thread.sleep(180);
            } catch (Exception e) {
            } finally {
                LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
            }
            return null;
        }
    }


    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.connectionLost);
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            Loger.e("😁connectionLost:", "Throwable == Null");
            if (cause != null) {
                Loger.e("😁connectionLost:", cause.getMessage());
            }
//            watchStatusDog();
            connectPoint.setConnected(ConnectActionState.CONNECT_LOSS);
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.messageArrived);
            mQTTConnectEvent.setTopic(topic);
            mQTTConnectEvent.setPayload(message.getPayload());
            mQTTConnectEvent.setId(message.getId());
            mQTTConnectEvent.setQos(message.getQos());
            mQTTConnectEvent.setPayload(message.getPayload());
            mQTTConnectEvent.setDate(System.currentTimeMillis());
            Loger.e("✉️messageArrived:", topic + "");
//            singleSender.submit(new SenderTask(mQTTConnectEvent));
//            RxEventBus.post(mQTTConnectEvent);
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.deliveryComplete);
            try {
                mQTTConnectEvent.setTopic(StringUtils.join(token.getTopics(), ","));
                mQTTConnectEvent.setId(token.getMessageId());
                mQTTConnectEvent.setQos(token.getMessage().getQos());
                mQTTConnectEvent.setServer(token.getClient().getServerURI());
                mQTTConnectEvent.setPayload(token.getMessage().getPayload());
                mQTTConnectEvent.setDate(System.currentTimeMillis());
                if (token.getUserContext() != null && token.getUserContext() instanceof MQTTMessage) {
                    MQTTMessage message = (MQTTMessage) token.getUserContext();
                    mQTTConnectEvent.setTopic(message.getTopic());
                    mQTTConnectEvent.setId(message.getId());
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
            if (token != null) {
                Loger.e("🆗deliveryComplete:", token.toString());
            }
            LiveDataBus.post(MQTTMessageEvent.class, mQTTConnectEvent);
        }
    };

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
//            LiveDataBus.post(MQTTTransferEvent.class, new MQTTTransferEvent(asyncActionToken, null));
            Loger.e("🔥onSuccess:", "" + Arrays.toString(asyncActionToken.getTopics()));
            connectPoint.setConnected(ConnectActionState.CONNECTED);
            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
            if (connectPoint != null && connectPoint.isAutoReconnect()) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            if (exception != null) {
//                LiveDataBus.post(MQTTTransferEvent.class, new MQTTTransferEvent(asyncActionToken, exception.getMessage()));
                Loger.e("😰onFailure:", "" + exception.getMessage());
            } else {
//                LiveDataBus.post(MQTTTransferEvent.class, new MQTTTransferEvent(asyncActionToken, "connect error"));
            }
            connectPoint.setConnected(ConnectActionState.DISCONNECTED);
            LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
        }
    };


    private void watchStatusDog() {
        synchronized (locker) {
            disposeTimer();
            actTimerSubscription = Observable
                    .interval(1, TimeUnit.SECONDS)
                    .subscribe((i) -> {
                        if (isServiceActive) {
                            boolean isConnected = isConnected();
                            if (isConnected && (connectPoint.getConnected() != ConnectActionState.CONNECTED)) {
                                connectPoint.setConnected(ConnectActionState.CONNECTED);
                                LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
                                disposeTimer();
                            }
                        }
                    }, e -> {
                    });
        }

    }

    private void disposeTimer() {
        if (actTimerSubscription != null) {
            if (!actTimerSubscription.isDisposed()) {
                actTimerSubscription.dispose();
            }
            actTimerSubscription = null;
        }
    }


    @Override
    public void onDestroy() {
        try {
            disposeTimer();
            if (mqttAndroidClient != null) {
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient.close();
                mqttAndroidClient = null;
            }
            if (singleSender != null && !singleSender.isShutdown()) {
                shutDown = true;
                singleSender.shutdownNow();
            }
            if (disposter != null && !disposter.isDisposed()) {
                disposter.dispose();
            }
        } catch (IllegalArgumentException e) {
            mqttAndroidClient = null;
        }

    }

    private MqttTraceHandler traceCallback = new MqttTraceHandler() {
        @Override
        public void traceDebug(String tag, String message) {
            Loger.e("🎈traceDebug:" + tag, "" + message);
            if (message != null && message.contains("Reconnect to server")) {
                connectPoint.setConnected(ConnectActionState.CONNECTED);
                LiveDataBus.post(MQTTStateEvent.class, new MQTTStateEvent(connectPoint.get_id(), connectPoint.getConnected()));
            }
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.DEBUG, tag, message));
        }

        @Override
        public void traceError(String tag, String message) {
            Loger.e("🎈traceError:" + tag, "" + message);
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.ERROR, tag, message));
        }

        @Override
        public void traceException(String tag, String message, Exception e) {
            Loger.e("🎈traceException:" + tag, "" + message + e.getMessage());
            LiveDataBus.post(MQTTTraceEvent.class, new MQTTTraceEvent(MQTTTraceEvent.Type.EXCEPTION, tag, message));
        }
    };
}
