package com.lhj.bluelibrary.ble.callback;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface PushCallBack {
    void PhoneCallBack(String phonenum, int phonetype);
    void SMSCallBack(String phonenum, String smsstr);
    void OtherAppCallBack(int pushEnum);
    void EmialCallBack();
}
