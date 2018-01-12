package com.lhj.bluelibrary.ble.request;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/1/20.
 */
public class RequestEntitys implements Comparable<RequestEntitys>{
    private ArrayList<RequestEntity> requestEntity;
    private int priority;
    private Subscriber<? super ArrayList<byte[]>> subscriber;

    public ArrayList<RequestEntity> getRequestEntitys() {
        return requestEntity;
    }

    public void setRequestEntitys(ArrayList<RequestEntity> requestEntity) {
        this.requestEntity = requestEntity;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Subscriber<? super ArrayList<byte[]>> getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber<? super ArrayList<byte[]>> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public int compareTo(RequestEntitys another) {
        if(another!=null){
            if(this.priority>another.priority){
                return -1;
            }else if(this.priority==another.priority){
                return 0;
            }
        }
        return 1;
    }

}
