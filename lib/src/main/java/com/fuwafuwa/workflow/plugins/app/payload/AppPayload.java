package com.fuwafuwa.workflow.plugins.app.payload;

import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;

public class AppPayload extends IPayload implements Cloneable {
    private String packageName;
    private String appName;

    public AppPayload() {
        type = DefaultPayloadType.type_app;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "AppPayload{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}

