package com.fuwafuwa.mqtt.bean;


import com.fuwafuwa.utils.Objects;
import com.fuwafuwa.workflow.agent.WorkFlowItemDelegate;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import java.util.List;

public class MQTTConnectUserEntity extends MqttConnectPoint implements Cloneable {
    private String _id;
    private String profileName = "Unnamed";

    private String profileType = "MQTT_Broker";

    private String brokerType = "";

    private String createdAt;

    private List<MQTTMessage> subTopics;

    private boolean isSelected;
    private int order;

    private ConnectActionState connected = ConnectActionState.IDLE;


    public boolean isIdle() {
        return connected == null || connected == ConnectActionState.NETWORK_ERROR || connected == ConnectActionState.IDLE || connected == ConnectActionState.DISCONNECTED;
    }

    public boolean isConnected() {
        return connected == ConnectActionState.CONNECTED;
    }

    public boolean isConnecting() {
        return connected == ConnectActionState.CONNECTING;
    }


    public MQTTConnectUserEntity() {
    }

    public static String idGenerator() {
        return WorkFlowItemDelegate.getUUID();
    }


    @Override
    public Object clone() {
        MQTTConnectUserEntity entity = null;
        try {
            entity = (MQTTConnectUserEntity) super.clone();
            entity.setSubTopics(Objects.deepCopy(subTopics));
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return entity;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(String brokerType) {
        this.brokerType = brokerType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<MQTTMessage> getSubTopics() {
        return subTopics;
    }

    public void setSubTopics(List<MQTTMessage> subTopics) {
        this.subTopics = subTopics;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ConnectActionState getConnected() {
        return connected;
    }

    public void setConnected(ConnectActionState connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return "MQTTConnectUserEntity{" +
                "_id='" + _id + '\'' +
                ", profileName='" + profileName + '\'' +
                ", profileType='" + profileType + '\'' +
                ", brokerType='" + brokerType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", subTopics=" + subTopics +
                ", isSelected=" + isSelected +
                ", order=" + order +
                ", connected=" + connected +
                '}';
    }
}
