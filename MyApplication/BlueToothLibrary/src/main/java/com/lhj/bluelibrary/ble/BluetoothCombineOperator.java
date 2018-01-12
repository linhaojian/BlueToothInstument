package com.lhj.bluelibrary.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.lhj.bluelibrary.ble.callback.BlueToothComServiceCallBack;
import com.lhj.bluelibrary.ble.callback.BlueToothScanServiceCallBack;
import com.lhj.bluelibrary.ble.callback.MainBleControllerListener;
import com.lhj.bluelibrary.ble.callback.PushCallBack;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/31.
 */
public class BluetoothCombineOperator {
    private static final int MSG_CONNECTTIMEOUT=0;
    private static final int MSG_CONNECTTED=1;
    private static final int MSG_DISCONNECTTED=2;
    private static final int MSG_DATA=3;
    private static final int MSG_RSSI=5;
    private static final int MSG_DEVICES=6;
    private static final int MSG_BLUETOOTH_STATUS=7;
    private static BluetoothCombineOperator bluetoothCombineOperator;
    private ConnectMainThread mBlueToothService;
    private String serviceUUID,charmsgUUID,notiUUID;
    private int scantime = 2000;
    private int connectOutTime = 15000;
    private boolean isConnect;
    private OutTimeHandler mOutTimeHandler;
    private MainBleControllerListener mBleControllerListener;
    public static BluetoothCombineOperator getInstance(){
        if(bluetoothCombineOperator ==null){
            synchronized (BluetoothCombineOperator.class){
                if(bluetoothCombineOperator ==null){
                    bluetoothCombineOperator = new BluetoothCombineOperator();
                }
            }
        }
        return bluetoothCombineOperator;
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_DEVICES:
                    ArrayList<ScanEntity> scans = (ArrayList<ScanEntity>) msg.obj;
                    if(mBleControllerListener!=null){
                        mBleControllerListener.getDevices(scans);
                    }
                    break;
                case MSG_CONNECTTED:
                    mOutTimeHandler.cancelConnectTimer();
                    isConnect = true;
                    if(mBleControllerListener!=null){
                        mBleControllerListener.connectted();
                    }
                    break;
                case MSG_DISCONNECTTED:
                    isConnect = false;
                    mOutTimeHandler.cancelConnectTimer();
                    if(mBleControllerListener!=null){
                        mBleControllerListener.disconnectted();
                    }
                    break;
                case MSG_DATA:
                    byte[] datas = (byte[]) msg.obj;
                    if(mBleControllerListener!=null){
                        mBleControllerListener.getDatas(datas);
                    }
                    break;
                case MSG_CONNECTTIMEOUT:
                    dealConnectOT();
                    break;
                case MSG_RSSI:
                    int rssi = (int) msg.obj;
                    if(mBleControllerListener!=null){
                        mBleControllerListener.getRssi(rssi);
                    }
                    break;
                case MSG_BLUETOOTH_STATUS:
                    if(mBleControllerListener!=null){
                        mBleControllerListener.getBuleStatus((Boolean) msg.obj);
                    }
                    break;
            }
        }
    };

    public void initSDK(Context context){
        mBlueToothService = new ConnectMainThread();
        mBlueToothService.initBlue(context);
        mOutTimeHandler = new OutTimeHandler();
        initListeners();
    }

    public void setFilterType(int type,ArrayList<String> typeStr){
        mBlueToothService.setFilterType(type,typeStr);
    }

    public void setScanTime(int millisecond){
        scantime = millisecond;
    }

    public void setConnectTimeOut(int millisecond){
        connectOutTime = millisecond;
    }

    public void initUUID(String seUUID,String cmUUID,String ntUUID){
        this.serviceUUID = seUUID;
        this.charmsgUUID = cmUUID;
        this.notiUUID = ntUUID;
    }

    private void initListeners() {
        mBlueToothService.setBlueToothScanServiceCallBack(new BlueToothScanServiceCallBack() {
            @Override
            public void getDevices(ArrayList<ScanEntity> scans) {
                if(mHandler!=null){
                    Message msg = Message.obtain(mHandler);
                    msg.what = MSG_DEVICES;
                    msg.obj = scans;
                    msg.sendToTarget();
                }
            }

            @Override
            public void getBuleStatus(boolean blueswicth) {
                if(mHandler!=null){
                    Message msg = Message.obtain(mHandler);
                    msg.what = MSG_BLUETOOTH_STATUS;
                    msg.obj = blueswicth;
                    msg.sendToTarget();
                }
            }
        });
        mBlueToothService.setBlueToothComServiceCallBack(new BlueToothComServiceCallBack() {
            @Override
            public void Connected(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, int status) {
                if(mHandler!=null){
                    mHandler.sendEmptyMessage(MSG_CONNECTTED);
                }
            }

            @Override
            public void Disconnect(BluetoothGatt gatt, int status) {
                if(mHandler!=null){
                    mHandler.sendEmptyMessage(MSG_DISCONNECTTED);
                }
            }

            @Override
            public void getRssi(BluetoothGatt gatt, int status, int rssi) {
                if(mHandler!=null){
                    Message msg = Message.obtain(mHandler);
                    msg.what = MSG_RSSI;
                    msg.obj = rssi;
                    msg.sendToTarget();
                }
            }

            @Override
            public void notiCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, byte[] data) {
                if(mHandler!=null){
                    Message msg = Message.obtain(mHandler);
                    msg.what = MSG_DATA;
                    msg.obj = data;
                    msg.sendToTarget();
                }
            }
        });
    }

    public void destorySDK(){
        mBleControllerListener = null;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mOutTimeHandler.onDestroy();
        mBlueToothService.OnDestory();
    }

    public void connect(ScanEntity scan){
        BluetoothDevice device = scan.getDevice();
        mBlueToothService.connect(device,serviceUUID,notiUUID,charmsgUUID,false);
        mOutTimeHandler.setConnectTimer(connectOutTime, new OutTimeHandler.OutTimerListeners() {
            @Override
            public void outtime() {
                if(mHandler!=null){
                    mHandler.sendEmptyMessage(MSG_CONNECTTIMEOUT);
                }
            }
        });
    }

    public void connect(ScanEntity scan,int connectOutTime){
        this.connectOutTime = connectOutTime;
        connect(scan);
    }

    public void disconnect(){
        mBlueToothService.disconnect();
    }

    public void startScan(){
        mBlueToothService.startScan(scantime);
    }

    public void startScan(int millisecond){
        this.scantime = millisecond;
        startScan();
    }

    public void stopScan(){
        mBlueToothService.stopScan();
    }

    public boolean writeCommand(byte[] datas,boolean isNoResponse){
        boolean iswrite = false;
        if(isNoResponse){
            iswrite = mBlueToothService.writeNoResponseCommand(datas);
        }else{
            iswrite = mBlueToothService.writeResponseCommand(datas);
        }
        return iswrite;
    }

    private void dealConnectOT(){
        mBlueToothService.dealConnectOT();
    }

    public void setOnBleControllerListener(MainBleControllerListener mBleControllerListener){
        this.mBleControllerListener = mBleControllerListener;
    }

    public void setOnPushCallBack(PushCallBack pushCallBack){
        mBlueToothService.setPushCallBack(pushCallBack);
    }

    public boolean getBlueAble(){
        return mBlueToothService.getBlueAble();
    }

    public boolean isConnect(){return isConnect;}

}
