package com.lhj.bluelibrary.ble.bluetooth.util;



/**
 * Created by Administrator on 2017/6/21.
 */
public enum  BLEContacts {
    SCAN_MAC  (0),
    SCAN_UUID  (1),
    SCAN_NAME  (2),
    ;
    private BLEContacts(int nativeInt) {
        this.nativeInt = nativeInt;
    }
    public final int nativeInt;
}
