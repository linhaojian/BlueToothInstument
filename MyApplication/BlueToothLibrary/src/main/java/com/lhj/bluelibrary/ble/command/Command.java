package com.lhj.bluelibrary.ble.command;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/30.
 */
public class Command {

    private static Command instance=new Command();
    public static Command getInstance(){
        return instance;
    }

    /**
     * 绑定协议（0x01）
     */
    public byte[] sendBindCode(int bindCode){
        byte[] bindbytes=get3Byte(bindCode);
        byte[] by = new byte[]{0x01,bindbytes[0],bindbytes[1],bindbytes[2]};
        return by;
    }

    /**
     *  解绑协议（0x02）
     * @return
     */
    public byte[] sendUnBind(){
        return new byte[]{0x02};
    }

    /**
     *  重连协议（0x03）
     * @return
     */
    public byte[] sendReConnect(byte[] bindbytes){
        byte[] reconnect = new byte[]{0x03,bindbytes[0],bindbytes[1],bindbytes[2],0x02};
        return reconnect;
    }

    /**
     *  用户信息(0x12)
     * @param objes 配置；出生年；出生月；出生日；身高；体重
     * @return
     */
    public byte[] sendUserMsg(byte ... objes){
        byte[] userbytes = new byte[]{0x12,objes[0],objes[1],objes[2],objes[3],objes[4],objes[5]};
        return userbytes;
    }

    /**
     *  目标值(0x15)
     * @param goals
     * @return
     */
    public byte[] sendGoals(byte ...goals){
        byte[] goalbytes = new byte[]{0x15,goals[0],goals[1],goals[2],goals[3],goals[4]};
        return goalbytes;
    }

    /**
     *  时钟格式(0x18)
     * @param timeFor 0x01为24小时制；0x02为12小时制；
     * @param dateFor 0x01为MM-dd；0x02为dd-MM
     * @return
     */
    public byte[] sendTimeFor(byte timeFor,byte dateFor){
        byte[] fro = new byte[]{0x18,timeFor,dateFor};
        return fro;
    }

    /**
     * 闹钟设置(0x1A)
     * @param alarms 序号；闹钟开关；闹钟时间-时；闹钟时间-分；重复（4组）
     *               序号：0x01 ：1-4的闹钟 ； 0x02 ：5-8的闹钟。
                    闹钟开关：0x01：关  ；0x02：开。
                    闹钟时间：时（0~23）；分（0~59）；默认与删除闹钟时候：时和分都为0xff
                    重复：0-6bit(星期一~星期天循环)
     * @return
     */
    public byte[] sendAlarms(byte ...alarms){
        byte[] alarmbytes = new byte[]{0x1A,alarms[0],alarms[1],alarms[2],alarms[3],alarms[4],
                alarms[5],alarms[6],alarms[7],alarms[8],alarms[9],alarms[10],alarms[11],
                alarms[12],alarms[13],alarms[14],alarms[15],alarms[16]};
        return alarmbytes;
    }

    /**
     *  自动感光与抬手亮设置(0x1B)
     *  自动感光/抬手亮：0x01：关  ；0x02：开。
     * @param autoL
     * @param thL
     * @return
     */
    public byte[] sendLight(byte autoL,byte thL){
        byte[] lightbytes = new byte[]{0x1B,autoL,thL};
        return lightbytes;
    }

    /**
     *  心率高低检测设置(0x1C)
     *  心率低：30~60.
        心率高：80~240.
     * @param min
     * @param max
     * @return
     */
    public byte[] sendHeartRateMaxMin(byte min,byte max){
        byte[] heart = new byte[]{0x1C,min,max};
        return heart;
    }

    /**
     *  同步系统时间
     * @return
     */
    public byte[] sendTimeSync(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = sf.format(new Date());
        byte year = (byte) (Integer.parseInt(date.split("-")[0])-1900);
        byte month = Byte.parseByte(date.split("-")[1]);
        byte day = Byte.parseByte(date.split("-")[2]);
        byte hour = Byte.parseByte(date.split("-")[3]);
        byte min = Byte.parseByte(date.split("-")[4]);
        byte second = Byte.parseByte(date.split("-")[5]);
        byte tsecond = 0;
        byte[] time = new byte[]{0x20,0x02,year,month,day,hour,min,second,tsecond};
        return time;
    }

    /**
     *  同步系统时间
     * @return
     */
    public byte[] sendTimeSync1(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = sf.format(new Date());
        byte year = (byte) (Integer.parseInt(date.split("-")[0])-1900);
        byte month = Byte.parseByte(date.split("-")[1]);
        byte day = Byte.parseByte(date.split("-")[2]);
        byte hour = Byte.parseByte(date.split("-")[3]);
        byte min = Byte.parseByte(date.split("-")[4]);
        byte second = Byte.parseByte(date.split("-")[5]);
        byte tsecond = 0;
        byte[] time = new byte[]{0x20,year,month,day,hour,min,second,tsecond};
        return time;
    }

    /**
     *  信息推送协议(0x30)
     *  提醒新增：Bit0:来电，0：没新增，1：有新增；
         Bit1:短信，0：没新增，1：有新增；
         Bit2:邮件，0：没新增，1：有新增；
         Bit3:社交(除短信)，0：没新增，1：有新增；
         Bit4:接通，0：没新增，1：有新增；
         Bit5:挂断，0：没新增，1：有新增；
     * @param push
     * @return
     */
    public byte[] sendPushNoti(byte push){
        byte[] pushbytes = new byte[]{0x30,push};
        return pushbytes;
    }

    /**
     *  数据上传协议(0x60~0x6f)
     * @param dates length=6 ；0：0~15，1：年，2：月，3：日，4：时，5：分。
     * @return
     */
    public byte[] sendDataTime(byte ...dates){
        byte[] databytes = new byte[]{(byte) (0x60+dates[0]),dates[1],dates[2],dates[3],dates[4],dates[5]};
        return databytes;
    }

    /**
     *  版本信息读取(0x70)
     * @return
     */
    public byte[] sendVersionMsg(){
        return new byte[]{0x70};
    }

    /**
     *  发送断开蓝牙(0x80)
     * @return
     */
    public byte[] sendDisconnect(){
        return new byte[]{(byte) 0x80};
    }

    /**
     *  发送更改连接速度(0x82)
     *  连接速度：0x01切到慢连接；0x02切到快连接；
     * @return
     */
    public byte[] sendChangeSpeed(byte speed){
        return new byte[]{(byte) 0x82,speed};
    }


    /**
     *  byte 转16进制
     * @param b
     */
    public static String printHexString( byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return (hex.toUpperCase() );
    }

    public static byte getByte(byte ...b){//可变参：...b
        byte bt=0;
        for(int i=0;i<b.length;i++){
            bt+=(byte) (b[i]*((byte)Math.pow(2, i)));
        }
        return bt;
    }

    public static byte[] get2Byte(int x){
        byte[] bt=new byte[2];
        int y= x & 0xFFFFFF;

        bt[0]=(byte) (y & 0xFF);
        bt[1]=(byte) ((y>>8) & 0xFF);
        return bt;
    }

    public static byte[] get3Byte(int x){
        byte[] bt=new byte[3];
        int y= x & 0xFFFFFF;

        bt[0]=(byte) (y & 0xFF);
        bt[1]=(byte) ((y>>8) & 0xFF);
        bt[2]=(byte) ((y>>16) & 0xFF);
        return bt;
    }
}
