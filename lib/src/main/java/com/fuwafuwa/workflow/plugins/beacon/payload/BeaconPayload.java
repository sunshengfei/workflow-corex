package com.fuwafuwa.workflow.plugins.beacon.payload;

import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;

import java.util.HashMap;


public class BeaconPayload extends IPayload implements Cloneable {

    private HashMap<String, String> param;

    public BeaconPayload() {
        type = DefaultPayloadType.type_beacon_finder;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "BeaconPayload{" +
                "param=" + param +
                '}';
    }
}

