package com.lhj.bluelibrary.ble.until;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionTools {

	public static boolean CheckPermission(Activity activity,int requesCode,String permission){
		boolean has = true;
		 if (ContextCompat.checkSelfPermission(activity, permission)
	              != PackageManager.PERMISSION_GRANTED) {
	          ActivityCompat.requestPermissions(activity, new String[]{permission},
	                  requesCode);
			 has = false;
	      }
		return has;
	}
	
	public static boolean CheckLocationPermissionForM(Context activity){
		if(Build.VERSION.SDK_INT >= 23){
			 if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
		              != PackageManager.PERMISSION_GRANTED
		              || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
		              != PackageManager.PERMISSION_GRANTED) {				 
				 return false;
			 }
		}
		return true;
	}



}
