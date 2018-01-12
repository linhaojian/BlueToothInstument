package com.lhj.bluelibrary.ble.airupdate.ti;

/**
 * Created by Administrator on 2017/1/7.
 */
public interface TIOADExecutorListeners{
    void OnStart();
    void OnStarting(double pro);
    void OnError(int error);
    void OnSuccess();
}
