package com.lhj.bluelibrary.ble.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface BlueToothComServiceCallBack{
        void Connected(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, int status);
        void Disconnect(BluetoothGatt gatt, int status);
        void getRssi(BluetoothGatt gatt, int status, int rssi);
        void notiCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, byte[] data);
}
