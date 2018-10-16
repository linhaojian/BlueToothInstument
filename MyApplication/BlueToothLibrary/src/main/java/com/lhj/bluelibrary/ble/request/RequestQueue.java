package com.lhj.bluelibrary.ble.request;


import android.util.Log;

import com.lhj.bluelibrary.ble.BluetoothCombineOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 逻辑操作类
 */
public class RequestQueue {
    private ArrayList<RequestEntitys> queue;
    private Timer sendTimer;
    private int timeOut = 5000;
    private ArrayList<byte[]> bytelist;
    private ExecutorService executorService;

    public RequestQueue(){
        queue = new ArrayList<RequestEntitys>();
        executorService = Executors.newSingleThreadExecutor();
    }

    public RequestEntity setRequestEntity(byte[] datas,int priority
            ,RequestEntity.bleRequestStatus reqstatus){
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setDatas(datas);
        requestEntity.setPriority(priority);
        requestEntity.setReqstatus(reqstatus);
        return requestEntity;
    }

    public RequestEntitys setRequestEntitys(ArrayList<RequestEntity> list,int priority){
        RequestEntitys requestEntitys = new RequestEntitys();
        requestEntitys.setPriority(priority);
        requestEntitys.setRequestEntitys(list);
        return requestEntitys;
    }

    /**
     *  send once for time
     * @param requestEntity
     */
    private synchronized void addQueue(RequestEntity requestEntity) {
        RequestEntitys requestEntitys = new RequestEntitys();
        requestEntitys.setPriority(requestEntity.getPriority());
        requestEntitys.setRequestEntitys(dealRequestEs(requestEntity));
        addQueue(requestEntitys);
    }

    /**
     *  send once for time
     * @param requestEntities
     */
    private synchronized  void addQueues(ArrayList<RequestEntity> requestEntities){
        if(requestEntities!=null&&requestEntities.size()>0){
            for(int i=0;i<requestEntities.size();i++){
                RequestEntitys requestEntitys = new RequestEntitys();
                requestEntitys.setPriority(requestEntities.get(i).getPriority());
                requestEntitys.setRequestEntitys(dealRequestEs(requestEntities.get(i)));
                addQueue(requestEntitys);
            }
        }
    }

    /**
     *  send more for time
     * @param requestEntitys
     */
    private synchronized void addQueueSendMore(RequestEntitys requestEntitys){
        addQueue(requestEntitys);
    }

    /**
     *  send more for time
     * @param requestEntityss
     */
    private synchronized void addQueueSendMores(ArrayList<RequestEntitys> requestEntityss){
        if(requestEntityss!=null&&requestEntityss.size()>0){
            for(int i=0;i<requestEntityss.size();i++){
                addQueue(requestEntityss.get(i));
            }
        }
    }

    private synchronized void addQueue(RequestEntitys requestEntitys){
        if(requestEntitys!=null&&isConnect()){
            queue.add(requestEntitys);
            if(queue.size()==1){
                sendCommand(requestEntitys);
            }
        }
    }

    private ArrayList<RequestEntity> dealRequestEs(RequestEntity requestEntity){
        ArrayList<RequestEntity> list = new ArrayList<RequestEntity>();
        list.add(requestEntity);
        return list;
    }

    private synchronized void sendCommand(RequestEntitys requestEntitys){
        if(bytelist!=null){
        }else{
            bytelist = new ArrayList<>();
        }
        ArrayList<RequestEntity> list = requestEntitys.getRequestEntitys();
        Collections.sort(list);
        for(int i=0;i<list.size();){
            Log.i("linhaojian","sendCommand : "+Arrays.toString(list.get(i).getDatas()));
            if(!isConnect()){return;}
            boolean write = BluetoothCombineOperator.getInstance().writeCommand(list.get(i).getDatas(),true);
            if(write){
                if(list.get(i).getReqstatus()==RequestEntity.bleRequestStatus.write_not_back){
                    list.remove(i);
                    queue.set(0,requestEntitys);
                    if(list.size()==0){
                        if(requestEntitys.getSubscriber()!=null){
                            requestEntitys.getSubscriber().onCompleted();
                        }
                        stopTimer();
                        queue.remove(0);
                        Collections.sort(queue);
                        if(queue.size()>0){
                            sendCommand(queue.get(0));
                        }
                        return;
                    }else{
                        continue;
                    }
                }else{
                    i++;
                    continue;
                }
            }else{
                continue;
            }
        }
        startTimer();
    }

    public void ClearQueue(){
        stopTimer();
        if(queue!=null){
            queue.clear();
        }
        if(bytelist!=null){
            bytelist.clear();
        }
    }

    private boolean isConnect(){
        return BluetoothCombineOperator.getInstance().isConnect();
    }

    private void startTimer(){
        stopTimer();
        sendTimer = new Timer();
        sendTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopTimer();
                sendCommand(queue.get(0));
            }
        },timeOut);
    }

    private void stopTimer(){
        if(sendTimer!=null){
            sendTimer.cancel();
            sendTimer = null;
        }
    }

    public Observable<byte[]> requestCommand(final RequestEntity requestEntity){
        RequestEntitys requestEntitys = new RequestEntitys();
        requestEntitys.setPriority(requestEntity.getPriority());
        requestEntitys.setRequestEntitys(dealRequestEs(requestEntity));
        return requestCommands(requestEntitys).map(new Func1<ArrayList<byte[]>, byte[]>() {
            @Override
            public byte[] call(ArrayList<byte[]> bytes) {
                return bytes.get(0);
            }
        });
    }

    public Observable<ArrayList<byte[]>> requestCommands(final RequestEntitys requestEntitys){
        return Observable.create(new Observable.OnSubscribe<ArrayList<byte[]>>() {
            @Override
            public void call(final Subscriber<? super ArrayList<byte[]>> subscriber) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        addQueueSendMore(requestEntitys);
                        requestEntitys.setSubscriber(subscriber);
                        Log.w("linhaojian "+Thread.currentThread().getId(),"requestCommands");
                    }
                };
                executorService.execute(run);
            }
        })
          .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<FilterEntity> removeCommands(final byte[] datas){
        return Observable.create(new Observable.OnSubscribe<FilterEntity>() {
            @Override
            public void call(final Subscriber<? super FilterEntity> subscriber) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        FilterEntity fil = new FilterEntity();
                        fil.setDatas(datas);
                        fil.setRequestEntitys(queue.size()==0?null:queue.get(0));
                        Log.w("linhaojian "+Thread.currentThread().getId(),"removeCommands");
                        subscriber.onNext(fil);
                    }
                };
                executorService.execute(run);
            }
        })
                ;
    }

    public void filter(final byte[] datas, final CommandFilter commandFilter){
        removeCommands(datas)
        .subscribe(new Action1<FilterEntity>() {
            @Override
            public void call(FilterEntity fil) {
                boolean isRe = false;
                if(fil.getRequestEntitys()!=null&&fil.getRequestEntitys().getRequestEntitys()!=null&&fil.getRequestEntitys().getRequestEntitys().size()>0){
                    for(int i=0;i<fil.getRequestEntitys().getRequestEntitys().size();){
//                        if(fil.getRequestEntitys().getRequestEntitys().get(i).getDatas()[0]==datas[0]){
                          if(commandFilter.filter(fil.getRequestEntitys().getRequestEntitys().get(i).getDatas(),datas)){
                            isRe = true;
                            Log.e("linhaojian","datas : "+Arrays.toString(datas));
                            bytelist.add(datas);
                            fil.getRequestEntitys().getRequestEntitys().remove(i);
                            break;
                        }
                        i++;
                    }
                }

                if(isRe){
                    queue.set(0,fil.getRequestEntitys());
                    if(fil.getRequestEntitys().getRequestEntitys().size()==0){
                        stopTimer();
                        if(fil.getRequestEntitys().getSubscriber()!=null){
                            ArrayList<byte[]> resultbytes = new ArrayList<byte[]>();
                            resultbytes.addAll(bytelist);
                            fil.getRequestEntitys().getSubscriber().onNext(resultbytes);
                            fil.getRequestEntitys().getSubscriber().onCompleted();
                            if(bytelist!=null){
                                bytelist.clear();
                            }
                        }
                        if(queue.size()>0){
                            queue.remove(0);
                            Collections.sort(queue);
                            sendCommand(queue.get(0));
                        }
                    }else{
                    }
                }
                if(!isRe&&RequestQueue.this.subscriber!=null){
                    RequestQueue.this.subscriber.onNext(datas);
//                    RequestQueue.this.subscriber.onCompleted();
                }
            }
        });
    }

    private Subscriber subscriber;
    public Observable<byte[]> notifi(){
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(final Subscriber<? super byte[]> subscriber) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        RequestQueue.this.subscriber = subscriber;
                    }
                };
                executorService.execute(run);
            }
        })
          .observeOn(AndroidSchedulers.mainThread());
    }

    public interface CommandFilter{
        boolean filter(byte[] send_datas,byte[] accept_dates);
    }

}
