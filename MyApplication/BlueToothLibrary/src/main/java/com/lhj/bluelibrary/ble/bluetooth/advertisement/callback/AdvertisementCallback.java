package com.lhj.bluelibrary.ble.bluetooth.advertisement.callback;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface AdvertisementCallback {
    void onStartSuccess();
    void onStartFailure(int error);
    void onBlueToothStatuError();
}
