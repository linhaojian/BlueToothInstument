package com.lhj.bluelibrary.ble.request;

/**
 * Created by Administrator on 2017/2/10.
 */
public class FilterEntity {
    private RequestEntitys requestEntitys;
    private byte[] datas;

    public RequestEntitys getRequestEntitys() {
        return requestEntitys;
    }

    public void setRequestEntitys(RequestEntitys requestEntitys) {
        this.requestEntitys = requestEntitys;
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }
}
