package com.fuwafuwa.workflow.plugins.beacon.action;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.plugins.beacon.payload.BeaconPayload;
import com.fuwafuwa.workflow.agent.exception.RunException;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.sys.snackai.BeaconHelper;
import com.fuwafuwa.sys.snackai.BeaconModel;
import com.fuwafuwa.sys.snackai.BeaconMonitor;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class BeaconFinderTask implements Callable<Task>, BeaconConsumer {

    private Context context;
    private Task input;
    private WorkFlowNode workFlowNode;
    private BeaconPayload payload;
    private String varValue;
    private BeaconManager manager;
    private final AtomicInteger lock = new AtomicInteger();
    private String monitorId;

    private List<BeaconModel> currentBeacons;
    private boolean gt;
    private double distance = Integer.MAX_VALUE;
    private String blueToothMac;
    private int major;
    private int minor;

    public BeaconFinderTask(Context context, WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.currentBeacons = new ArrayList<>();
        this.context = context;
        this.payload = (BeaconPayload) workFlowNode.getPayload();
        if (RegexHelper.isNotEmpty(resultSlots)) {
            this.input = resultSlots.get("defaultSlot");
            Task varValueVar = resultSlots.get("defaultVar");
            if (varValueVar != null) {
                varValue = varValueVar.getResult();
            }
        }
        String anid = BeaconMonitor.getAndroidId(context);
        this.monitorId = anid == null ? UUID.randomUUID().toString() : anid;
    }

    @Override
    public Task call() throws Exception {
        if (input == null && varValue != null) {
            input = new Task();
            input.setResult(varValue);
        }
        HashMap<String, String> restrict = payload.getParam();
        if (RegexHelper.isNotEmpty(restrict)) {
            String distanceStr = restrict.get("distance");
            if (distanceStr != null && distanceStr.matches("^[<>]\\d+(\\.\\d+)?$")) {
                String replace = distanceStr.replaceAll("[<>]", "");
                try {
                    distance = Double.parseDouble(replace);
                    gt = '>' == distanceStr.charAt(0);
                } catch (NumberFormatException e) {
                    gt = false;
                }
            }
            String majorNumStr = restrict.get("major");
            major = Byte.MIN_VALUE;
            if (RegexHelper.isHex(majorNumStr)) {
                major = RegexHelper.radiusNumber2DexVal(majorNumStr);
            } else if (RegexHelper.isNatureSerialNumber(majorNumStr)) {
                try {
                    assert majorNumStr != null;
                    major = Integer.parseInt(majorNumStr);
                } catch (NumberFormatException e) {
                    major = Byte.MIN_VALUE;
                }
            }
            if (major < 0 || major > 65535) {
                major = Byte.MIN_VALUE;
            }
            String minorNumStr = restrict.get("minor");
            minor = Byte.MIN_VALUE;
            if (RegexHelper.isHex(minorNumStr)) {
                minor = RegexHelper.radiusNumber2DexVal(minorNumStr);
            } else if (RegexHelper.isNatureSerialNumber(minorNumStr)) {
                try {
                    assert minorNumStr != null;
                    minor = Integer.parseInt(minorNumStr);
                } catch (NumberFormatException e) {
                    minor = Byte.MIN_VALUE;
                }
            }
            if (minor < 0 || minor > 65535) {
                minor = Byte.MIN_VALUE;
            }

            String mac = restrict.get("ble-mac");
            if (RegexHelper.isBlueToothMac(mac)) {
                assert mac != null;
                blueToothMac = mac.replaceAll("-", ":");
            }
        }

        Task task = new Task();
        if (!BeaconHelper.isHasPermisson(context)) {
            throw new RunException(workFlowNode.get_id(), "请检查定位权限以及蓝牙设备是否开启");
        }
        if (BeaconHelper.isSupported(context)) {
            BeaconMonitor monitor = BeaconMonitor.builder(context, this, 0);
            manager = monitor.getBeaconManager();
            if (!manager.checkAvailability()) {
                throw new RunException(workFlowNode.get_id(), "请检查定位权限以及蓝牙设备是否开启");
            }
            synchronized (lock) {
                ranger();
                lock.wait();
                lock.set(-1);
            }
            if (RegexHelper.isNotEmpty(currentBeacons)) {
                task.setResult(GsonUtils.toJson(currentBeacons));
            }
        } else {
            throw new RunException(workFlowNode.get_id(), "本设备不支持Beacon操作");
//            TaskMessage taskError=new TaskMessage();
//            taskError.setSuccess(false);
//            taskError.setMsg("本设备不支持Beacon操作");
//            task.setResult(GsonUtils.toJson(taskError));
        }
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        return task;
    }


    private void releaseLock() {
        try {
            if (lock.get() == -1) return;
            synchronized (lock) {
                lock.notify();
            }
        } catch (Exception e) {
        }

    }


    public void stopScan() {
        manager.removeAllMonitorNotifiers();
        manager.removeAllRangeNotifiers();
    }

    private void ranger() throws RunException {
        manager.addRangeNotifier((beacons, region) -> {
            if (beacons.size() > 0) {
                synchronized (lock) {
                    boolean breakOut = false;
                    for (Beacon beacon : beacons) {
                        BeaconModel beaconModel = BeaconModel.toModel(beacon);
                        boolean isAccept = (gt ? -1 : 1) * beacon.getDistance() < (gt ? -1 : 1) * distance;
                        isAccept = isAccept &&
                                (blueToothMac == null ||
                                        RegexHelper.isBlueToothMac(blueToothMac) &&
                                                blueToothMac.equalsIgnoreCase(beaconModel.getBluetoothAddress()));
                        int major = beaconModel.getMajor().toInt();
                        int minor = beaconModel.getMinor().toInt();
                        if (this.major != Byte.MIN_VALUE) {
                            isAccept = isAccept && major == this.major;
                        }
                        if (this.minor != Byte.MIN_VALUE) {
                            isAccept = isAccept && minor == this.minor;
                        }
                        if (isAccept) {
                            currentBeacons.add(beaconModel);
                            breakOut = true;
                            break;
                        }
                    }
                    if (breakOut) {
                        stopScan();
                        releaseLock();
                    }
                }
            }
        });

        try {
            //Identifier.fromUuid(UUID.fromString(IBeaconTransConstant.proximityUUID))
            manager.startRangingBeaconsInRegion(new Region(monitorId, null
                    , null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new RunException(workFlowNode.get_id(), "开启失败，请检查蓝牙BLE设备是否关闭");
        }
    }


    @Override
    public void onBeaconServiceConnect() {

    }

    @Override
    public Context getApplicationContext() {
        return context == null ? null : context.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        if (context != null)
            context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }
}
