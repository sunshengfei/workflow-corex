package com.fuwafuwa.mqtt.event;


import com.fuwafuwa.workflow.agent.event.Event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

public class MQTTTraceEvent implements Event {

    @Target({FIELD, PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int ERROR = 1;
        int DEBUG = 2;
        int EXCEPTION = 3;
    }

    private int type;
    private String tag;
    private String message;
    private long date;

    public MQTTTraceEvent(@Type int type, String tag, String message) {
        this.tag = tag;
        this.message = message;
        this.date = System.currentTimeMillis();
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
