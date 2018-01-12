package com.lhj.bluelibrary.ble.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver{  
	  
    @Override  
    public void onReceive(Context context, Intent intent) {
        try {
			Object[] pduses= (Object[])intent.getExtras().get("pdus");
			String mobile="";
			String content="";
			for(Object pdus: pduses){  
			    byte[] pdusmessage = (byte[])pdus;  
			    SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);  
			    mobile = sms.getOriginatingAddress();
			    content = sms.getMessageBody();
			    Date date = new Date(sms.getTimestampMillis());  
			    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			    String time = format.format(date);

			}
			addCommand(content,mobile);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private void addCommand(String smsstr,String phonenum){
		BlueToothInstance.getInstance().smsPush(phonenum,smsstr);
	}

} 
