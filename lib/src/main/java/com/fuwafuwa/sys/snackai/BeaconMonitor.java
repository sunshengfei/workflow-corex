package com.fuwafuwa.sys.snackai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.fuwafuwa.sys.snackai.event.BeaconEvent;
import com.fuwafuwa.sys.snackai.event.BeaconFinderEvent;
import com.fuwafuwa.sys.snackai.event.TransmitBeaconEvent;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BeaconMonitor implements BeaconConsumer {
    private static final String TAG = "BeaconMonitor";
    public static long TimeOutMills = 1100L;
    private static BeaconMonitor beaconMonitor;
    private final HashMap<String, Boolean> regionStateMap;
    private String monitorId;

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    private BeaconManager beaconManager;
    private Context context;

    private Map<String, BeaconTransmitter> transmitters;

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    private BeaconMonitor() {
        this.regionStateMap = new HashMap<>();
    }

    public static BeaconMonitor builder(Activity context) {
        return builder(context, 10000);
    }

    public static BeaconMonitor builder(Activity context, long seconds) {
        return builder(context, null, seconds);
    }

    public static BeaconMonitor builder(Context context, BeaconConsumer consumer, long seconds) {
        if (beaconMonitor == null) {
            beaconMonitor = new BeaconMonitor();
            beaconMonitor.transmitters = new HashMap<>();
        }
        String anid = getAndroidId(context);
        beaconMonitor.monitorId = anid == null ? UUID.randomUUID().toString() : anid;
        beaconMonitor.regionStateMap.clear();
        beaconMonitor.context = context;
        new BackgroundPowerSaver(context.getApplicationContext());
        beaconMonitor.beaconManager = BeaconManager.getInstanceForApplication(context.getApplicationContext());
        addLayers();
        beaconMonitor.beaconManager.bind(consumer == null ? beaconMonitor : consumer);
        beaconMonitor.beaconManager.setBackgroundBetweenScanPeriod(seconds < 300000L ? 300000L : seconds);
        return beaconMonitor;
    }

    private static void addLayers() {
        beaconMonitor.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(LayoutOptions.iBeacon.getValue()));
        beaconMonitor.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconMonitor.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconMonitor.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
    }

    @Override
    public void onBeaconServiceConnect() {
//        monitor();
//        ranger();
//        beaconTransmitter(null);
//        this.monitorId = "D1B024CB-A02D-4650-9C6A-BAEDA8A31F0E";
    }


    public void scan() {
        if (!beaconManager.checkAvailability()) {
            BeaconFinderEvent b = new BeaconFinderEvent();
            b.setSuccess(false);
            b.setMessage("蓝牙设备不可用，请检查是否开启");
            LiveAIBus.post(BeaconFinderEvent.class, b);
            return;
        }
        monitor();
        ranger();

    }

    public void stopScan() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        BeaconFinderEvent b = new BeaconFinderEvent();
        b.setSuccess(false);
        LiveAIBus.post(BeaconFinderEvent.class, b);
    }

    public void transmitter(TransmitBeacon transmitBeacon) {
        beaconTransmitter(transmitBeacon);
    }

    public void stopTransmitter() {
        if (transmitters != null && transmitters.size() > 0) {
            for (Map.Entry<String, BeaconTransmitter> map : transmitters.entrySet()) {
                BeaconTransmitter transmitter = map.getValue();
                if (transmitter != null && transmitter.isStarted()) {
                    transmitter.stopAdvertising();
                }
            }
            transmitters.clear();
        }
    }

    private void monitor() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                regionStateMap.put(region.getUniqueId(), state == 1);
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region(monitorId, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void ranger() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Iterator<Beacon> it = beacons.iterator();
                    while (it.hasNext()) {
                        Beacon beacon = it.next();
                        BeaconModel beaconModel = BeaconModel.toModel(beacon);
                        beaconModel.region = region;
                        beaconModel.updateTime = System.currentTimeMillis();
                        BeaconEvent event = new BeaconEvent();
                        event.setModel(beaconModel);
                        LiveAIBus.post(BeaconEvent.class, event);
                    }
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                } else {
                    Log.i(TAG, "I have just not seeing beacons");
                }
            }
        });
        BeaconFinderEvent b = new BeaconFinderEvent();
        try {
            //Identifier.fromUuid(UUID.fromString(IBeaconTransConstant.proximityUUID))
            beaconManager.startRangingBeaconsInRegion(new Region(monitorId, null
                    , null, null));
            b.setSuccess(true);
        } catch (RemoteException e) {
            b.setSuccess(false);
            b.setMessage("开启失败，请检查蓝牙BLE设备是否关闭");
            e.printStackTrace();
        }
        LiveAIBus.post(BeaconFinderEvent.class, b);
    }

    public void stopTransmitter(String id) {
        if (id == null) {
            stopTransmitter();
        } else if (transmitters != null && transmitters.size() > 0) {
            Set<Map.Entry<String, BeaconTransmitter>> set = transmitters.entrySet();
            for (Map.Entry<String, BeaconTransmitter> entry : set) {
                if (entry.getKey().equals(id)) {
                    if (entry.getValue() != null) {
                        entry.getValue().stopAdvertising();
                    }
                }
            }
        }
        TransmitBeaconEvent event = new TransmitBeaconEvent();
        event.setSuccess(false);
        LiveAIBus.post(TransmitBeaconEvent.class, event);
    }


    ///region  #   example of BEACON_FORMAT
    //ALTBEACON      m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25
    //EDDYSTONE TLM  x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15
    //EDDYSTONE UID  s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19
    //EDDYSTONE URL  s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v
    //IBEACON        m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24
    //endregion

    /// region   #   Base Beacon Packet
    //d6 be 89 8e # Access address for advertising data (this is always the same fixed value)
    //40 # Advertising Channel PDU Header byte 0.  Contains: (type = 0), (tx add = 1), (rx add = 0)
    //24 # Advertising Channel PDU Header byte 1.  Contains:  (length = total bytes of the advertising payload + 6 bytes for the BLE mac address.)
    //05 a2 17 6e 3d 71 # Bluetooth Mac address (note this is a spoofed address)
    //02 01 1a 1a ff 4c 00 02 15 e2 c5 6d b5 df fb 48 d2 b0 60 d0 f5 a7 10 96 e0 00 00 00 00 c5 # Bluetooth advertisement
    //52 ab 8d 38 a5 # checksum

    //Bluetooth advertisement
    //02 # Number of bytes that follow in first AD structure
    //01 # Flags AD type
    //1A # Flags value 0x1A = 000011010
    //   bit 0 (OFF) LE Limited Discoverable Mode
    //   bit 1 (ON) LE General Discoverable Mode
    //   bit 2 (OFF) BR/EDR Not Supported
    //   bit 3 (ON) Simultaneous LE and BR/EDR to Same Device Capable (controller)
    //   bit 4 (ON) Simultaneous LE and BR/EDR to Same Device Capable (Host)
    //1A # Number of bytes that follow in second (and last) AD structure
    //FF # Manufacturer specific data AD type
    //4C 00 # Company identifier code (0x004C == Apple)
    //02 # Byte 0 of iBeacon advertisement indicator
    //15 # Byte 1 of iBeacon advertisement indicator
    //e2 c5 6d b5 df fb 48 d2 b0 60 d0 f5 a7 10 96 e0 # iBeacon proximity uuid
    //00 00 # major
    //00 00 # minor
    //c5 # The 2's complement of the calibrated Tx Power
    //endregion

    /// region   #   altbeacon Packet BeaconLayout
    //m - matching byte sequence for this beacon type to parse (exactly one required)
    //
    //s - ServiceUuid for this beacon type to parse (optional, only for Gatt-based beacons)
    //
    //i - identifier (at least one required, multiple allowed)
    //
    //p - power calibration field (exactly one required)
    //
    //d - data field (optional, multiple allowed)
    //
    //x - extra layout. Signifies that the layout is secondary to a primary layout with the same matching byte sequence (or ServiceUuid). Extra layouts do not require power or identifier fields and create Beacon objects without identifiers.
    //
    //Example of a parser string for AltBeacon:
    //
    //"m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
    //
    //This signifies that the beacon type will be decoded when an advertisement is found with 0xbeac in bytes 2-3, and a three-part identifier will be pulled out of bytes 4-19, bytes 20-21 and bytes 22-23, respectively. A signed power calibration value will be pulled out of byte 24, and a data field will be pulled out of byte 25.
    //endregion


    private void beaconTransmitter(TransmitBeacon transmitBeacon) {
        TransmitBeaconEvent event = new TransmitBeaconEvent();
        if (!beaconManager.checkAvailability()) {
            event.setSuccess(false);
            event.setError("蓝牙设备不可用，请检查是否开启");
            LiveAIBus.post(TransmitBeaconEvent.class, event);
            return;
        }
        int result = BeaconTransmitter.checkTransmissionSupported(context);
        event.setSuccess(result == BeaconTransmitter.SUPPORTED);
        if (result == BeaconTransmitter.SUPPORTED) {
            if (transmitBeacon == null) {
                event.setError("请先加载配置");
            } else {
                List<Long> dataFields = new ArrayList<>();
                dataFields.add(0L);
                Beacon beacon = new Beacon.Builder()
                        .setId1(transmitBeacon.getUuid())
                        .setId2(String.valueOf(transmitBeacon.getMajor()))
                        .setId3(String.valueOf(transmitBeacon.getMinor()))
                        .setManufacturer(transmitBeacon.getManufacturor())
                        .setTxPower(transmitBeacon.getTxPower())
                        .setDataFields(dataFields)
                        .build();
                event.setModel(transmitBeacon);
                BeaconParser beaconParser = new BeaconParser();
                if (transmitBeacon.getLayout() != null) {
                    beaconParser.setBeaconLayout(transmitBeacon.getLayout());
                } else {
                    beaconParser.setBeaconLayout(LayoutOptions.iBeacon.getValue());
                }
                beaconManager.getBeaconParsers().add(beaconParser);
                BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                event.set_id(transmitBeacon.get_id());
                transmitters.put(event.get_id(), beaconTransmitter);
                beaconTransmitter.startAdvertising(beacon);
            }
        } else {
            event.setError("设备不支持该操作");
        }
        LiveAIBus.post(TransmitBeaconEvent.class, event);
    }

    @Override
    public Context getApplicationContext() {
        return this.context.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }


    public void destroy() {
        if (beaconManager != null)
            beaconManager.unbind(this);
    }
}
