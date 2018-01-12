package com.lhj.bluelibrary.ble.bluetooth.advertisement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.BlueServerOperatorCallBack;


/**
 * Created by Administrator on 2017/5/27.
 */
public class BlueServerOperator{
    public static final int LOCK_OPEN = 1;
    public static final int LOCK_CLOSE = 2;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattServer bluetoothGattServer;
    private BluetToothServerListener bluetToothServerListener = new BluetToothServerListener();
    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private boolean isConnect;
    private byte randrom1,randrom2;
    private BlueServerOperatorCallBack blueServerOperatorCallBack;

    public BlueServerOperator(Context mContext){
        bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothGattServer = bluetoothManager.openGattServer(mContext,bluetToothServerListener);
        configuration();
    }

    private void configuration(){
        BluetoothGattService bs = new BluetoothGattService(ServerContacts.SERVICE_UUID.getUuid()
                ,BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic bcwrite = new BluetoothGattCharacteristic(ServerContacts.CHARACTERISTIC_WRITE_UUID.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_WRITE|BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE|BluetoothGattCharacteristic.PERMISSION_READ
        );
        bs.addCharacteristic(bcwrite);
        BluetoothGattCharacteristic bcnoti = new BluetoothGattCharacteristic(ServerContacts.CHARACTERISTIC_NOTI_UUID.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE|BluetoothGattCharacteristic.PERMISSION_READ
        );
        BluetoothGattDescriptor bd1 = new BluetoothGattDescriptor(ServerContacts.UUID_DESCRIPTOR.getUuid(), BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor bd2 = new BluetoothGattDescriptor(ServerContacts.UUID_DESCRIPTOR_NOTI.getUuid(), BluetoothGattDescriptor.PERMISSION_WRITE);
        bcnoti.addDescriptor(bd1);
        bcnoti.addDescriptor(bd2);
        bs.addCharacteristic(bcnoti);
        bluetoothGattServer.addService(bs);
    }

    public void close(){
        bluetoothGattServer.clearServices();
        bluetoothGattServer.close();
        bluetoothGattServer = null;
        isConnect = false;
        bluetoothDevice = null;
        bluetoothGattCharacteristic = null;
        blueServerOperatorCallBack = null;
    }

    public void blueUnable(){
        isConnect = false;
        isConnect = false;
        if(blueServerOperatorCallBack!=null){
            blueServerOperatorCallBack.OnDisConnect();
        }
    }

    public void sendCommand(byte[] bytes){
        if(isConnect&&bluetoothDevice!=null&&bluetoothGattCharacteristic!=null){
            //noti数据到主机
            bluetoothGattCharacteristic.setValue(bytes);
            bluetoothGattServer.notifyCharacteristicChanged(bluetoothDevice,bluetoothGattCharacteristic,false);
        }
    }

    public void setBlueServerOperatorCallBack(BlueServerOperatorCallBack blueServerOperatorCallBack){
        this.blueServerOperatorCallBack = blueServerOperatorCallBack;
    }

    class BluetToothServerListener extends BluetoothGattServerCallback{
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if(status==BluetoothGatt.GATT_SUCCESS&&newState== BluetoothProfile.STATE_CONNECTED){
                bluetoothDevice = device;
                isConnect = true;
                if(blueServerOperatorCallBack!=null){
                    blueServerOperatorCallBack.OnConnect();
                }
            }else{
                isConnect = false;
                if(blueServerOperatorCallBack!=null){
                    blueServerOperatorCallBack.OnDisConnect();
                }
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            bluetoothGattServer.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset,characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            //响应主机收到写入的指令
            bluetoothGattServer.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset,value);
            dealWriteResquest(value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            bluetoothGattServer.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset,descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            bluetoothGattServer.sendResponse(device,requestId, BluetoothGatt.GATT_SUCCESS,offset,value);
            bluetoothGattCharacteristic = descriptor.getCharacteristic();
        }
    }

    private void dealWriteResquest(byte[] bytes){
    }

    public void disconnect(){
    }

    private String toStringForBytes(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<bytes.length;i++){
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(" 0x"+hex);
        }
        return sb.toString();
    }
}
