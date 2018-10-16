package com.lhj.bluelibrary.ble.request;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Administrator on 2017/2/10.
 */
public class RxCommand implements CommandDispose{
    private static RxCommand rxCommand;
    private RequestQueue requestQueue;
    public static RxCommand getInstance(){
        if(rxCommand==null){
            synchronized (RxCommand.class){
                if(rxCommand==null){
                    rxCommand = new RxCommand();
                }
            }
        }
        return rxCommand;
    }

    public RxCommand(){
        requestQueue = new RequestQueue();
    }


    @Override
    public Observable<byte[]> requestCommand(RequestEntity requestEntity) {
        return requestQueue.requestCommand(requestEntity);
    }

    @Override
    public Observable<ArrayList<byte[]>> requestCommands(RequestEntitys requestEntitys) {
        return requestQueue.requestCommands(requestEntitys);
    }

    @Override
    public void disconnectClear() {
        requestQueue.ClearQueue();
    }

    @Override
    public void filterCommand(byte[] datas, RequestQueue.CommandFilter commandFilter) {
        requestQueue.filter(datas,commandFilter);
    }

    @Override
    public Observable<byte[]> notifi() {
        return requestQueue.notifi();
    }

    @Override
    public RequestEntity setRequestEntity(byte[] datas,int priority
            ,RequestEntity.bleRequestStatus reqstatus) {
        return requestQueue.setRequestEntity(datas,priority,reqstatus);
    }

    @Override
    public RequestEntitys setRequestEntitys(ArrayList<RequestEntity> list, int priority) {
        return requestQueue.setRequestEntitys(list,priority);
    }
}
