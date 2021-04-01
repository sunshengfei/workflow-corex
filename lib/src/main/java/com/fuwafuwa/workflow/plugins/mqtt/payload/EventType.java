package com.fuwafuwa.workflow.plugins.mqtt.payload;

public enum EventType {
    connectionLost,
    messageArrived,
    deliveryComplete,
    subCallBack,
    unsubCallBack;

    EventType() {
    }
}
