package com.lhj.bluelibrary.ble.airupdate.ti;

/**
 * Created by Administrator on 2017/1/5.
 */
public class TIOADProfile {
    public static final String oadService_UUID = "f000ffc0-0451-4000-b000-000000000000";
    public static final String oadImageNotify_UUID = "f000ffc1-0451-4000-b000-000000000000";
    public static final String oadBlockRequest_UUID = "f000ffc2-0451-4000-b000-000000000000";
    public static final String connectintervalService_UUID = "f000ccc0-0451-4000-b000-000000000000";
    public static final String connectintervalChar_UUID = "f000ccc2-0451-4000-b000-000000000000";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final int BLUETOOTH_UNABLE_ERROR = 0x10;
    public static final int UPGRADE_FALSE_ERROR = 0X11;
    public static final int UPGRADE_STARTED_ERROR = 0X12;
    public static final int UPGRADE_START = 0X13;
    public static final int UPGRADE_STARTING = 0X14;
    public static final int UPGRADE_SUSSECED = 0X15;

    public static final int ONSERVICESFISCOVERED = 1;
    public static final int CONNECT_SUCCESSFUL = 2;
    public static final int CONNECT_UNSUCCESSFUL = 3;
    public static final int ONDESCTIPTORWRITE = 4;
    public static final int ONCHARATERISTICSCHANGED = 5;
    public static final int ONCHARATERISTICSWRITE = 6;


}
