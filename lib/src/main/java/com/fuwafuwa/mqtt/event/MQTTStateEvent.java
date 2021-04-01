package com.fuwafuwa.mqtt.event;


import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.workflow.agent.event.Event;

public class MQTTStateEvent implements Event {
    private ConnectActionState isConnected;

    private String _id;

    public MQTTStateEvent(ConnectActionState isConnected) {
        this.isConnected = isConnected;
    }

    public MQTTStateEvent(String _id, ConnectActionState isConnected) {
        this._id = _id;
        this.isConnected = isConnected;
    }

    public ConnectActionState getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(ConnectActionState isConnected) {
        this.isConnected = isConnected;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "MQTTStateEvent{" +
                "isConnected=" + isConnected +
                ", _id='" + _id + '\'' +
                '}';
    }
}
