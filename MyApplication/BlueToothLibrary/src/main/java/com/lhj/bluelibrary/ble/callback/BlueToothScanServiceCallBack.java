package com.lhj.bluelibrary.ble.callback;

import com.lhj.bluelibrary.ble.entity.ScanEntity;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface BlueToothScanServiceCallBack {

    void getDevices(ArrayList<ScanEntity> scans);
    void getBuleStatus(boolean blueswicth);
}
