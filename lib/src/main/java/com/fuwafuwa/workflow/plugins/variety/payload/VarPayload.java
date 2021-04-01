package com.fuwafuwa.workflow.plugins.variety.payload;

import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;


public class VarPayload extends IPayload implements Cloneable {
    private String varName;
    private String value;

    public VarPayload() {
        type = DefaultPayloadType.type_var;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "VarPayload{" +
                "varName='" + varName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

