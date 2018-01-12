package com.lhj.bluelibrary.ble.request;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Administrator on 2017/2/10.
 */
public interface CommandDispose {

    abstract Observable<byte[]> requestCommand(RequestEntity requestEntity);

    abstract Observable<ArrayList<byte[]>> requestCommands(RequestEntitys requestEntitys);

    abstract void disconnectClear();

    abstract void filterCommand(byte[] datas);

    abstract Observable<byte[]> notifi();

    abstract RequestEntity setRequestEntity(byte[] datas, int priority
            , RequestEntity.bleRequestStatus reqstatus);

    abstract RequestEntitys setRequestEntitys(ArrayList<RequestEntity> list, int priority);

}
