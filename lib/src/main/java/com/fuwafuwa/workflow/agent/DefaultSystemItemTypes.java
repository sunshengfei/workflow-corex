package com.fuwafuwa.workflow.agent;

public final class DefaultSystemItemTypes {
    public static final int TYPE_HEAD = 0;
    public static final int SEG_TITLE = 0x01;
    public static final int TYPE_NONE = 0x2;
    public static final int TYPE_MQTT = 0x4;
    public static final int TYPE_MQTT_PUBLISH = 0x401;
    public static final int TYPE_MQTT_SUBSCRIBE = 0x402;
    public static final int TYPE_FOOTER = 0x8;
    public static final int TYPE_CONDITION_IF = 0x10;
    public static final int TYPE_CONDITION_IF_ELSE = 0x10 | 1;
    public static final int TYPE_CONDITION_IF_END = 0x10 | 1 << 1 | 1;
    public static final int TYPE_CONDITION_WAIT = 0x20;
    public static final int TYPE_CONDITION_REPEAT = 0x40;
    public static final int TYPE_CONDITION_REPEAT_END = 0x41;
    public static final int TYPE_GOTO = 0x910;
    public static final int TYPE_EXIT_END = 0x911;

    public static final int TYPE_CIPHER = 0x44;

    public static final int TYPE_ACCESS_URL = 0x50;
    public static final int TYPE_BROWSER_URL = 0x60;

    public static final int TYPE_JSON_BEAUTY = 0x80;

    public static final int TYPE_UI_TOAST = 0x146;
    public static final int TYPE_UI_ALERT_TEXT = 0x147;

    public static final int TYPE_LAUNCH_APP = 0x200;
    public static final int TYPE_APP_BACK_2_SELF = 0x201;
    public static final int TYPE_X_INIT = 0x301;
    public static final int TYPE_X_OVERWRITE = 0x302;
    public static final int TYPE_BEACON_FINDER = 0x5180;
    public static final int TYPE_DO_NOTHING_REMARK = 0xfe80;
    public static final int TYPE_MEDIA_PLAY = 0xff30;
    public static final int TYPE_JS = 0x4869;
}
