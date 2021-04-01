package com.fuwafuwa.workflow.plugins.mqtt.payload;


import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

public class MQTTPayload extends IPayload implements Cloneable {
    private String brokerId;
    private String host;
    private MQTTMessage body;

    public MQTTPayload() {
        type = DefaultPayloadType.type_mqtt;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public MQTTMessage getBody() {
        return body;
    }

    public void setBody(MQTTMessage body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MQTTPayload{" +
                "brokerId='" + brokerId + '\'' +
                ", host='" + host + '\'' +
                ", body=" + body +
                '}';
    }
}

