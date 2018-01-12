package com.lhj.bluelibrary.ble.push;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Timer;

public class MyNotificationService extends NotificationListenerService {
	public static final String GOOGLE_GM = "com.google.android.gm";
	public static final String EMAIL = "com.android.email";
	public static final String HTC_EMAIL = "com.htc.android.mail";
	public static final String WEIBO = "com.sina.weibo";
	public static final String WECHAT = "com.tencent.mm";
	public static final String LINE = "jp.naver.line.android";
	public static final String FACEBOOK = "com.facebook.katana";
	public static final String TWITTER = "com.twitter.android";
	public static final String SKYPE_1 = "com.skype.raider";
	public static final String SKYPE_2 = "com.skype.polaris";
	public static final String QQ = "com.tencent.mobileqq";
	public static final String WHARTAPPS = "com.whatsapp";
	public static final String FACEBOOKMESSAGE = "com.facebook.orca";
	int count=0;
	Timer timer;

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
//		Log.d("onNotificationPosted", sbn.getPackageName());
//		StatusBarNotification[] sbn1 = getActiveNotifications();
//		for(int i=0;i<sbn1.length;i++){
//			Log.d("onNotificationPosted", sbn1[i].getPackageName());
//		}

		if(GOOGLE_GM.equals(sbn.getPackageName())//gmail
				|| EMAIL.equals(sbn.getPackageName())//ϵͳEmail
				|| HTC_EMAIL.equals(sbn.getPackageName())//
				){
			BlueToothInstance.getInstance().emailPush();

		}else if( (WEIBO.equals(sbn.getPackageName()))
				|| (WECHAT.equals(sbn.getPackageName()))
				|| (LINE.equals(sbn.getPackageName()))
				|| (FACEBOOK.equals(sbn.getPackageName()))
				|| (TWITTER.equals(sbn.getPackageName()))
				){
			setFilter(sbn.getPackageName());
		}
	}

	private void setFilter(String type){
		if(WEIBO.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.SINAWEIBO);
		}else if(WECHAT.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.WEIXIN);
		}else if(TWITTER.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.TWITTER);
		}else if(LINE.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.LINE);
		}else if(FACEBOOK.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.FACEBOOK);
		}else if(SKYPE_1.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.SKYPE);
		}else if(SKYPE_2.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.SKYPE);
		}else if(QQ.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.QQ);
		}else if(WHARTAPPS.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.WHARTAPPS);
		}else if(FACEBOOKMESSAGE.equals(type)){
			BlueToothInstance.getInstance().otherApp(PushEnum.FACEBOOKMESSAGE);
		}
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
//		Log.d("onNotificationRemoved", sbn.getPackageName());
//		StatusBarNotification[] sbn2 = getActiveNotifications();

	}

}
