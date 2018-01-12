package com.lhj.bluelibrary.ble.bluetooth.advertisement;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;

import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.AdvertisementCallback;


/**
 * Created by Administrator on 2017/5/26.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AdvertisementOperator {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private AdvertisementCallback advertisementCallback;
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            if(advertisementCallback!=null){
                advertisementCallback.onStartSuccess();
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            if(advertisementCallback!=null){
                advertisementCallback.onStartFailure(errorCode);
            }
        }
    };

    public AdvertisementOperator(BluetoothAdapter bluetoothAdapt){
        this.bluetoothAdapter = bluetoothAdapt;
        bluetoothAdapter.setName(ServerContacts.ADVERT_NAME);
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    public AdvertisementOperator(Context mContext){
        bluetoothAdapter = ((BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        bluetoothAdapter.setName(ServerContacts.ADVERT_NAME);
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    public void startAdvert(){
        if(bluetoothAdapter.isEnabled()){
            bluetoothLeAdvertiser.startAdvertising(buildAdvertiseSettings(),buildAdvertiseData(),buildAdvertiseDataScan(),advertiseCallback);
        }else{
            if(advertisementCallback!=null){
                advertisementCallback.onBlueToothStatuError();
            }
        }
    }

    public void stopAdvert(){
        if(bluetoothAdapter.isEnabled()){
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }else{
            if(advertisementCallback!=null){
                advertisementCallback.onBlueToothStatuError();
            }
        }
    }

    public void setAdvertisementCallback(AdvertisementCallback advertisementCallback){
        this.advertisementCallback = advertisementCallback;
    }

    /**
     * Returns an AdvertiseData object which includes the Service UUID and Device Name.
     */
    private AdvertiseData buildAdvertiseData() {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(ServerContacts.ADVERT_UUID);
//        dataBuilder.setIncludeDeviceName(true);
//        dataBuilder.addManufacturerData(ServerContacts.MANUFACTRUE_ID,ServerContacts.MANUFACTRUE_BYTES);
        return dataBuilder.build();
    }

    private AdvertiseData buildAdvertiseDataScan() {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.setIncludeDeviceName(true);
        dataBuilder.addManufacturerData(ServerContacts.MANUFACTRUE_ID,ServerContacts.MANUFACTRUE_BYTES);
        return dataBuilder.build();
    }

    /**
     * Returns an AdvertiseSettings object set to use low power (to help preserve battery life)
     * and disable the built-in timeout since this code uses its own timeout runnable.
     */
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
//        settingsBuilder.setTimeout(0);
//        settingsBuilder.setTxPowerLevel(-7);
        settingsBuilder.setConnectable(true);
        return settingsBuilder.build();
    }

    public boolean getBlueEnable(){
        return bluetoothAdapter.isEnabled();
    }

}
