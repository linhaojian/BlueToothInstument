package com.lhj.bluelibrary.ble.push;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver{
    
    private static final String TAG = "PhoneStatReceiver";
    

    private static boolean incomingFlag = false;
    private static boolean isConnect=false;
    private static int PHONESTATE=4;

    private static String incoming_number = null;

    @Override
    public void onReceive(Context context, Intent intent) {
            try {
				//拨出去
				if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                        
				        incomingFlag = false;
				        isConnect=false;
				        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);        
				        Log.i(TAG, "call OUT:"+phoneNumber);                        
				}else{
				        //拨进来
				        TelephonyManager tm = 
				            (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                        
				        
				        switch (tm.getCallState()) {
				        case TelephonyManager.CALL_STATE_RINGING:
				        	if(PHONESTATE!=TelephonyManager.CALL_STATE_RINGING){
				        		PHONESTATE=TelephonyManager.CALL_STATE_RINGING;
				        		incomingFlag = true;
				        		isConnect=false;
				        		incoming_number = intent.getStringExtra("incoming_number");
				        		Log.i(TAG, "来电 :"+ incoming_number);
				        		addCommand(PushEnum.CALL_STATE_RINGING,incoming_number);
				        	}
				                break;
				        case TelephonyManager.CALL_STATE_OFFHOOK:        
				        	if(PHONESTATE!=TelephonyManager.CALL_STATE_OFFHOOK){
				        		PHONESTATE=TelephonyManager.CALL_STATE_OFFHOOK;
				                if(incomingFlag){
			                        Log.i(TAG, "接通 :"+ incoming_number);
			                        isConnect=true;
					                addCommand(PushEnum.CALL_STATE_OFFHOOK,incoming_number);
				                }
				        	}
				                break;
				        case TelephonyManager.CALL_STATE_IDLE:     
				        	if(PHONESTATE!=TelephonyManager.CALL_STATE_IDLE){
				        		PHONESTATE=TelephonyManager.CALL_STATE_IDLE;
				        		if(incomingFlag&&!isConnect){
				        			Log.i(TAG, "没接或者挂断");  
				        			addCommand(PushEnum.CALL_STATE_IDLE,incoming_number);
				        		}
				        	}
				                break;
				       } 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    }
    
    private void addCommand(int phoneStatus,String phonenum){
		BlueToothInstance.getInstance().phonePush(phonenum,phoneStatus);
    }


}
