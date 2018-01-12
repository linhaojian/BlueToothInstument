package com.lhj.bluelibrary.ble.push;

import android.content.Context;
import android.service.notification.NotificationListenerService;

/**
 * Created by Administrator on 2016/5/20.
 */
public class BlueToothInstance {

    private static BlueToothInstance instance=new BlueToothInstance();
    public static BlueToothInstance getInstance(){
        return instance;
    }
    private InformationPushCallBack informationPushCallBack;

    public void phonePush(String phonenum,int phonetype){
        if(this.informationPushCallBack!=null) {
            informationPushCallBack.PhoneCallBack(phonenum,phonetype);
        }
    }

    public void smsPush(String phonenum,String smsstr){
        if(this.informationPushCallBack!=null) {
            informationPushCallBack.SMSCallBack(phonenum,smsstr);
        }
    }

    public void otherApp(int pushEnum){
        if(this.informationPushCallBack!=null) {
            informationPushCallBack.OtherAppCallBack(pushEnum);
        }
    }

    public void emailPush(){
        if(this.informationPushCallBack!=null) {
            informationPushCallBack.EmialCallBack();
        }
    }

    public void setInformationPushCallBack(InformationPushCallBack informationPushCallBack) {
        this.informationPushCallBack = informationPushCallBack;
    }

    public void toggleNotificationListenerService(Context context, Class componentclass){
        NotiPushUtils.toggleNotificationListenerService(context,componentclass);
    }

    public boolean NotificationServiceEnable(Context context){
        return NotiPushUtils.isEnabled(context);
    }

    public void openNotiPushSettingUI(Context context){
        NotiPushUtils.openNotiPushSettingUI(context);
    }

}
