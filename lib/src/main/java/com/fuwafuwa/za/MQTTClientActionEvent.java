package com.fuwafuwa.za;


import com.fuwafuwa.workflow.agent.event.Event;

public class MQTTClientActionEvent<T> implements Event {

    private ActionEventType eventType;

    private T payload;


    public MQTTClientActionEvent() {
    }

    public ActionEventType getEventType() {
        return eventType;
    }

    public void setEventType(ActionEventType eventType) {
        this.eventType = eventType;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
