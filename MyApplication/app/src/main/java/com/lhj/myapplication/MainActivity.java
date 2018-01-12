package com.lhj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lhj.bluelibrary.ble.bluetooth.BlueToothOperator;
import com.lhj.bluelibrary.ble.bluetooth.result.OnSmartBluetooth;
import com.lhj.bluelibrary.ble.bluetooth.util.BLEContacts;
import com.lhj.bluelibrary.ble.entity.ScanEntity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBlue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initDestroy();
    }

    private void initBlue(){
        //初始化
        BlueToothOperator.getInstance().initSDK(this);
    }

    private void initDestroy(){
        //销毁蓝牙库，释放内存
        BlueToothOperator.getInstance().release();
    }

    private void scan(){
        //初始化服务，特征，使能 ---- > UUID
        BlueToothOperator.getInstance().getSmartBluetooth().noti("","","");
        //过滤条件
        BlueToothOperator.getInstance().getSmartBluetooth().filter(BLEContacts.SCAN_NAME,new ArrayList<String>());
        //循环搜索
        BlueToothOperator.getInstance().getSmartBluetooth().startScan(3000);
    }

    private void stopScan(){
        BlueToothOperator.getInstance().getSmartBluetooth().stopScan();
    }

    private void connectForMac(String mac){
        BlueToothOperator.getInstance().getSmartBluetooth().connect(mac,true);
    }

    private void initListeners(){
        BlueToothOperator.getInstance().getSmartBluetooth().setOnSmartBluetooth(new OnSmartBluetooth() {
            @Override
            public void onBluetoothStatus(boolean bstatus) {

            }

            @Override
            public void onDeviceResult(ScanEntity scanEntity) {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onCharacteristicChanged(byte[] bytes) {

            }

            @Override
            public void onNotity() {

            }

            @Override
            public void onRssi(int rssi) {

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }


}
