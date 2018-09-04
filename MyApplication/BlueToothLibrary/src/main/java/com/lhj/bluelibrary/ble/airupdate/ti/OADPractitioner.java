package com.lhj.bluelibrary.ble.airupdate.ti;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Administrator on 2017/1/5.
 */
public class OADPractitioner extends Service {
    private static final String TAG = "OADPractitioner";
    private static final int OAD_BLOCK_SIZE = 16;
    private static final int OAD_BUFFER_SIZE = 18;
    private static final int OAD_IMG_HDR_SIZE = 8;
    public static final String MAC = "mac";
    public static final String BYTESA = "bytesa";
    public static final String BYTESB = "bytesb";
    public static final String BROADCAST_OAD_START = "ti.oad.start";
    public static final String BROADCAST_OAD_STARTTING = "ti.oad.startting";
    public static final String BROADCAST_OAD_ERROR = "ti.oad.error";

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothGattService oadService,connintervalService;
    private BluetoothGattCharacteristic charIdentify,charBlock,charinterval;
    private TIBlueCallBack tiBlueCallBack;
    private short lenA,lenB,ver,blockNumber;//ver : 0-A ,1-B
    private byte[] vluBytesA,vluBytesB;
    private byte[] fileBytesA,fileBytesB;
    private volatile boolean isSendZoo,isConInter,isUpGra;
    private int blockBufferCount;
    private volatile boolean isUpgradeSuccess;
    private Object mLock = new Object();
    private OADThread oadThread;
    private volatile boolean isBlockThread;
    private Timer conOutTimer;
    private volatile boolean isNotConIntervalService;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        oadThread = new OADThread(intent);
        oadThread.start();
//        String mac = intent.getStringExtra(MAC);
//        byte[] byteas = intent.getByteArrayExtra(BYTESA);
//        byte[] bytebs = intent.getByteArrayExtra(BYTESB);
//        startOAD(mac,byteas,bytebs);
        return new MyBinder();
    }

    class MyBinder extends Binder{
        //此方法是为了可以在Acitity中获得服务的实例
        public OADPractitioner getService() {
            return OADPractitioner.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void init() {
        this.context = this;
        tiBlueCallBack = new TIBlueCallBack();
        bluetoothManager = (BluetoothManager)this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            }
        };
        bluetoothAdapter.startLeScan(leScanCallback);
        isBlockThread = false;
    }

    private void startOAD(String mac,byte[] bytesA,byte[] bytesB){
        if(bluetoothAdapter!=null&&bluetoothAdapter.isEnabled()){
            dealUpgradeStatus(TIOADProfile.UPGRADE_START,0);
            //获取手机当前已连接的设备信息
//            List list = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            fileBytesA = bytesA;
            fileBytesB = bytesB;
            lenA = (short) (Conversion.buildUint16(bytesA[7],bytesA[6]) / 4);
            lenB = (short) (Conversion.buildUint16(bytesB[7],bytesB[6]) / 4);
            vluBytesA = new byte[OAD_IMG_HDR_SIZE+4];
            System.arraycopy(bytesA,4,vluBytesA,0,8);
            vluBytesB = new byte[OAD_IMG_HDR_SIZE+4];
            System.arraycopy(bytesB,4,vluBytesB,0,8);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
            bluetoothAdapter.stopLeScan(leScanCallback);
            bluetoothGatt = device.connectGatt(context,false,tiBlueCallBack);
        }else{
            //  蓝牙不可用或者蓝牙没有打开
            dealError(TIOADProfile.BLUETOOTH_UNABLE_ERROR);
        }
    }

    private void discoverServices(){
        boolean isdis = bluetoothGatt.discoverServices();
    }

    private void initServiceAndCharateristic(){
        oadService = bluetoothGatt.getService(UUID.fromString(TIOADProfile.oadService_UUID));
        if(oadService==null){
            ThrowNull("oadService is null,do not upgrade");
        }else{
            charIdentify = oadService.getCharacteristic(UUID.fromString(TIOADProfile.oadImageNotify_UUID));
            charBlock = oadService.getCharacteristic(UUID.fromString(TIOADProfile.oadBlockRequest_UUID));
            if(charIdentify==null||charBlock==null){
                ThrowNull("oadCharracteristic is null,do not upgrade");
            }
            charIdentify.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            charBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }
        connintervalService = bluetoothGatt.getService(UUID.fromString(TIOADProfile.connectintervalService_UUID));
        if(connintervalService==null){
//            ThrowNull("connintervalService is null,do not upgrade");
            isNotConIntervalService = true;
        }else{
            charinterval = connintervalService.getCharacteristic(UUID.fromString(TIOADProfile.connectintervalChar_UUID));
            if(charinterval==null){
                ThrowNull("connintervalCharracteristic is null,do not upgrade");
            }
        }
        notiOADCharacteristic(charBlock);
    }

    private void notiOADCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(bluetoothGatt.setCharacteristicNotification(characteristic,true)){
            BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(TIOADProfile.CLIENT_CHARACTERISTIC_CONFIG));
            if(clientConfig!=null){
                clientConfig
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(clientConfig);
            }else{
                throw new NullPointerException("BluetoothGattDescriptor is null ,do not notity");
            }
        }
    }

    private boolean writeCharateristics(BluetoothGattCharacteristic characteristic,byte[] bytes){
        characteristic.setValue(bytes);
        boolean iswrite = bluetoothGatt.writeCharacteristic(characteristic);
        return iswrite;
    }

    private void dealCharateristicsReturn(BluetoothGattCharacteristic characteristic){
        byte[] buffer = characteristic.getValue();
        String uuidchar = characteristic.getUuid().toString();
        if(uuidchar.equalsIgnoreCase(charIdentify.getUuid().toString())){
            if(!isUpGra){
                if(buffer.length==8&&!isSendZoo){
                    isSendZoo = true;
                    ver = (short) (((Conversion.buildUint16(buffer[1],buffer[0]) & 1)==1)?1:0);
                    writeCharateristics(charIdentify,new byte[]{1});
                }else if(buffer.length==8&&isSendZoo){
                    ver = (short) (((Conversion.buildUint16(buffer[1],buffer[0]) & 1)==1)?1:0);
                    if(isNotConIntervalService){
                        mHandler.sendEmptyMessage(TIOADProfile.ONCHARATERISTICSWRITE);
                    }else{
                        byte[] buf = new byte[]{12, 0, 12, 0, 0, 0, 50, 0};
                        writeCharateristics(charinterval, buf);
                    }
                }
            }
        }else if(uuidchar.equalsIgnoreCase(charBlock.getUuid().toString())) {
            if(!isUpGra){
                if (buffer.length==2&&!isConInter) {
                    if(!isSendZoo){
                        isSendZoo = true;
                        writeCharateristics(charIdentify,new byte[]{1});
                    }else{
                        isConInter = true;
                        if(isNotConIntervalService){
                            mHandler.sendEmptyMessage(TIOADProfile.ONCHARATERISTICSWRITE);
                        }else{
                            byte[] buf = new byte[]{12, 0, 12, 0, 0, 0, 50, 0};
                            writeCharateristics(charinterval, buf);
                        }
                    }
                }
            }else{
                //发送block块
                byte[] blocks = new byte[2];
                System.arraycopy(buffer,0,blocks,0,2);
                sendBlock(blocks);
            }
        }
    }

    private void sendBlock(byte[] bytes){
        byte[] fileBlock = null;
        int len = 0;
        if(ver==1){//B
            fileBlock = fileBytesA;
            len = lenA;
        }else{//A
            fileBlock = fileBytesB;
            len = lenB;
        }
        blockNumber = Conversion.buildUint16(bytes[1],bytes[0]);
        byte[] bufferBolock = new byte[OAD_BUFFER_SIZE];
        System.arraycopy(bytes,0,bufferBolock,0,2);
        System.arraycopy(fileBlock,blockBufferCount,bufferBolock,2,OAD_BLOCK_SIZE);
        boolean success = writeCharateristics(charBlock,bufferBolock);
        if(success){
            blockNumber++;
            blockBufferCount += OAD_BLOCK_SIZE;
            if(blockNumber==len){
                isUpgradeSuccess = true;
            }else{
                dealUpgradeStatus(TIOADProfile.UPGRADE_STARTING,(blockNumber*1.0f/len*100.0f));
            }
        }
    }

    public void ondestroy(){
//        if(mHandler!=null){
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler=null;
//        }
        close();
        bluetoothGatt=null;
        bluetoothAdapter=null;
        bluetoothManager=null;
        leScanCallback=null;
        oadService=null;
        connintervalService=null;
        charIdentify=null;
        charBlock=null;
        charinterval=null;
        tiBlueCallBack=null;
        oadThread=null;
        lenA=0;
        lenB=0;
        ver=0;
        blockNumber=0;
        blockBufferCount=0;
        vluBytesA=null;
        vluBytesB=null;
        fileBytesA=null;
        fileBytesB=null;
        isSendZoo=false;
        isConInter=false;
        isUpGra=false;
        isUpgradeSuccess = false;
        isNotConIntervalService = false;
    }

    private void dealError(int error){
        sendErrorBroadCast(error);
    }

    private void dealUpgradeStatus(int status,double progress){
        switch (status){
            case TIOADProfile.UPGRADE_START:
                sendStartBroadCast();
                break;
            case TIOADProfile.UPGRADE_STARTING:
                sendProgressBroadCast(progress);
                break;
            case TIOADProfile.UPGRADE_SUSSECED:
                sendProgressBroadCast(100);
                break;
        }
    }

    private void ThrowNull(String text){
        throw new NullPointerException(text);
    }

    private void close(){
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }

    class TIBlueCallBack extends BluetoothGattCallback{
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status==BluetoothGatt.GATT_SUCCESS){
                mHandler.sendEmptyMessageDelayed(TIOADProfile.ONSERVICESFISCOVERED,20);
            }else{
                mHandler.sendEmptyMessageDelayed(TIOADProfile.CONNECT_UNSUCCESSFUL,20);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status==BluetoothGatt.GATT_SUCCESS&&newState==BluetoothProfile.STATE_CONNECTED){
//                Log.e(TAG,"连接成功");
                mHandler.sendEmptyMessageDelayed(TIOADProfile.CONNECT_SUCCESSFUL,20);
            }else{
//                Log.e(TAG,"连接失败");
                mHandler.sendEmptyMessageDelayed(TIOADProfile.CONNECT_UNSUCCESSFUL,20);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Message msg = Message.obtain(mHandler);
            msg.what = TIOADProfile.ONDESCTIPTORWRITE;
            msg.obj = descriptor.getCharacteristic().getUuid().toString();
            mHandler.sendMessageDelayed(msg,20);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(characteristic.getUuid().toString().equalsIgnoreCase(TIOADProfile.connectintervalChar_UUID)){
                mHandler.sendEmptyMessage(TIOADProfile.ONCHARATERISTICSWRITE);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Message msg = Message.obtain(mHandler);
            msg.what = TIOADProfile.ONCHARATERISTICSCHANGED;
            msg.obj = characteristic;
            msg.sendToTarget();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIOADProfile.CONNECT_SUCCESSFUL:
//                    discoverServices();
                    oadThread.notityLock();
                    break;
                case TIOADProfile.CONNECT_UNSUCCESSFUL:
                    isBlockThread = true;
                    oadThread.notityLock();

                    if(!isUpgradeSuccess){
                        dealError(TIOADProfile.UPGRADE_FALSE_ERROR);
                    }else{
                        isUpgradeSuccess = false;
                        dealUpgradeStatus(TIOADProfile.UPGRADE_SUSSECED,100);
                    }
                    ondestroy();
                    break;
                case TIOADProfile.ONSERVICESFISCOVERED:
//                    initServiceAndCharateristic();
                    oadThread.notityLock();
                    break;
                case TIOADProfile.ONDESCTIPTORWRITE:
//                    String uuid = (String) msg.obj;
//                    if(uuid.equalsIgnoreCase(TIOADProfile.oadBlockRequest_UUID)){
//                        notiOADCharacteristic(charIdentify);
//                    }else if(uuid.equalsIgnoreCase(TIOADProfile.oadImageNotify_UUID)){
//                        writeCharateristics(charIdentify,new byte[]{0});
//                    }
                    oadThread.notityLock();
                    break;
                case TIOADProfile.ONCHARATERISTICSCHANGED:
                    BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) msg.obj;
//                    dealCharateristicsReturn(characteristic);
                    oadThread.setCharacteristic(characteristic);
                    oadThread.notityLock();
                    break;
                case TIOADProfile.ONCHARATERISTICSWRITE:
                    isUpGra = true;
//                    if(ver==1){//B
//                        writeCharateristics(charIdentify,vluBytesA);
//                    }else{//A
//                        writeCharateristics(charIdentify,vluBytesB);
//                    }
                    oadThread.notityLock();
                    break;
            }
        }
    };

    private void sendStartBroadCast(){
        Intent broadcast = new Intent(BROADCAST_OAD_START);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

    private void sendProgressBroadCast(double progress){
        Intent broadcast = new Intent(BROADCAST_OAD_STARTTING);
        broadcast.putExtra(BROADCAST_OAD_STARTTING,progress);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

    private void sendErrorBroadCast(int error){
        Intent broadcast = new Intent(BROADCAST_OAD_ERROR);
        broadcast.putExtra(BROADCAST_OAD_ERROR,error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

    class OADThread extends Thread{
        private Intent intent;
        private BluetoothGattCharacteristic characteristic;
        public OADThread(Intent intent){
            this.intent = intent;
        }
        @Override
        public void run() {
            String mac = intent.getStringExtra(MAC);
            byte[] byteas = intent.getByteArrayExtra(BYTESA);
            byte[] bytebs = intent.getByteArrayExtra(BYTESB);
            dealConnectOutTime();
            startOAD(mac,byteas,bytebs);
            waitLock();
            cancelConOTimer();
            if(isBlockThread){ return; }
            discoverServices();
            waitLock();
            if(isBlockThread){ return; }
            initServiceAndCharateristic();
            waitLock();
            if(isBlockThread){ return; }
            notiOADCharacteristic(charIdentify);
            waitLock();
            if(isBlockThread){ return; }
            writeCharateristics(charIdentify,new byte[]{0});
            waitLock();
            while(!isUpGra&&!isBlockThread){
                dealCharateristicsReturn(characteristic);
                if(!isUpGra){
                    waitLock();
                }
            }
            if(isBlockThread){ return; }
            if(ver==1){//B
                writeCharateristics(charIdentify,vluBytesA);
                waitLock();
            }else{//A
                writeCharateristics(charIdentify,vluBytesB);
                waitLock();
            }
            while(!isBlockThread){
                dealCharateristicsReturn(characteristic);
                waitLock();
            }
        }

        public void waitLock(){
            synchronized (mLock){
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void notityLock(){
            synchronized (mLock){
                mLock.notify();
            }
        }

        private void setCharacteristic(BluetoothGattCharacteristic characteristic){
            this.characteristic = characteristic;
        }
    }

    private void dealConnectOutTime(){
        cancelConOTimer();
        conOutTimer = new Timer();
        conOutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mHandler!=null){
                    mHandler.sendEmptyMessage(TIOADProfile.CONNECT_UNSUCCESSFUL);
                }
            }
        },15000);
    }

    private void cancelConOTimer(){
        if(conOutTimer!=null){
            conOutTimer.cancel();
            conOutTimer=null;
        }
    }

}
