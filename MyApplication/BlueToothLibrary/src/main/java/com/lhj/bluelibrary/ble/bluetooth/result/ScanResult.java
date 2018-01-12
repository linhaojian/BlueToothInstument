package com.lhj.bluelibrary.ble.bluetooth.result;

import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/21.
 */
public interface ScanResult {
    void getDevices(ScanEntity scanEntity);
}
