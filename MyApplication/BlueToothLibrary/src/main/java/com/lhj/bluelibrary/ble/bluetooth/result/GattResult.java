package com.lhj.bluelibrary.ble.bluetooth.result;

/**
 * Created by Administrator on 2017/6/22.
 */
public interface GattResult {
    void OnConnectted();
    void OnDisconnectted();
    void onCharacteristicRead(byte[] bytes);
    void onReadRemoteRssi(int rssi);
    void onCharacteristicChanged(byte[] bytes);
    void onDescriptorWrite(String serviceUUID,String characteristicsUUID);
}
