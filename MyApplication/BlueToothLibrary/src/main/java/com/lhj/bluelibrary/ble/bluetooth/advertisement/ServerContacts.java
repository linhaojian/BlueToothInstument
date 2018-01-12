package com.lhj.bluelibrary.ble.bluetooth.advertisement;

import android.os.ParcelUuid;

/**
 * Created by Administrator on 2017/5/27.
 */
public class ServerContacts {
    public static final String ADVERT_NAME = "AST7H31";
    public static final ParcelUuid ADVERT_UUID = ParcelUuid.fromString("BF9BA731-BD91-4E19-96F1-6ED5388D8B6E");
    public static final ParcelUuid SERVICE_UUID = ParcelUuid.fromString("BF9BFFF0-BD91-4E19-96F1-6ED5388D8B6E");
    public static final ParcelUuid CHARACTERISTIC_WRITE_UUID = ParcelUuid.fromString("BF9BFFF1-BD91-4E19-96F1-6ED5388D8B6E");
    public static final ParcelUuid CHARACTERISTIC_NOTI_UUID = ParcelUuid.fromString("BF9BFFF2-BD91-4E19-96F1-6ED5388D8B6E");
    public static final ParcelUuid UUID_DESCRIPTOR = ParcelUuid
            .fromString("00002901-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid UUID_DESCRIPTOR_NOTI = ParcelUuid
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final int MANUFACTRUE_ID = 0x1010;
    public static final byte[] MANUFACTRUE_BYTES = new byte[]{0x00,0x00,0x00, (byte) 0xFE,0x00,0x00,0x00, (byte) 0xCF};
}
