package com.fuwafuwa.sys.snackai;

import androidx.annotation.NonNull;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BeaconModel implements Serializable {
    List<Long> datafields;
    List<Long> extraDatafields;
    int serviceUUID;
    String bluetoothAddress;
    String bluetoothName;
    int beaconTypeCode;
    Identifier uuid;
    Identifier major;
    Identifier minor;
    int rssi;
    double distance;
    int txPower;
    int manufacturer;
    Region region;
    long updateTime;

    public int getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(int serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public int getBeaconTypeCode() {
        return beaconTypeCode;
    }

    public void setBeaconTypeCode(int beaconTypeCode) {
        this.beaconTypeCode = beaconTypeCode;
    }

    public Identifier getUuid() {
        return uuid;
    }

    public void setUuid(Identifier uuid) {
        this.uuid = uuid;
    }

    public Identifier getMajor() {
        return major;
    }

    public void setMajor(Identifier major) {
        this.major = major;
    }

    public Identifier getMinor() {
        return minor;
    }

    public void setMinor(Identifier minor) {
        this.minor = minor;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public int getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(int manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Long> getDatafields() {
        return datafields;
    }

    public void setDatafields(List<Long> datafields) {
        this.datafields = datafields;
    }

    public List<Long> getExtraDatafields() {
        return extraDatafields;
    }

    public void setExtraDatafields(List<Long> extraDatafields) {
        this.extraDatafields = extraDatafields;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeaconModel that = (BeaconModel) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }


    public static BeaconModel toModel(@NonNull Beacon beacon) {
        BeaconModel beaconModel = new BeaconModel();
        beaconModel.setServiceUUID(beacon.getServiceUuid());
        beaconModel.setBluetoothAddress(beacon.getBluetoothAddress());
        beaconModel.setBluetoothName(beacon.getBluetoothName());
        beaconModel.setBeaconTypeCode(beacon.getBeaconTypeCode());
        beaconModel.setUuid(beacon.getId1());
        beaconModel.setMajor(beacon.getId2());
        beaconModel.setMinor(beacon.getId3());
        beaconModel.setRssi(beacon.getRssi());
        beaconModel.setDistance(beacon.getDistance());
        beaconModel.setTxPower(beacon.getTxPower());
        beaconModel.setManufacturer(beacon.getManufacturer());
        beaconModel.setTxPower(beacon.getTxPower());
        beaconModel.setDatafields(beacon.getDataFields());
        beaconModel.setExtraDatafields(beacon.getExtraDataFields());
        return beaconModel;
    }
}
