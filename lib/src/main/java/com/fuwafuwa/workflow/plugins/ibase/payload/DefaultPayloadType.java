package com.fuwafuwa.workflow.plugins.ibase.payload;

public @interface DefaultPayloadType {
    int type_none = 0;
    int type_mqtt = 0x1;//MQTT
    int type_if = 0x02;//IF
    int type_number = 0x03;//NUMBER
    int type_app = 0x04;//APP
    int type_var = 0x05;//VAR
    int type_string = 0x06;//String
    int type_http = 0x07;//http
    int type_cipher = 0x08;//cipher
    int type_beacon_finder = 0x09;//beacon_finder
}