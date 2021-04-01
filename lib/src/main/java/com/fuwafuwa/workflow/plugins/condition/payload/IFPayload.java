package com.fuwafuwa.workflow.plugins.condition.payload;

import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;


public class IFPayload extends IPayload implements Cloneable {
    private OperatorEnum operator;
    private String param;

    public IFPayload() {
        type = DefaultPayloadType.type_if;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public OperatorEnum getOperator() {
        return operator;
    }

    public void setOperator(OperatorEnum operator) {
        this.operator = operator;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "IFPayload{" +
                "operator=" + operator +
                ", param='" + param + '\'' +
                '}';
    }
}

