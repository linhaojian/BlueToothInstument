package com.lhj.bluelibrary.ble.bluetooth.entrust;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.ParcelUuid;

import com.lhj.bluelibrary.ble.bluetooth.result.GattResult;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

/**
 * Created by Administrator on 2017/6/21.
 */
public interface IConnect {
    BluetoothGatt connect(ScanEntity scanEntity, GattResult gattResult);
    BluetoothGatt connect(String mac, GattResult gattResult);
    void getRssi(BluetoothGatt gatt);
    boolean writeCommand(BluetoothGatt gatt,BluetoothGattCharacteristic bluetoothGattCharacteristics,byte[] bytes, boolean isNoResponse);
    void Notity(BluetoothGatt gatt,String serviceUUID,String characteristicsUUID);
    void disconnect(BluetoothGatt bluetoothGatt);
    void close(BluetoothGatt bluetoothGatt);
}
