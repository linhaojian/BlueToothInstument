package com.lhj.bluelibrary.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;

public class BlueToothControllerBackCall extends BluetoothGattCallback {
	private String SERI_UUID="0000ffd0-0000-1000-8000-00805f9b34fb";
	private String CHAR_NOTI_UUID="0000ffd2-0000-1000-8000-00805f9b34fb";
	private String CHAR_DESP_NOTI_UUID="00002902-0000-1000-8000-00805f9b34fb";
	private String CHAR_MSG_UUID="0000ffd1-0000-1000-8000-00805f9b34fb";
	private BluetoothGatt gatt;
	private BluetoothGattCharacteristic characteristic;

	
	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// noti接口
		this.gatt=gatt;
		mBleStauscallback.notiCallBack(gatt, characteristic,characteristic.getValue());
	}
	
	@Override
	public void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		//特征读取接口
		if(status==BluetoothGatt.GATT_SUCCESS){
			
		}else{
			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
		}
	}
	
	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		// 特征写入接口
//		if(status==BluetoothGatt.GATT_SUCCESS){
//			
//		}else{
//			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
//		}
	}
	
	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status,
			int newState) {
		// 连接状态接口
		if(status==BluetoothGatt.GATT_SUCCESS&&newState==BluetoothProfile.STATE_CONNECTED){//初步连接成功
			//支持服务
			Log.i("Linhaojian","支持服务："+(gatt.discoverServices()));
		}else{
			if(gatt!=null){
				gatt.close();
			}
			if(this.gatt!=null){
				this.gatt.close();
			}
			mBleStauscallback.Disconnect(gatt, status);
		}
	}
	
	@Override
	public void onDescriptorRead(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
		// 描述读取接口
		if(status==BluetoothGatt.GATT_SUCCESS){
			
		}else{
			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
		}
	}
	
	@Override
	public void onDescriptorWrite(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
		// 描述写入接口
//		if(status==BluetoothGatt.GATT_SUCCESS){
			// 获取服务项
			BluetoothGattService s = gatt.getService(UUID.fromString(SERI_UUID));
			// 获取特征--noti/msg
			BluetoothGattCharacteristic c_msg = s.getCharacteristic(UUID.fromString(CHAR_MSG_UUID));
			this.gatt=gatt;
			this.characteristic=c_msg;
			mBleStauscallback.Connected(gatt,c_msg, status);
//		}else{
//			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
//		}
	}
	
	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		// 读取信号强度的接口
		if(status==BluetoothGatt.GATT_SUCCESS){
			mBleStauscallback.getRssi(gatt, status, rssi);
		}else{
			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
		}
	}
	
	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		// 支持服务的接口
		if(status==BluetoothGatt.GATT_SUCCESS){
			if(!TextUtils.isEmpty(SERI_UUID)&&!TextUtils.isEmpty(CHAR_NOTI_UUID)&&!TextUtils.isEmpty(CHAR_MSG_UUID)){
				  // 获取服务项
				BluetoothGattService s = gatt.getService(UUID.fromString(SERI_UUID));
				// 获取特征--noti/msg
				BluetoothGattCharacteristic c_noti = s.getCharacteristic(UUID.fromString(CHAR_NOTI_UUID));
				//使能
				if(gatt.setCharacteristicNotification(c_noti, true)){
					BluetoothGattDescriptor config = c_noti.getDescriptor(UUID.fromString(CHAR_DESP_NOTI_UUID));
					byte[] configValue = true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
					config.setValue(configValue);
					Log.i("Linhaojian", "使能："+gatt.writeDescriptor(config));
				}
			}else if(!TextUtils.isEmpty(SERI_UUID)&&TextUtils.isEmpty(CHAR_NOTI_UUID)&&!TextUtils.isEmpty(CHAR_MSG_UUID)){
				Log.i("Linhaojian", "不需要使能的连接成功！");
				// 获取服务项
				BluetoothGattService s = gatt.getService(UUID.fromString(SERI_UUID));
				// 获取特征--noti/msg
				BluetoothGattCharacteristic c_msg = s.getCharacteristic(UUID.fromString(CHAR_MSG_UUID));
				mBleStauscallback.Connected(gatt,c_msg, status);
			}else{
				mBleStauscallback.Connected(gatt,null, status);
			}
			
		}else{
			close(gatt);
//			mBleStauscallback.Disconnect(gatt, status);
		}
	}
	
	/**
	 *   向下位机写入数据
	 * @param bytes  数据或者命令
	 * @return  成功返回true
	 */
	public boolean wirteValue(byte[] bytes,boolean response){
		if(characteristic!=null){
			if(response){
				characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			}else{
			}
			characteristic.setValue(bytes);
			return gatt.writeCharacteristic(characteristic);
		}
		return false;
	}
	
	/**
	 *   获取连上的RSSI
	 * @return  rssi
	 */
	public boolean getRssi(){
		return gatt.readRemoteRssi();
	}
	
	
	/**
	 *   初始化服务项的UUID；noti的UUID；通信的UUID
	 * @param Service_UUID
	 * @param Char_NOTI_UUID
	 * @param Char_MSG_UUID
	 */
	public void setUUID(String Service_UUID,String Char_NOTI_UUID,String Char_MSG_UUID){
		this.CHAR_MSG_UUID=Char_MSG_UUID;
		this.CHAR_NOTI_UUID=Char_NOTI_UUID;
		this.SERI_UUID=Service_UUID;
	}
	
	/**
	 *   关闭gatt
	 * @param gatt
	 */
	private void close(BluetoothGatt gatt){
		if(gatt!=null){
			gatt.disconnect();
			gatt.close();
//			gatt=null;
		}
		if(this.gatt!=null){
			this.gatt.disconnect();
			this.gatt.close();
//			gatt=null;
		}
	}
	
	public interface BleStatuscallback{
		void Connected(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, int status);//不需要noti，BluetoothGattCharacteristic设置为Null
		void Disconnect(BluetoothGatt gatt, int status);
		void getRssi(BluetoothGatt gatt, int status, int rssi);
		void notiCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic c_msg, byte[] data);
	};
	
	private BleStatuscallback mBleStauscallback;

	public void setBleStauscallback(BleStatuscallback mBleStauscallback) {
		this.mBleStauscallback = mBleStauscallback;
	}

	/**
	 * Clears the device cache. After uploading new firmware the DFU target will have other services than before.
	 *
	 * @param gatt  the GATT device to be refreshed
	 * @param force <code>true</code> to force the refresh
	 */
	private void refreshDeviceCache(final BluetoothGatt gatt, final boolean force) {
		/*
		 * If the device is bonded this is up to the Service Changed characteristic to notify Android that the services has changed.
		 * There is no need for this trick in that case.
		 * If not bonded, the Android should not keep the services cached when the Service Changed characteristic is present in the target device database.
		 * However, due to the Android bug (still exists in Android 5.0.1), it is keeping them anyway and the only way to clear services is by using this hidden refresh method.
		 */
		if (force || gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
			/*
			 * There is a refresh() method in BluetoothGatt class but for now it's hidden. We will call it using reflections.
			 */
			try {
				final Method refresh = gatt.getClass().getMethod("refresh");
				if (refresh != null) {
					final boolean success = (Boolean) refresh.invoke(gatt);
				}
			} catch (Exception e) {
			}
		}
	}


}
