package com.fuwafuwa.sys.snackai.event;

import com.fuwafuwa.sys.snackai.BeaconModel;

public class BeaconEvent extends Event{

    private BeaconModel model;

    public BeaconModel getModel() {
        return model;
    }

    public void setModel(BeaconModel model) {
        this.model = model;
    }

}
