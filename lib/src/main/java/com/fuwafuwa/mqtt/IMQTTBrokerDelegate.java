package com.fuwafuwa.mqtt;


import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

public interface IMQTTBrokerDelegate {

    void prepared();

    void connect();

    void disconnect();

    void subscribe(MQTTMessage mqttMessage);

    void subscribeAll(String[] topics, int[] qos);

    void unsubscribe(String topic);

    void unsubscribeAll(String[] topics);

    void publish(MQTTMessage mqttMessage);

    boolean isConnected();

    void release();

    void onResume();

    void onPause();

    void onDestroy();
}
