package com.fuwafuwa.mqtt.bean;

public enum ConnectActionState {
    IDLE,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    NETWORK_ERROR,
    CONNECT_LOSS,
    TERMINAL
}

