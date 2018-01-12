package com.lhj.bluelibrary.ble.bluetooth.ientrust;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.ParcelUuid;

import com.lhj.bluelibrary.ble.bluetooth.entrust.IConnect;
import com.lhj.bluelibrary.ble.bluetooth.result.GattResult;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/22.
 */
public class Connect implements IConnect {
    private String CHAR_DESP_NOTI_UUID="00002902-0000-1000-8000-00805f9b34fb";
    BluetoothAdapter bluetoothAdapter;
    Context context;
    ParcelUuid[] parcelUuids;

    public Connect(Context context,BluetoothAdapter bluetoothAdapter){
        this.bluetoothAdapter = bluetoothAdapter;
        this.context = context;
    }

    @Override
    public BluetoothGatt connect(ScanEntity scanEntity, GattResult gattResult) {
        return connect(scanEntity.getDevice(),gattResult);
    }

    @Override
    public BluetoothGatt connect(String mac, GattResult gattResult) {
        return connect(bluetoothAdapter.getRemoteDevice(mac),gattResult);
    }

    @Override
    public void getRssi(BluetoothGatt gatt) {
        gatt.readRemoteRssi();
    }

    @Override
    public boolean writeCommand(BluetoothGatt gatt,BluetoothGattCharacteristic bluetoothGattCharacteristics, byte[] bytes, boolean isNoResponse) {
        if(isNoResponse){
            bluetoothGattCharacteristics.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }else{
            bluetoothGattCharacteristics.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
        bluetoothGattCharacteristics.setValue(bytes);
        return gatt.writeCharacteristic(bluetoothGattCharacteristics);
    }

    @Override
    public void Notity(BluetoothGatt gatt,String serviceUUID, String characteristicsUUID) {
        BluetoothGattCharacteristic character = gatt.getService(UUID.fromString(serviceUUID)).getCharacteristic(UUID.fromString(characteristicsUUID));
        if(gatt.setCharacteristicNotification(character,true)){
            BluetoothGattDescriptor config = character.getDescriptor(UUID.fromString(CHAR_DESP_NOTI_UUID));
            byte[] configValue = true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            config.setValue(configValue);
            gatt.writeDescriptor(config);
        }
    }

    @Override
    public void disconnect(BluetoothGatt bluetoothGatt) {
        bluetoothGatt.disconnect();
    }

    @Override
    public void close(BluetoothGatt bluetoothGatt) {
        bluetoothGatt.close();
    }

    private BluetoothGatt connect(BluetoothDevice bluetoothDevice, final GattResult gattResult){
        return bluetoothDevice.connectGatt(context, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if(status==BluetoothGatt.GATT_SUCCESS&&newState== BluetoothProfile.STATE_CONNECTED){
                    if(gatt!=null){
                        gatt.discoverServices();
                    }
                }else{
                    gattResult.OnDisconnectted();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if(status==BluetoothGatt.GATT_SUCCESS){
                    gattResult.OnConnectted();
                }else{
                    gattResult.OnDisconnectted();
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                gattResult.onReadRemoteRssi(rssi);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                gattResult.onDescriptorWrite(descriptor.getCharacteristic().getService().getUuid().toString(),
                        descriptor.getCharacteristic().getUuid().toString());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                gattResult.onCharacteristicChanged(characteristic.getValue());
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                gattResult.onCharacteristicRead(characteristic.getValue());
            }
        });
    }



}
