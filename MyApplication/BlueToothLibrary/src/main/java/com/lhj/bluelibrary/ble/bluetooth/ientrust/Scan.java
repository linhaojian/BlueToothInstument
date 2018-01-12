package com.lhj.bluelibrary.ble.bluetooth.ientrust;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lhj.bluelibrary.ble.bluetooth.util.BLEContacts;
import com.lhj.bluelibrary.ble.bluetooth.entrust.IScan;
import com.lhj.bluelibrary.ble.bluetooth.result.ScanResult;
import com.lhj.bluelibrary.ble.entity.ScanEntity;
import com.lhj.bluelibrary.ble.until.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/21.
 */
public class Scan implements IScan {
    BluetoothAdapter bluetoothAdapter;
    BluetoothAdapter.LeScanCallback leScanCallback;
    ScanCallback scanCallback;
    BluetoothLeScanner bluetoothLeScanner;
    Timer scanTimer;
    BLEContacts fitype = BLEContacts.SCAN_MAC;
    ArrayList<String> filters = new ArrayList<>();
    ScanResult scanResult;
    boolean isScan;
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(!isScan){
                return;
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                bluetoothLeScanner.stopScan(scanCallback);
                bluetoothLeScanner.startScan(scanCallback);
            }else{
                bluetoothAdapter.stopLeScan(leScanCallback);
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        }
    };

    public Scan(BluetoothAdapter bluetoothAdapter){
        this.bluetoothAdapter = bluetoothAdapter;
        init();
    }

    private void init(){
        scanTimer = new Timer();
        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                dealResult(device,rssi,scanRecord);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            scanCallback = new ScanCallback() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                    dealResult(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes());
                }

                @Override
                public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
                }

                @Override
                public void onScanFailed(int errorCode) {
                }
            };
        }
    }

    @Override
    public void startScan(int scanmillisecond, ScanResult scanResult) {
        isScan = true;
        this.scanResult = scanResult;
        if(scanTimer!=null){
            scanTimer.cancel();
            scanTimer = null;
        }
        scanTimer = new Timer();
        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },0,scanmillisecond);
    }

    @Override
    public void stopScan() {
        isScan = false;
        if(scanTimer!=null){
            scanTimer.cancel();
//            handler.removeCallbacksAndMessages(null);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            bluetoothLeScanner.stopScan(scanCallback);
        }else{
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    @Override
    public void filter(BLEContacts fitype, ArrayList<String> filters) {
        this.fitype = fitype;
        this.filters = filters;
    }

    private void dealResult(BluetoothDevice device, int rssi, byte[] scanRecord){
        if(isScan){
            ScanEntity scanEntity = new ScanEntity();
            scanEntity.setMac(device.getAddress());
            scanEntity.setDevice(device);
            scanEntity.setName(device.getName());
            scanEntity.setRssi(rssi);
            scanEntity.setScanRecoder(scanRecord);
            scanEntity.setScanRecodestr(Tools.splitType(Tools.splitScanRecode(Tools.bytesToHexString(scanRecord))));
            String uuid = Tools.splitType(Tools.splitScanRecode(Tools.bytesToHexString(scanRecord)))[0];
            if(fitype==BLEContacts.SCAN_MAC){
                if(filters.contains(device.getAddress())){
                    scanResult.getDevices(scanEntity);
                }
            }else if(fitype==BLEContacts.SCAN_NAME){
                if(filters.contains(device.getName())){
                    scanResult.getDevices(scanEntity);
                }
            }else if(fitype==BLEContacts.SCAN_UUID){
                if(filters.contains(uuid)){
                    scanResult.getDevices(scanEntity);
                }
            }
        }
    }

}
