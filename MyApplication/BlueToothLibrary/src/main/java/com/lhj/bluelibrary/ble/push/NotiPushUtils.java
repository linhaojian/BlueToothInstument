package com.lhj.bluelibrary.ble.push;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

public class NotiPushUtils {
	
	private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"; 
	private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

	/**
	 * 跳转到设置通知权限的界面
	 * @param context
     */
	public static void openNotiPushSettingUI(Context context){
		Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
		context.startActivity(intent);
	}
	
	/**本应用是否已打开通知栏通知权限的判断**/
	public static boolean isEnabled(Context context) {
//		Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages (context);

		 String pkgName = context.getPackageName();  
		    final String flat = Settings.Secure.getString(context.getContentResolver(),  
		            ENABLED_NOTIFICATION_LISTENERS);  
		    if (!TextUtils.isEmpty(flat)) {  
		        final String[] names = flat.split(":");  
		        for (int i = 0; i < names.length; i++) {  
		            final ComponentName cn = ComponentName.unflattenFromString(names[i]);  
		            if (cn != null) {  
		                if (TextUtils.equals(pkgName, cn.getPackageName())) {  
		                    return true;  
		                }  
		            }  
		        }  
		    }  
		    return false;  
	}

	/**
	 *  disable与enable NotificationListenerService
	 * @param context
	 * @param componentclass
	 */
	public static void toggleNotificationListenerService(Context context, Class componentclass) {
		if(isEnabled(context)){
			PackageManager pm = context.getPackageManager();
			pm.setComponentEnabledSetting(new ComponentName(context,componentclass),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			pm.setComponentEnabledSetting(new ComponentName(context, componentclass),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		}
	}


}
