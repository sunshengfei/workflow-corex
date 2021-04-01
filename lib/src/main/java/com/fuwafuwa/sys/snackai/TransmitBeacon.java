package com.fuwafuwa.sys.snackai;

import java.io.Serializable;
import java.util.UUID;

public class TransmitBeacon implements Serializable {

    private String _id;
    private String name;
    private String uuid;
    private String layout;
    private int major;
    private int minor;
    private int manufacturor = 0x0118;
    private int txPower = -59;
    private String createdAt;

    public TransmitBeacon() {
    }

    public static String idGenerator() {
        return UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getManufacturor() {
        return manufacturor;
    }

    public void setManufacturor(int manufacturor) {
        this.manufacturor = manufacturor;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}
