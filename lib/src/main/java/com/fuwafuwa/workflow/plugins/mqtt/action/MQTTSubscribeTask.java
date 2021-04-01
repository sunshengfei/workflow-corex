package com.fuwafuwa.workflow.plugins.mqtt.action;

import android.content.Context;

import com.fuwafuwa.mqtt.BaseMqttSubscriber;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.event.MQTTMessageEvent;
import com.fuwafuwa.workflow.agent.exception.RunException;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;


import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class MQTTSubscribeTask extends BaseMqttSubscriber implements Callable<Task> {

    protected WorkFlowNode workFlowNode;
    private final AtomicInteger lock = new AtomicInteger();

    private MQTTMessageEvent localMQTTConnectEvent;

    public MQTTSubscribeTask(Context context, WorkFlowNode workFlowNode) {
        super(context, workFlowNode.getPayload());
        this.workFlowNode = workFlowNode;
    }


    protected void onConnectFailure() {

    }

    protected void onConnectSuccess() {
        releaseLock();
    }


    protected void whenConnectionLost() {
        releaseLock();
    }

    protected void afterMessageArrived(String topic, MqttMessage message) {
        localMQTTConnectEvent = new MQTTMessageEvent();
        localMQTTConnectEvent.setTopic(topic);
        localMQTTConnectEvent.setPayload(message.getPayload());
        releaseLock();
    }

    protected void afterDeliveryComplete() {
        releaseLock();
    }

    private void releaseLock() {
        try {
            if (lock.get() == -1) return;
            synchronized (lock) {
                lock.notify();
            }
        } catch (Exception e) {
        }

    }


    @Override
    public Task call() throws Exception {
        if (RegexHelper.isOneEmpty(payload, payload.getBrokerId())) return null;
        if (connectPoint == null) {
            throw new RunException(workFlowNode.get_id(), "请检查MQTT相关连接属性是否配置");
        }
        synchronized (lock) {
            connect();
            lock.wait();
            localMQTTConnectEvent = null;
            //isConnectSuccess
            if (isConnectSuccess) {
                //订阅
                subscribe(payload.getBody());
                lock.wait();
                lock.set(-1);
            }
            disconnect();
        }
        if (localMQTTConnectEvent == null) return null;
        //取 mQTTConnectEvent
        byte[] message = localMQTTConnectEvent.getPayload();
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        task.setResult(new String(message));
        return task;
    }


}
