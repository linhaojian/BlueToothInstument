package com.lhj.bluelibrary.ble.callback;

import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/31.
 */
public interface MainBleControllerListener {
    void getDevices(ArrayList<ScanEntity> list);
    void getBuleStatus(boolean blueswicth);
    void connectted();
    void disconnectted();
    void getDatas(byte[] datas);
    void getRssi(int rssi);
}
