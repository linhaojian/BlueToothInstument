package com.lhj.bluelibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
public class BLeBroadcastReceiver extends BroadcastReceiver {
	private BlueToothController mbBlueToothController;
	
	public BLeBroadcastReceiver(BlueToothController mBlueToothController){
		this.mbBlueToothController=mBlueToothController;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
			            BluetoothAdapter.STATE_OFF);
				
				switch(btState){
				case BluetoothAdapter.STATE_OFF://10
					Log.e("BroadcastReceiver", "STATE_OFF="+btState);
					mbBlueToothController.setBlueAble(false);
					mbBlueToothController.stopScan();
					break;
				case BluetoothAdapter.STATE_TURNING_ON://11
					Log.e("BroadcastReceiver", "STATE_TURNING_ON="+btState);
					break;
				case BluetoothAdapter.STATE_ON://12
					Log.e("BroadcastReceiver", "STATE_ON="+btState);
					try {
						mbBlueToothController.setBlueAble(true);
						mbBlueToothController.reInitBluetooth();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case BluetoothAdapter.STATE_TURNING_OFF://13
					Log.e("BroadcastReceiver", "STATE_TURNING_OFF="+btState);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
