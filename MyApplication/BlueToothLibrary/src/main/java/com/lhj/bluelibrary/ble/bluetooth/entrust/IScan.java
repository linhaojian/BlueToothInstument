package com.lhj.bluelibrary.ble.bluetooth.entrust;

import com.lhj.bluelibrary.ble.bluetooth.util.BLEContacts;
import com.lhj.bluelibrary.ble.bluetooth.result.ScanResult;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/21.
 */
public interface IScan {
    void startScan(int scanmillisecond, ScanResult scanResult);
    void stopScan();
    void filter(BLEContacts fitype, ArrayList<String> filters);
}
