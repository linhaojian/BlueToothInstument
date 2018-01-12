package com.lhj.bluelibrary.ble.airupdate.ti;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/1/5.
 */
public class TIOADExecutor {
    private Context context;
    private int rawIdA;
    private int rawIdB;
    private String filePathA;
    private String filePathB;
    private Uri fileuriA;
    private Uri fileuriB;
    private String fileNameA;
    private String fileNameB;
    private byte[] bytesA;
    private byte[] bytesB;
    private String mac;
    private ServiceConnection serviceConnection;
    private TIOADExecutorListeners tioadExecutorListeners;
    private TIOADBroadcast tioadBroadcast;

    public TIOADExecutor(Context context){
        this.context = context;
        registerBroadcastReceiver(context);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    public TIOADExecutor setZip(int rawIdA,int rawIdB){
        this.rawIdA = rawIdA;
        this.rawIdB = rawIdB;
        this.bytesA = getBytesForRaw(rawIdA);
        this.bytesB = getBytesForRaw(rawIdB);
        return this;
    }

    public TIOADExecutor setZip(@NonNull String filePathA, @NonNull String filePathB){
        this.filePathA = filePathA;
        this.filePathB = filePathB;
        this.bytesA = getBytesForPath(filePathA);
        this.bytesB = getBytesForPath(filePathB);
        return this;
    }

    public TIOADExecutor setZip(@NonNull Uri fileuriA, @NonNull Uri fileuriB){
        this.fileuriA = fileuriA;
        this.fileuriB = fileuriB;
        this.bytesA = getBytesForUri(fileuriA);
        this.bytesB = getBytesForUri(fileuriB);
        return this;
    }

    public TIOADExecutor setZipForAssets(@NonNull String fileNameA, @NonNull String fileNameB){
        this.fileNameA = fileNameA;
        this.fileNameB = fileNameB;
        this.bytesA = getBytesForAssets(fileNameA);
        this.bytesB = getBytesForAssets(fileNameB);
        return this;
    }

    public TIOADExecutor setZipForBytes(@NonNull byte[] bytesA, @NonNull byte[] bytesB){
        this.bytesA = bytesA;
        this.bytesB = bytesB;
        return this;
    }

    public TIOADExecutor DeviceMac(@NonNull String mac){
        this.mac = mac;
        return this;
    }

    public TIOADExecutor start(){
        if(!TextUtils.isEmpty(mac)&&bytesA!=null&&bytesB!=null){
            Intent intent = new Intent(context,OADPractitioner.class);
            intent.putExtra(OADPractitioner.MAC,mac);
            intent.putExtra(OADPractitioner.BYTESA,bytesA);
            intent.putExtra(OADPractitioner.BYTESB,bytesB);
            context.bindService(intent,serviceConnection ,Context.BIND_AUTO_CREATE);
        }else{
            if(TextUtils.isEmpty(mac)){
                throw new NullPointerException("mac is null or \"\" ");
            }else{
                throw new NullPointerException("bytesA or bytesB is null");
            }
        }
        return this;
    }

    public TIOADExecutor setResultListenters(TIOADExecutorListeners tioadExecutorListeners){
        this.tioadExecutorListeners = tioadExecutorListeners;
        return this;
    }

    private byte[] getBytesForRaw(int raw){
        byte[] buffer = null;
        try{
            InputStream in = context.getResources().openRawResource(raw);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            in.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return buffer;
    }

    private byte[] getBytesForAssets(String fileName){
        byte[] buffer = null;
        try{
            InputStream in = context.getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            in.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return buffer;
    }

    private byte[] getBytesForPath(String filePath){
        byte[] buffer = null;
        try{
            File file = new File(filePath);
            InputStream in = new FileInputStream(file);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            in.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return buffer;
    }

    private byte[] getBytesForUri(Uri uri){
        byte[] buffer = null;
        try{
            InputStream in = new FileInputStream(uri.getPath());
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            in.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return buffer;
    }

    private void stop(){
        unregisterBroadcastReceiver(context);
        context.unbindService(serviceConnection);
    }

    private void registerBroadcastReceiver(Context context){
        tioadBroadcast = new TIOADBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OADPractitioner.BROADCAST_OAD_START);
        intentFilter.addAction(OADPractitioner.BROADCAST_OAD_STARTTING);
        intentFilter.addAction(OADPractitioner.BROADCAST_OAD_ERROR);
        LocalBroadcastManager.getInstance(context).registerReceiver(tioadBroadcast,intentFilter);
    }

    private void unregisterBroadcastReceiver(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(tioadBroadcast);
    }

    public class TIOADBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(tioadExecutorListeners==null){
                return;
            }
            String action = intent.getAction();
            if(action.equals(OADPractitioner.BROADCAST_OAD_START)){
                tioadExecutorListeners.OnStart();
            }else if(action.equals(OADPractitioner.BROADCAST_OAD_STARTTING)){
                double pro = intent.getDoubleExtra(OADPractitioner.BROADCAST_OAD_STARTTING,0);
                tioadExecutorListeners.OnStarting(pro);
                if(pro==100){
                    stop();
                    tioadExecutorListeners.OnSuccess();
                }
            }else if(action.equals(OADPractitioner.BROADCAST_OAD_ERROR)){
                stop();
                tioadExecutorListeners.OnError(intent.getIntExtra(OADPractitioner.BROADCAST_OAD_ERROR,TIOADProfile.UPGRADE_FALSE_ERROR));
            }
        }
    }

    public void OnDestroy(){
        stop();
    }




}
