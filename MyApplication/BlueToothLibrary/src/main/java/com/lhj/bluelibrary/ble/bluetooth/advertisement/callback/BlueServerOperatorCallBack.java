package com.lhj.bluelibrary.ble.bluetooth.advertisement.callback;

/**
 * Created by Administrator on 2017/5/31.
 */
public interface BlueServerOperatorCallBack {
    void OnConnect();
    void OnDisConnect();
    void OnPairRequest();
    void OnPairResult(boolean isVerify);
    void OnOpenLockSucceed();
    void OnLockStatusResult(int status);
    void OnUpdatePwResult(boolean isSucced);
}
