package com.lhj.bluelibrary.ble.bluetooth.ientrust;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lhj.bluelibrary.ble.BlueToothController;
import com.lhj.bluelibrary.ble.bluetooth.ientrust.SmartBluetooth;

/**
 * 蓝牙广播类：接收蓝牙广播，处理蓝牙开关操作
 * 蓝牙的4种状态：
 * 		1、BluetoothAdapter.STATE_OFF://10(处理蓝牙关闭后的事情)
 * 		2、BluetoothAdapter.STATE_TURNING_ON://11(一般不作处理)
 * 		3、BluetoothAdapter.STATE_ON://12(处理蓝牙打开后的事情)
 * 		4、BluetoothAdapter.STATE_TURNING_OFF://13(一般不作处理)
 * 
 * @author Administrator
 *
 */
public class BlueBroadcastReceiver extends BroadcastReceiver {
	private SmartBluetooth smartBluetooth;

	public BlueBroadcastReceiver(SmartBluetooth smartBluetooth){
		this.smartBluetooth = smartBluetooth;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
			            BluetoothAdapter.STATE_OFF);
				
				switch(btState){
				case BluetoothAdapter.STATE_OFF://10
					break;
				case BluetoothAdapter.STATE_TURNING_ON://11
					break;
				case BluetoothAdapter.STATE_ON://12
					smartBluetooth.reInit();
					smartBluetooth.onBlueStatus(true);
					break;
				case BluetoothAdapter.STATE_TURNING_OFF://13
					smartBluetooth.stopScan();
					smartBluetooth.onBlueStatus(false);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
