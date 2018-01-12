package com.lhj.bluelibrary.ble.bluetooth.advertisement.callback;

/**
 * Created by Administrator on 2017/5/31.
 */
public interface AdvertServerOperatorCallBack {
    void OnBlueToothStatuError();
    void OnConnectted();
    void OnDisConnectted();
    void OnPairRequest();
}
