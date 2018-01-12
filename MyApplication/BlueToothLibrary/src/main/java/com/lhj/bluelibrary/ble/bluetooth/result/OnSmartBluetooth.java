package com.lhj.bluelibrary.ble.bluetooth.result;

import com.lhj.bluelibrary.ble.entity.ScanEntity;

/**
 * Created by Administrator on 2017/6/26.
 */
public interface OnSmartBluetooth {
    void onBluetoothStatus(boolean bstatus);
    void onDeviceResult(ScanEntity scanEntity);
    void onConnect();
    void onDisconnect();
    void onCharacteristicChanged(byte[] bytes);
    void onNotity();
    void onRssi(int rssi);
    void onError(int error,String errorMsg);
}
