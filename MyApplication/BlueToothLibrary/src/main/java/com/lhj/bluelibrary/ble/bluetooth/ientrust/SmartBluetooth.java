package com.lhj.bluelibrary.ble.bluetooth.ientrust;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.lhj.bluelibrary.ble.bluetooth.util.BLEContacts;
import com.lhj.bluelibrary.ble.bluetooth.entrust.ISmartBluetooth;
import com.lhj.bluelibrary.ble.bluetooth.result.GattResult;
import com.lhj.bluelibrary.ble.bluetooth.result.OnSmartBluetooth;
import com.lhj.bluelibrary.ble.bluetooth.result.ScanResult;
import com.lhj.bluelibrary.ble.bluetooth.util.ErrorMsg;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Administrator on 2017/6/26.
 */
public class SmartBluetooth implements ISmartBluetooth {
    private static final int HANDLER_DEVICES = 100;
    private static final int HANDLER_CONNECT = 101;
    private static final int HANDLER_DISCONNECT = 102;
    private static final int HANDLER_NOTITY = 103;
    private static final int HANDLER_CHANGE = 104;
    private static final int HANDLER_RSSI = 105;
    public static final int CONNECTING = 113;
    public static final int CONNECTTED = 114;
    public static final int DISCONNECTTED = 115;
    private Context mContext;
    private BluetoothAdapter adapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic writechar;
    private BlueBroadcastReceiver blueBroadcastReceiver;
    private Scan scan;
    private Connect connect;
    private OnSmartBluetooth onSmartBluetooth;
    private String serviceUUID,charwriteUUID,charnotiUUID;
    private boolean isReConnect = false;
    private int connectStatus = DISCONNECTTED;
    private String mac = "";
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
           dealHandler(msg);
        }
    };

    public SmartBluetooth(Context mContext){
        this.mContext = mContext;
        adapter = ((BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        registerBlueReceiver();
        init();
    }

    private void init(){
        if(adapter!=null&&adapter.isEnabled()){
            if(scan==null){
                scan = new Scan(adapter);
            }
            if(connect==null){
                connect = new Connect(mContext,adapter);
            }
        }
    }

    public void reInit(){
        if(scan==null){
            scan = new Scan(adapter);
        }
        if(connect==null){
            connect = new Connect(mContext,adapter);
        }
    }

    @Override
    public boolean getBlueStatus() {
        return adapter.isEnabled();
    }

    @Override
    public void filter(BLEContacts fitype, ArrayList<String> filters) {
        if(scan!=null){
            scan.filter(fitype,filters);
        }
    }

    @Override
    public void startScan(int scanmillisecond) {
        if(getBlueStatus()){
            scan.startScan(scanmillisecond, new ScanResult() {
                @Override
                public void getDevices(ScanEntity scanEntity) {
                    Message msg = Message.obtain(handler);
                    msg.what = HANDLER_DEVICES;
                    msg.obj = scanEntity;
                    msg.sendToTarget();
                }
            });
        }else{
            if(onSmartBluetooth!=null){
                onSmartBluetooth.onError(ErrorMsg.ERROR_SYSTEMBLUETOOTH_CLOSE,"System Bluetooth is unable!");
            }
        }
    }

    @Override
    public void stopScan() {
        if(scan!=null&&getBlueStatus()){
            scan.stopScan();
        }
    }

    @Override
    public void connect(String mac,boolean isReConnect) {
        if(getBlueStatus()){
            if(connectStatus==DISCONNECTTED){
                this.mac = mac;
                connectStatus = CONNECTING;
                this.isReConnect = isReConnect;
                bluetoothGatt=connect.connect(mac,gattResult);
            }else{
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onError(ErrorMsg.ERROR_CONNECTTED_OTHER,"You are connected to a device, please disconnect it first");
                }
            }
        }else{
            if(onSmartBluetooth!=null){
                onSmartBluetooth.onError(ErrorMsg.ERROR_SYSTEMBLUETOOTH_CLOSE,"System Bluetooth is unable!");
            }
        }
    }

    @Override
    public void connect(ScanEntity scanEntity,boolean isReConnect) {
        if(getBlueStatus()){
            if(connectStatus==DISCONNECTTED){
                this.mac = scanEntity.getMac();
                connectStatus = CONNECTING;
                this.isReConnect = isReConnect;
                bluetoothGatt=connect.connect(scanEntity,gattResult);
            }else{
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onError(ErrorMsg.ERROR_CONNECTTED_OTHER,"You are connected to a device, please disconnect it first");
                }
            }
        }else{
            if(onSmartBluetooth!=null){
                onSmartBluetooth.onError(ErrorMsg.ERROR_SYSTEMBLUETOOTH_CLOSE,"System Bluetooth is unable!");
            }
        }
    }

    @Override
    public void getRssi() {
        if(connectStatus==CONNECTTED){
            connect.getRssi(bluetoothGatt);
        }
    }

    @Override
    public boolean writeCommand(byte[] bytes, boolean isNotResponse) {
        boolean write = false;
        if(connectStatus==CONNECTTED&&bluetoothGatt!=null&&writechar!=null){
            write = connect.writeCommand(bluetoothGatt,writechar,bytes,isNotResponse);
        }
        return write;
    }

    @Override
    public void noti(String serviceUUID, String charwriteUUID, String charnotiUUID) {
        this.serviceUUID = serviceUUID;
        this.charwriteUUID = charwriteUUID;
        this.charnotiUUID = charnotiUUID;
    }

    @Override
    public void disconnect(boolean isReConnect) {
        if(bluetoothGatt!=null){
            this.isReConnect = isReConnect;
            if(connectStatus==CONNECTTED){
                connect.disconnect(bluetoothGatt);
            }else if(connectStatus==CONNECTING){
                connect.disconnect(bluetoothGatt);
                gattResult.OnDisconnectted();
            }
        }
    }

    @Override
    public void close() {
        if(bluetoothGatt!=null){
            connect.close(bluetoothGatt);
            bluetoothGatt=null;
        }
    }

    @Override
    public void release() {
        unregisterLeReceiver();
        if(connectStatus==CONNECTTED){
            disconnect(false);
        }
        writechar = null;
        adapter = null;
        blueBroadcastReceiver = null;
        scan = null;
        connect = null;
        isReConnect = false;
        handler = null;
    }

    @Override
    public void setOnSmartBluetooth(OnSmartBluetooth onSmartBluetooth) {
        this.onSmartBluetooth = onSmartBluetooth;
    }

    @Override
    public void setReConnect(boolean isReConnect) {
        this.isReConnect = isReConnect;
    }

    @Override
    public int getConnectStatus() {
        return connectStatus;
    }

    private void registerBlueReceiver(){
        blueBroadcastReceiver=new BlueBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(blueBroadcastReceiver, filter);
    }

    private void unregisterLeReceiver(){
        if(blueBroadcastReceiver!=null){
            mContext.unregisterReceiver(blueBroadcastReceiver);
            blueBroadcastReceiver = null;
        }
    }

    public void onBlueStatus(boolean bluestatus){
        if(onSmartBluetooth!=null){
            onSmartBluetooth.onBluetoothStatus(bluestatus);
        }
    }

    private GattResult gattResult = new GattResult() {
        @Override
        public void OnConnectted() {
            if(!TextUtils.isEmpty(serviceUUID)&&!TextUtils.isEmpty(charwriteUUID)){
                BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(serviceUUID));
                writechar = bluetoothGattService.getCharacteristic(UUID.fromString(charwriteUUID));
                if(!TextUtils.isEmpty(charnotiUUID)){
                    connect.Notity(bluetoothGatt,serviceUUID,charnotiUUID);
                }
            }
            connectStatus = CONNECTTED;
            handler.sendEmptyMessage(HANDLER_CONNECT);
        }

        @Override
        public void OnDisconnectted() {
            connectStatus = DISCONNECTTED;
            handler.sendEmptyMessage(HANDLER_DISCONNECT);
            if(isReConnect){
                close();
                try {
                    Thread.sleep(200);
                    connect(mac,true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                close();
            }
        }

        @Override
        public void onCharacteristicRead(byte[] bytes) {
            Message msg = Message.obtain(handler);
            msg.what = HANDLER_CHANGE;
            msg.obj = bytes;
            msg.sendToTarget();
        }

        @Override
        public void onReadRemoteRssi(int rssi) {
            Message msg = Message.obtain(handler);
            msg.what = HANDLER_RSSI;
            msg.obj = rssi;
            msg.sendToTarget();
        }

        @Override
        public void onCharacteristicChanged(byte[] bytes) {
            Message msg = Message.obtain(handler);
            msg.what = HANDLER_CHANGE;
            msg.obj = bytes;
            msg.sendToTarget();
        }

        @Override
        public void onDescriptorWrite(String serviceUUID, String characteristicsUUID) {
            handler.sendEmptyMessage(HANDLER_NOTITY);
        }
    };

    private void dealHandler(Message msg){
        switch (msg.what){
            case HANDLER_DEVICES:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onDeviceResult((ScanEntity) msg.obj);
                }
                break;
            case HANDLER_CONNECT:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onConnect();
                }
                break;
            case HANDLER_DISCONNECT:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onDisconnect();
                }
                break;
            case HANDLER_NOTITY:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onNotity();
                }
                break;
            case HANDLER_CHANGE:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onCharacteristicChanged((byte[]) msg.obj);
                }
                break;
            case HANDLER_RSSI:
                if(onSmartBluetooth!=null){
                    onSmartBluetooth.onRssi((int) msg.obj);
                }
                break;
        }
    }

}
