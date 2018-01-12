package com.lhj.bluelibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.IntentFilter;


import com.lhj.bluelibrary.ble.callback.BlueToothComServiceCallBack;
import com.lhj.bluelibrary.ble.callback.BlueToothScanServiceCallBack;
import com.lhj.bluelibrary.ble.callback.PushCallBack;
import com.lhj.bluelibrary.ble.entity.ScanEntity;
import com.lhj.bluelibrary.ble.push.BlueToothInstance;
import com.lhj.bluelibrary.ble.push.InformationPushCallBack;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/20.
 */
public class ConnectMainThread {
    private BlueToothController mBlueToothController;
    private BLeBroadcastReceiver myBleReceiver;
    private Context mContext;
    private BlueToothScanServiceCallBack blueToothScanServiceCallBack;
    private BlueToothComServiceCallBack blueToothComServiceCallBack;
    private PushCallBack pushCallBack;
    private int isConnect = DEFAULT;
    //过滤方式 1 设备名字过滤
    public static final int TYPE_NAME=BlueToothController.TYPE_NAME;
    //过滤方式 2 设备广播包UUID过滤
    public static final int TYPE_UUID=BlueToothController.TYPE_UUID;
    //过滤方式 3 设备MAC过滤
    public static final int TYPE_MAC=BlueToothController.TYPE_MAC;

    public static final int DEFAULT = 0;
    public static final int CONNECTTING = 1;
    public static final int CONNECTTED = 2;
    public static final int DISCONNECTTED = 3;

    public void initBlue(Context context){
        this.mContext=context;
        mBlueToothController=new BlueToothController();
        registerBlueReceiver(mBlueToothController);
        mBlueToothController.initBluetooth(context);
        initCallBack();
    }

    public void setFilterType(int type,ArrayList<String> typeStr){
        mBlueToothController.setFilterType(type,typeStr);
    }

    private void initCallBack(){
        mBlueToothController.setMsScanTimerDevicesCallback(new BlueToothController.ScanTimerDevicesCallback() {
            @Override
            public void getDevices(ArrayList<ScanEntity> scans) {
                if(blueToothScanServiceCallBack!=null){
                    blueToothScanServiceCallBack.getDevices(scans);
                }
            }

            @Override
            public void getBuleStatus(boolean blueswicth) {
                if(blueToothScanServiceCallBack!=null){
                    blueToothScanServiceCallBack.getBuleStatus(blueswicth);
                }
            }
        });

        mBlueToothController.setmDeviceMsgCallback(new BlueToothController.DeviceMsgCallBack() {
            @Override
            public void Connected(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, int status) {
                isConnect = CONNECTTED;
                if(blueToothComServiceCallBack!=null){
                    blueToothComServiceCallBack.Connected(gatt,c_msg,status);
                }
            }

            @Override
            public void Disconnect(BluetoothGatt gatt, int status) {
                isConnect = DISCONNECTTED;
                if(blueToothComServiceCallBack!=null){
                    blueToothComServiceCallBack.Disconnect(gatt,status);
                }
            }

            @Override
            public void getRssi(BluetoothGatt gatt, int status, int rssi) {
                if(blueToothComServiceCallBack!=null){
                    blueToothComServiceCallBack.getRssi(gatt,status,rssi);
                }
            }

            @Override
            public void notiCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, byte[] data) {
                if(blueToothComServiceCallBack!=null){
                    blueToothComServiceCallBack.notiCallBack(gatt,c_msg,data);
                }
            }
        });

        BlueToothInstance.getInstance().setInformationPushCallBack(new InformationPushCallBack() {
            @Override
            public void PhoneCallBack(String phonenum, int phonetype) {
                if(pushCallBack!=null){
                    pushCallBack.PhoneCallBack(phonenum, phonetype);
                }
            }

            @Override
            public void SMSCallBack(String phonenum, String smsstr) {
                if(pushCallBack!=null){
                    pushCallBack.SMSCallBack(phonenum, smsstr);
                }
            }

            @Override
            public void OtherAppCallBack(int pushEnum) {
                if(pushCallBack!=null){
                    pushCallBack.OtherAppCallBack(pushEnum);
                }
            }

            @Override
            public void EmialCallBack() {
                if(pushCallBack!=null){
                    pushCallBack.EmialCallBack();
                }
            }
        });

    }

    public void startScan(int scanms){
        mBlueToothController.startScan(scanms);
    }

    public void stopScan(){
        mBlueToothController.stopScan();
    }

    public void connect(BluetoothDevice device, String Service_UUID, String Char_NOTI_UUID, String Char_MSG_UUID, boolean autoConnect){
        isConnect = CONNECTTING;
        mBlueToothController.connect(device, Service_UUID, Char_NOTI_UUID, Char_MSG_UUID, autoConnect);
    }

    public void disconnect(){
        mBlueToothController.accordDisConnect();
    }

    public boolean writeResponseCommand(byte[] datas){
        boolean write = mBlueToothController.wirteValue(datas,false);
//        Log.e("Linhaojian","发送指令结果："+write);
        return write;
    }

    public boolean writeNoResponseCommand(byte[] datas){
        boolean write = mBlueToothController.wirteValue(datas,true);
//        Log.e("Linhaojian","发送指令结果："+write);
        return write;
    }

    /**
     *   注册蓝牙开关状态广播
     */
    private void registerBlueReceiver(BlueToothController mBlueToothController){
        myBleReceiver=new BLeBroadcastReceiver(mBlueToothController);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(myBleReceiver, filter);
    }

    public PushCallBack getPushCallBack() {
        return pushCallBack;
    }

    public void setPushCallBack(PushCallBack pushCallBack) {
        this.pushCallBack = pushCallBack;
    }

    /**
     *   注销蓝牙开关状态的广播
     */
    private void unregisterLeReceiver(){
        if(myBleReceiver!=null){
            mContext.unregisterReceiver(myBleReceiver);
        }
    }

    public void OnDestory(){
        unregisterLeReceiver();
        myBleReceiver = null;
        blueToothScanServiceCallBack = null;
        blueToothComServiceCallBack = null;
        pushCallBack = null;
        mBlueToothController.release();
    }

    public void dealConnectOT(){
        mBlueToothController.dealConnectOT();
    }

    public BlueToothScanServiceCallBack getBlueToothScanServiceCallBack() {
        return blueToothScanServiceCallBack;
    }

    public void setBlueToothScanServiceCallBack(BlueToothScanServiceCallBack blueToothScanServiceCallBack) {
        this.blueToothScanServiceCallBack = blueToothScanServiceCallBack;
    }

    public BlueToothComServiceCallBack getBlueToothComServiceCallBack() {
        return blueToothComServiceCallBack;
    }

    public void setBlueToothComServiceCallBack(BlueToothComServiceCallBack blueToothComServiceCallBack) {
        this.blueToothComServiceCallBack = blueToothComServiceCallBack;
    }

    public boolean getBlueAble(){
        return mBlueToothController.isBlueAble();
    }

    public int getConnectType() {
        return isConnect;
    }
}
