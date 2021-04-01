package com.fuwafuwa.core;


import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({
        ModuleID.WEB_VIEW,
        ModuleID.IOT_BEACON,
        ModuleID.IOT_MQTT_PAHO,
        ModuleID.IOT_MQTT_EMQX
})
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
public @interface ModuleID {
    int WEB_VIEW = 0x5001;
    int IOT_BEACON = 0x8101;
    int IOT_MQTT_PAHO = 0x8201;
    int IOT_MQTT_EMQX = 0x8202;
}
