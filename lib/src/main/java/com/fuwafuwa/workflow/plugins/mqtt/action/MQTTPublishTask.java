package com.fuwafuwa.workflow.plugins.mqtt.action;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.mqtt.bean.MqttConnectPoint;
import com.fuwafuwa.mqtt.db.MQTTProfileDBHelper;
import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.NetSuit;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.StringUtils;
import com.fuwafuwa.workflow.agent.event.MQTTMessageEvent;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.mqtt.payload.EventType;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTPayload;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class MQTTPublishTask implements Callable<Task> {

    private WorkFlowNode workFlowNode;
    private Context context;
    private String bid;
    private MQTTProfileDBHelper helper;
    private MQTTPayload payload;

    private MqttAndroidClient mqttAndroidClient;
    private MQTTMessageEvent mQTTConnectEvent;
    private MQTTConnectUserEntity connectPoint;
    private MqttConnectOptions mMqttConnectOptions;
    private boolean isConnectSuccess;
    private final AtomicInteger lock = new AtomicInteger();

    public MQTTPublishTask(Context context, WorkFlowNode workFlowNode) {
        this.workFlowNode = workFlowNode;
        this.payload = (MQTTPayload) workFlowNode.getPayload();
        this.context = context;
        this.helper = new MQTTProfileDBHelper(context);
    }

    private MQTTConnectUserEntity queryById(String bid) {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            String[] columns = {MQTTProfileDBHelper.ID, MQTTProfileDBHelper.KEY, MQTTProfileDBHelper.VAL, MQTTProfileDBHelper.CREATEDAT};
            Cursor cursor = db.query(MQTTProfileDBHelper.TABLE_NAME,
                    columns, MQTTProfileDBHelper.ID + "=?", new String[]{bid},
                    null, null,
                    null);
            MQTTConnectUserEntity record = null;
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.ID));
                    String key = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.KEY));
                    String value = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.VAL));
                    String date = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.CREATEDAT));
                    record = GsonUtils.parseJson(value, MQTTConnectUserEntity.class);
                    record.set_id(id);
                    record.setProfileName(key);
                    record.setCreatedAt(date);
                }
                cursor.close();
            }
            return record;
        } catch (Exception e) {
            return null;
        }
    }


    private void connect() {
        if (mqttAndroidClient == null) {
            $prepared(connectPoint);
        }
        if (!mqttAndroidClient.isConnected() && NetSuit.checkEnable(context)) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    private void $prepared(MqttConnectPoint connectPoint) {
        String serverURI = String.format(Locale.ENGLISH, "%s://%s:%s", connectPoint.isUseSSL() ? "ssl" : "tcp", connectPoint.getHost(), connectPoint.getPort());
        if (mqttAndroidClient != null) {
            disconnect();
        }
        mqttAndroidClient = new MqttAndroidClient(context.getApplicationContext(), serverURI, connectPoint.getClientId());
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mqttAndroidClient.setTraceEnabled(false);
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
    }

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Loger.e("🔥onSuccess:", "" + Arrays.toString(asyncActionToken.getTopics()));
            //TODO 订阅
            isConnectSuccess = true;
            releaseLock();
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
            isConnectSuccess = true;
            releaseLock();
            if (exception != null) {
                Loger.e("😰onFailure:", "" + exception.getMessage());
            } else {
            }
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.connectionLost);
            if (cause != null) {
                Loger.e("😁connectionLost:", cause.getMessage());
            }
            releaseLock();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            mQTTConnectEvent = new MQTTMessageEvent();
            mQTTConnectEvent.setType(EventType.deliveryComplete);
            mQTTConnectEvent.setTopic(StringUtils.join(token.getTopics(), ","));
            releaseLock();
            if (token != null) {
                Loger.e("🆗deliveryComplete:", token.toString());
            }
        }
    };

    private void releaseLock() {
        try {
            if (lock.get() == -1) return;
            synchronized (lock) {
                lock.notify();
            }
        } catch (Exception e) {
        }

    }

    private void subscribe(MQTTMessage subscribe) {
        try {
            if (mqttAndroidClient == null || subscribe == null || subscribe.getTopic() == null)
                return;
            MqttMessage.validateQos(subscribe.getQos());
            mqttAndroidClient.subscribe(subscribe.getTopic(), subscribe.getQos());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if (mqttAndroidClient == null) return;
            mqttAndroidClient.disconnect();
            mqttAndroidClient.unregisterResources();
            mqttAndroidClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {

        } finally {
            mqttAndroidClient = null;
        }

    }

    private void publish(MQTTMessage subscribe) {
        try {
            MqttMessage.validateQos(subscribe.getQos());
            mqttAndroidClient.publish(subscribe.getTopic(), subscribe.getMessage().getBytes(), subscribe.getQos(), subscribe.isRetained());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task call() throws Exception {
        if (RegexHelper.isOneEmpty(payload, payload.getBrokerId())) return null;
        bid = payload.getBrokerId();
        connectPoint = queryById(bid);
        if (connectPoint == null) throw new UnknownHostException("配置丢失");
        synchronized (lock) {
            connect();
            lock.wait();
            mQTTConnectEvent = null;
            //isConnectSuccess
            if (isConnectSuccess) {
                //订阅
                publish(payload.getBody());
                lock.wait();
                lock.set(-1);
            }
            disconnect();
        }
        if (mQTTConnectEvent == null) return null;
        //取 mQTTConnectEvent
        Task task = new Task();
        task.setType(workFlowNode.getItemType());
        task.set_id(workFlowNode.get_id());
        task.setResult("OK");
        return task;
    }


}
