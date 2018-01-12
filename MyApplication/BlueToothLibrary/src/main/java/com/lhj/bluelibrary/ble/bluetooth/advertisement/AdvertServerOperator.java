package com.lhj.bluelibrary.ble.bluetooth.advertisement;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.AdvertServerOperatorCallBack;
import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.AdvertisementCallback;
import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.BlueServerOperatorCallBack;
import com.lhj.bluelibrary.ble.bluetooth.advertisement.callback.OperatorCallBack;


/**
 * Created by Administrator on 2017/5/31.
 */
public class AdvertServerOperator {
    private static AdvertServerOperator advertServerOperator = new AdvertServerOperator();
    public static AdvertServerOperator getInstance(){
        return advertServerOperator;
    }
    private AdvertisementOperator advertisementOperator;
    private BlueServerOperator blueServerOperator;
    private OperatorCallBack verifyPwCallBack
            ,openlockCallBack,lockstatusCallBack,updatePwCallBack;
    private AdvertServerOperatorCallBack advertServerOperatorCallBack;
    private boolean isConnectted;
    private BlueBroadcastReceiver blueBroadcastReceiver;
    private Context mContext;

    public void init(Context mContext){
        this.mContext = mContext;
        advertisementOperator = new AdvertisementOperator(mContext);
        blueServerOperator = new BlueServerOperator(mContext);
        initCallback();
        registerBlueReceiver();
    }

    public void ondestroy(){
        unregisterLeReceiver();
        if(blueServerOperator!=null){
            blueServerOperator.close();
            blueServerOperator=null;
        }
        if(advertisementOperator!=null){
            advertisementOperator.stopAdvert();
            advertisementOperator=null;
        }
    }

    private void initCallback(){
        advertisementOperator.setAdvertisementCallback(new AdvertisementCallback() {
            @Override
            public void onStartSuccess() {
                Log.e("fuck","onStartSuccess");
            }

            @Override
            public void onStartFailure(int error) {
                Log.e("fuck","onStartFailure : "+error);
            }

            @Override
            public void onBlueToothStatuError() {
                if(advertServerOperatorCallBack!=null){
                    advertServerOperatorCallBack.OnBlueToothStatuError();
                }
            }
        });
        blueServerOperator.setBlueServerOperatorCallBack(new BlueServerOperatorCallBack() {
            @Override
            public void OnConnect() {
                isConnectted = true;
                if(advertServerOperatorCallBack!=null){
                    advertServerOperatorCallBack.OnConnectted();
                }
            }

            @Override
            public void OnDisConnect() {
                isConnectted = false;
                if(advertServerOperatorCallBack!=null){
                    advertServerOperatorCallBack.OnDisConnectted();
                }
            }

            @Override
            public void OnPairRequest() {
                if(advertServerOperatorCallBack!=null){
                    advertServerOperatorCallBack.OnPairRequest();
                }
            }

            @Override
            public void OnPairResult(boolean isVerify) {
                if(verifyPwCallBack!=null){
                    verifyPwCallBack.getResult(isVerify);
                }
            }

            @Override
            public void OnOpenLockSucceed() {
                if(openlockCallBack!=null){
                    openlockCallBack.getResult(null);
                }
            }

            @Override
            public void OnLockStatusResult(int status) {
                if(lockstatusCallBack!=null){
                    lockstatusCallBack.getResult(status);
                }
            }

            @Override
            public void OnUpdatePwResult(boolean isSucced) {
                if(updatePwCallBack!=null){
                    updatePwCallBack.getResult(isSucced);
                }
            }
        });
    }

    public void startAdvert(){
        advertisementOperator.startAdvert();
    }

    public void stopAdvert(){
        advertisementOperator.stopAdvert();
    }

    public void disconnect(){
        blueServerOperator.disconnect();
    }

    public void OnLockStatus(OperatorCallBack operatorCallBack){
        this.lockstatusCallBack = operatorCallBack;
    }

    public void setAdvertServerOperatorCallBack(AdvertServerOperatorCallBack advertServerOperatorCallBack){
        this.advertServerOperatorCallBack = advertServerOperatorCallBack;
    }

    public boolean getConnectted(){
        return isConnectted;
    }

    public boolean getBlueEnable(){
        return advertisementOperator.getBlueEnable();
    }

    public class BlueBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                    int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.STATE_OFF);
                    switch(btState){
                        case BluetoothAdapter.STATE_OFF://10
                            if(blueServerOperator!=null){
                                blueServerOperator.blueUnable();
                            }
                            if(advertisementOperator!=null){
                                advertisementOperator.stopAdvert();
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON://11
                            break;
                        case BluetoothAdapter.STATE_ON://12
                            advertisementOperator = new AdvertisementOperator(mContext);
                            blueServerOperator = new BlueServerOperator(mContext);
                            initCallback();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://13
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerBlueReceiver(){
        blueBroadcastReceiver=new BlueBroadcastReceiver();
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



}
