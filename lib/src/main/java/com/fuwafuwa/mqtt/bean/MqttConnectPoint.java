package com.fuwafuwa.mqtt.bean;

import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;

import java.io.Serializable;
import java.util.Properties;


import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_DEFAULT;

public class MqttConnectPoint implements Serializable, Cloneable {

    private String clientId;//客户端ID，一般以客户端唯一标识符表示
    //    private String protocol="tcp";//ssl
    private String host;
    private int port = 1883;

    private int sslPort = 8883;

    private int version = MQTT_VERSION_DEFAULT;

    private boolean useSSL;
    private boolean webSocket;

    private int webSocketPort = 8080;
    private int webSocketSSLPort = 8081;

    private boolean clearSession = true;
    private boolean autoReconnect = true;

    private String userName;
    private String userPasswort;

    private int qos = 0;//0至多一次 1至少一次 2只有一次
    private int connectTimeout = 30;
    private int tickTime = 60;
    private int maxInflight = 50; //飞空消息数

    private Properties sslProperties;
    //临终遗言LWT
    private MQTTMessage lwt;

    public MqttConnectPoint() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSslPort() {
        return sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public boolean isWebSocket() {
        return webSocket;
    }

    public void setWebSocket(boolean webSocket) {
        this.webSocket = webSocket;
    }

    public int getWebSocketPort() {
        return webSocketPort;
    }

    public void setWebSocketPort(int webSocketPort) {
        this.webSocketPort = webSocketPort;
    }

    public int getWebSocketSSLPort() {
        return webSocketSSLPort;
    }

    public void setWebSocketSSLPort(int webSocketSSLPort) {
        this.webSocketSSLPort = webSocketSSLPort;
    }

    public boolean isClearSession() {
        return clearSession;
    }

    public void setClearSession(boolean clearSession) {
        this.clearSession = clearSession;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPasswort() {
        return userPasswort;
    }

    public void setUserPasswort(String userPasswort) {
        this.userPasswort = userPasswort;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getMaxInflight() {
        return maxInflight;
    }

    public void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
    }

    public Properties getSslProperties() {
        return sslProperties;
    }

    public void setSslProperties(Properties sslProperties) {
        this.sslProperties = sslProperties;
    }

    public MQTTMessage getLwt() {
        return lwt;
    }

    public void setLwt(MQTTMessage lwt) {
        this.lwt = lwt;
    }

    @Override
    public String toString() {
        return "MqttConnectPoint{" +
                "clientId='" + clientId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", sslPort=" + sslPort +
                ", version=" + version +
                ", useSSL=" + useSSL +
                ", webSocket=" + webSocket +
                ", webSocketPort=" + webSocketPort +
                ", webSocketSSLPort=" + webSocketSSLPort +
                ", clearSession=" + clearSession +
                ", autoReconnect=" + autoReconnect +
                ", userName='" + userName + '\'' +
                ", userPasswort='" + userPasswort + '\'' +
                ", qos=" + qos +
                ", connectTimeout=" + connectTimeout +
                ", tickTime=" + tickTime +
                ", maxInflight=" + maxInflight +
                ", sslProperties=" + sslProperties +
                ", lwt=" + lwt +
                '}';
    }
}
