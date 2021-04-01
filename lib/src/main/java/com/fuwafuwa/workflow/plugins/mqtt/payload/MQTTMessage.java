package com.fuwafuwa.workflow.plugins.mqtt.payload;


import java.io.Serializable;
import java.util.Arrays;


public class MQTTMessage implements Serializable, Cloneable {
    private int id;
    private String topic;
    private String message;
    private int qos;//0至多一次 1至少一次 2只有一次
    private long date;
    private boolean retained;
    private byte[] payload;
    private boolean emqttd;
    private EventType type;

    public MQTTMessage() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public boolean isEmqttd() {
        return emqttd;
    }

    public void setEmqttd(boolean emqttd) {
        this.emqttd = emqttd;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MQTTMessage{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                ", qos=" + qos +
                ", date=" + date +
                ", retained=" + retained +
                ", payload=" + Arrays.toString(payload) +
                ", emqttd=" + emqttd +
                ", type=" + type +
                '}';
    }
}
