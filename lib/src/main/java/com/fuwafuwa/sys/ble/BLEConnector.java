package com.fuwafuwa.sys.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BLEConnector {
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(
            "00002902-0000-1000-8000-00805f9b34fb");
    private Context context;
    private BluetoothAdapter.LeScanCallback leScanCallBack;
    private ScanCallback scannerCallback;
    private BluetoothGatt mBluetoothGatt;

    public BLEConnector(@NonNull Context context) {
        this.context = context;
    }

    private BluetoothAdapter btAdapter;


    public static boolean isSupported(@NonNull Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public Observable<Object> scanDevices() {
        BluetoothManager bluetoothManager
                = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) return Observable.error(new Exception("蓝牙设备不可用"));
        btAdapter = bluetoothManager.getAdapter();
//        if (btAdapter == null || !btAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull final ObservableEmitter<Object> emitter) throws Throwable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BluetoothLeScanner scanner = btAdapter.getBluetoothLeScanner();
                    scannerCallback = new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            super.onScanResult(callbackType, result);
                            Log.e("🙅‍♂️ 蓝牙名称", result.getDevice().getName() + " " + result.getDevice().getAddress());
                            emitter.onNext(result);
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            super.onScanFailed(errorCode);
                        }
                    };
                    scanner.startScan(scannerCallback);
                } else {
                    leScanCallBack = (device, rssi, scanRecord) -> emitter.onNext(device);
                    btAdapter.startLeScan(leScanCallBack);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void log() {
        Observable<Object> dispo = scanDevices();
        dispo
                .observeOn(Schedulers.single())
                .filter(item -> item instanceof ScanResult)
                .map(item -> ((ScanResult) item).getDevice())
                .filter(item -> (item.getName() != null && item.getName().contains("WXL")) || "3C:A5:19:7A:FF:38".equalsIgnoreCase(item.getAddress()))
                .doOnNext(scanResult -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BluetoothLeScanner scanner = btAdapter.getBluetoothLeScanner();
                        scanner.stopScan(scannerCallback);
                    } else {
                        btAdapter.stopLeScan(leScanCallBack);
                    }
                })
                .flatMap(item -> connect(item))
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    Log.e("🙅‍♂️ 蓝牙", res + "");
                }, er -> {
                    er.printStackTrace();
                });
    }

    private Observable<String> connect(BluetoothDevice device) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                mBluetoothGatt = device.connectGatt(context, true, new BluetoothGattCallback() {
                    @Override
                    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                    }

                    @Override
                    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                        super.onPhyRead(gatt, txPhy, rxPhy, status);
                    }

                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (newState == BluetoothGatt.STATE_CONNECTED) {
                            Log.e("🙅‍♂️ 蓝牙", "连接成功");
                            Disposable res = Observable.timer(1, TimeUnit.SECONDS)
                                    .subscribe(aLong -> {
                                        mBluetoothGatt.discoverServices();//启用发现服务
                                    }, e -> {
                                        e.printStackTrace();
                                    });

                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        List<BluetoothGattService> services = gatt.getServices();
                        for (BluetoothGattService service : services) {
                            Log.e("🙅蓝牙 service ", service.getUuid().toString());
                            if (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) {
                                List<BluetoothGattCharacteristic> characters = service.getCharacteristics();
                                for (BluetoothGattCharacteristic characteristic : characters) {
                                    Log.e("🙅‍蓝牙 character ", characteristic.getUuid().toString() +" /:"+String.valueOf(characteristic.getProperties()));
                                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                                        continue;
                                    }
                                    toggleNotification(gatt, characteristic, true);
                                }
//                                break;
                            }

                        }

                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                        Log.w("🙅‍蓝牙 Char ",characteristic.getStringValue(0));

                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        Log.w("🙅‍蓝牙 Changed ",characteristic.getStringValue(0));
                        //Notification
                        emitter.onNext(characteristic.getStringValue(0));
                    }

                    @Override
                    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorRead(gatt, descriptor, status);
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                    }

                    @Override
                    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                        super.onReliableWriteCompleted(gatt, status);
                    }

                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                    }
                });
//                connectGatt.getServices();
//                connectGatt.setCharacteristicNotification();
            }
        });
    }

    private boolean toggleNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enable) {
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0)
            return false;
        Log.e("🙅‍蓝牙 notification",characteristic.getUuid().toString());
                gatt.setCharacteristicNotification(characteristic, enable);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean result = gatt.writeDescriptor(descriptor);
            Log.e("🙅‍蓝牙 noti---",result+"");
            return result;
        }
        return false;
    }

}
