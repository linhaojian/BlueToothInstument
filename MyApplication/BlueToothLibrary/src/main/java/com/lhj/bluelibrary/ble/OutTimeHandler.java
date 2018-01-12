package com.lhj.bluelibrary.ble;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/10/31.
 */
public class OutTimeHandler {
    private Timer connectTimer;
    private Timer commandTimer;

    public void setConnectTimer(int time,final OutTimerListeners outTimerListeners){
        cancelConnectTimer();
        connectTimer = new Timer();
        connectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(outTimerListeners!=null){
                    outTimerListeners.outtime();
                }
            }
        },time);
    }

    public void cancelConnectTimer(){
        if(connectTimer!=null) {
            connectTimer.cancel();
            connectTimer = null;
        }
    }

    public void setCommandTimer(int time,final OutTimerListeners outTimerListeners){
        cancelCommandTimer();
        commandTimer = new Timer();
        commandTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(outTimerListeners!=null){
                    outTimerListeners.outtime();
                }
            }
        },time);
    }

    public void cancelCommandTimer(){
        if(commandTimer!=null) {
            commandTimer.cancel();
            commandTimer = null;
        }
    }

    public void onDestroy(){
        cancelCommandTimer();
        cancelConnectTimer();
    }

    public interface OutTimerListeners{
        void outtime();
    }


}
