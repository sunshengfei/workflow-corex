package com.fuwafuwa.sys.snackai;

import org.altbeacon.beacon.BeaconParser;

public enum LayoutOptions {
    iBeacon("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"),
    AltBeacon(BeaconParser.ALTBEACON_LAYOUT);

    private String value;

    public String getValue() {
        return value;
    }

    LayoutOptions(String value) {
        this.value = value;
    }
}
