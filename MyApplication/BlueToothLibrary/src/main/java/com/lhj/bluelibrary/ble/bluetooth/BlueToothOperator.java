package com.lhj.bluelibrary.ble.bluetooth;

import android.content.Context;

import com.lhj.bluelibrary.ble.bluetooth.entrust.ISmartBluetooth;
import com.lhj.bluelibrary.ble.bluetooth.ientrust.SmartBluetooth;
import com.lhj.bluelibrary.ble.push.BlueToothInstance;
import com.lhj.bluelibrary.ble.push.InformationPushCallBack;
import com.lhj.bluelibrary.ble.push.MyNotificationService;

/**
 * Created by Administrator on 2017/6/21.
 */
public class BlueToothOperator {
    private static final BlueToothOperator instance = new BlueToothOperator();
    public static BlueToothOperator getInstance(){
        return instance;
    }
    private ISmartBluetooth iSmartBluetooth;

    public void initSDK(Context context){
        if(iSmartBluetooth==null){
            iSmartBluetooth = new SmartBluetooth(context);
        }
    }

    public ISmartBluetooth getSmartBluetooth(){
        return iSmartBluetooth;
    }

    public void setOnPushListeners(InformationPushCallBack informationPushCallBack){
        BlueToothInstance.getInstance().setInformationPushCallBack(informationPushCallBack);
    }

    public boolean getPushEnable(Context context){
        return BlueToothInstance.getInstance().NotificationServiceEnable(context);
    }

    public void openPushSettings(Context context){
        BlueToothInstance.getInstance().openNotiPushSettingUI(context);
    }

    public void restartPushService(Context context){
        BlueToothInstance.getInstance().toggleNotificationListenerService(context, MyNotificationService.class);
    }

    public void release(){
        if(iSmartBluetooth!=null){
            iSmartBluetooth.release();
            iSmartBluetooth = null;
        }
    }

}
