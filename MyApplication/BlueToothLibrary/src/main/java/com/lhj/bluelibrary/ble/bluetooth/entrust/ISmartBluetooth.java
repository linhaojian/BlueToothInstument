package com.lhj.bluelibrary.ble.bluetooth.entrust;

import com.lhj.bluelibrary.ble.bluetooth.util.BLEContacts;
import com.lhj.bluelibrary.ble.bluetooth.result.OnSmartBluetooth;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/26.
 */
public interface ISmartBluetooth {
    boolean getBlueStatus();
    void filter(BLEContacts fitype, ArrayList<String> filters);
    void startScan(int scanmillisecond);
    void stopScan();
    void connect(String mac,boolean isReConnect);
    void connect(ScanEntity scanEntity,boolean isReConnect);
    void getRssi();
    boolean writeCommand(byte[] bytes,boolean isNotResponse);
    void noti(String serviceUUID,String charwriteUUID,String charnotiUUID);
    void disconnect(boolean isReConnect);
    void close();
    void release();
    void setOnSmartBluetooth(OnSmartBluetooth onSmartBluetooth);
    void setReConnect(boolean isReConnect);
    int getConnectStatus();
    void requestMtu(int mtu);
}
