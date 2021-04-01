package com.fuwafuwa.workflow.agent.event;


import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import java.io.Serializable;



public class MQTTMessageEvent extends MQTTMessage implements Event, Serializable {
    private String entryId;
    private String server;
    private String extras;
    private String error;

    public MQTTMessageEvent() {
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "MQTTMessageEvent{" +
                "entryId='" + entryId + '\'' +
                ", server='" + server + '\'' +
                ", extras='" + extras + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
