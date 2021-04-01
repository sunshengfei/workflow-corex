package com.fuwafuwa.workflow.plugins.ibase.payload;

import java.io.Serializable;


public class IPayload implements Serializable, Cloneable {
    public int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IPayload{" +
                "type=" + type +
                '}';
    }
}
