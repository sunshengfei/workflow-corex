package com.fuwafuwa.sys.snackai.event;

import com.fuwafuwa.sys.snackai.TransmitBeacon;

public class TransmitBeaconEvent extends Event{

    private TransmitBeacon model;

    public TransmitBeacon getModel() {
        return model;
    }

    public void setModel(TransmitBeacon model) {
        this.model = model;
    }

}
