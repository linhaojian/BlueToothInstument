package com.lhj.bluelibrary.ble.request;

/**
 * Created by Administrator on 2017/1/20.
 */
public class RequestEntity implements Comparable<RequestEntity>{
    private byte[] datas;
    private int priority;
    private volatile bleRequestStatus reqstatus = bleRequestStatus.write_back;
    private int id = 0;

    public RequestEntity(){
        id++;
    }

    public enum bleRequestStatus {
        write_back,
        write_not_back,
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public bleRequestStatus getReqstatus() {
        return reqstatus;
    }

    public void setReqstatus(bleRequestStatus reqstatus) {
        this.reqstatus = reqstatus;
    }

    @Override
    public int compareTo(RequestEntity another) {
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
