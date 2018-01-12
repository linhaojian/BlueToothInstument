package com.lhj.bluelibrary.ble.until;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tools {
	private static final byte GAP_ADTYPE_FLAGS=0x01;   //Discovery Mode  
	private static final byte GAP_ADTYPE_16BIT_MORE=0x02; //Service: More 16-bit UUIDs available  
	   
	private static final byte GAP_ADTYPE_16BIT_COMPLETE=0x03; //Service: Complete list of 16-bit UUIDs  
	private static final byte GAP_ADTYPE_32BIT_MORE=0x04;     //Service: More 32-bit UUIDs available  
	private static final byte GAP_ADTYPE_32BIT_COMPLETE=0x05; //Service: Complete list of 32-bit UUIDs  
	   
	private static final byte GAP_ADTYPE_128BIT_MORE=0x06;  //Service: More 128-bit UUIDs available  
	   
	private static final byte GAP_ADTYPE_128BIT_COMPLETE=0x07;  //Service: Complete list of 128-bit UUIDs  
	private static final byte GAP_ADTYPE_LOCAL_NAME_SHORT=0x08; //Shortened local name  
	    
	private static final byte GAP_ADTYPE_LOCAL_NAME_COMPLETE=0x09; //Complete local name  
	private static final byte GAP_ADTYPE_POWER_LEVEL=0x0A;  //TX Power Level: 0xXX: -127 to +127 dBm  
	   
	private static final byte GAP_ADTYPE_OOB_CLASS_OF_DEVICE=0x0D; //Simple Pairing OOB Tag: Class of device (3 octets)  
	private static final byte GAP_ADTYPE_OOB_SIMPLE_PAIRING_HASHC=0x0E;//Simple Pairing OOB Tag: Simple Pairing Hash C (16 octets)  
	private static final byte GAP_ADTYPE_OOB_SIMPLE_PAIRING_RANDR=0x0F; //Simple Pairing OOB Tag: Simple Pairing Randomizer R (16 octets)  
	private static final byte GAP_ADTYPE_SM_TK=0x10;   //Security Manager TK Value  
	private static final byte GAP_ADTYPE_SM_OOB_FLAG=0x11;  //Secutiry Manager OOB Flags  
	   
	private static final byte GAP_ADTYPE_SLAVE_CONN_INTERVAL_RANGE=0x12;  //Min and Max values of the connection interval  
	private static final byte GAP_ADTYPE_SIGNED_DATA=0x13;  //Signed Data field  
	private static final byte GAP_ADTYPE_SERVICES_LIST_16BIT=0x14;  //Service Solicitation: list of 16-bit Service UUIDs  
	private static final byte GAP_ADTYPE_SERVICES_LIST_128BIT=0x15; //Service Solicitation: list of 128-bit Service UUIDs  
	private static final byte GAP_ADTYPE_SERVICE_DATA=0x16;  //Service Data  
	private static final byte GAP_ADTYPE_APPEARANCE=0x19;  //Appearance  
	private static final int GAP_ADTYPE_MANUFACTURER_SPECIFIC=255;  //Manufacturer Specific Data
	

	/**
	 *   拆分广播包的数据
	 * @param record 以16进制组合的数据集
	 * @return
	 */
	public static ArrayList<ArrayList<String>> splitScanRecode(String record){
		ArrayList<ArrayList<String>> list_liststr=new ArrayList<ArrayList<String>>();
		ArrayList<String> list=new ArrayList<String>();
		String[] records=record.split(" ");
		int count=Integer.parseInt(records[0],16);
		boolean sumCount=false;
		for(int i=0;i<records.length;i++){
			if(sumCount){
				sumCount=false;
				count=count+Integer.parseInt(records[i],16)+1;
			}
			if(i==count){
//				if(Integer.parseInt(records[i],16)==0){break;}
				list.add(records[i]);
				sumCount=true;
				list_liststr.add(list);
				list=new ArrayList<>();
				
			}else{
				list.add(records[i]);				
			}
		}
		return list_liststr;
	}
	
	/**
	 *   拆分包，获取指定的128bit,16bit的UUID ; local name ; manufacturer ; 发射功率 ; 自定义服务描述 ; 外设 ; 广播模式 ; 连接间隔 ; 签名数据 ; 服务列表16bit ; 服务列表128bit ; 
	 *                  完整16bitUUID ; 32bit ; 完整32bitUUID ; 完整128bitUUID ; 短的签名 ; 配对模式1 ; 配对模式2 ; 配对模式3 ; TK值 ; oob模式 ;
	 *                                     
	 * @param list  数据
	 * @return 返回数组 1:128bit ; 2:16bit ; 3:localname ; 4:manufacturer ; 5:power_level ; 6:service_data ; 7:apparance ; 8:flags ; 9:con_range ; 10:singe_data
	 *                                   11:service_list_16bit ; 12:service_list_128bit ; 13:16bit_complete ; 14:32bit_more ; 15:32bit_complete ; 16:128bit_complete ; 
	 *                                   17:localNameShort ; 18:配对模式(Class of device (3 octets)) ; 19:配对模式(Simple Pairing Hash C (16 octets)) ; 20:配对模式(Simple Pairing Randomizer R (16 octets))
	 *                                   21:TK value ; 22:oob flag ;
	 *                   默认为nulln
	 */
	public static String[] splitType(ArrayList<ArrayList<String>> list){
		String[] s=new String[22];
		for(int i=0;i<list.size();i++){
//			Log.e("",(Integer.parseInt(list.get(i).get(1),16))+"  :  "+GAP_ADTYPE_MANUFACTURER_SPECIFIC);
			if(list.get(i).size()>1){
				if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_128BIT_MORE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[0]=sb.toString();
					Log.i("GAP_ADTYPE_128BIT_MORE", s[0]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_16BIT_MORE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[1]=sb.toString();
					Log.i("GAP_ADTYPE_16BIT_MORE", s[1]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_LOCAL_NAME_COMPLETE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[2]=toStringHex(sb.toString());
					Log.i("GAP_ADTYPE_LOCAL_NAME_COMPLETE", s[2]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_MANUFACTURER_SPECIFIC){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[3]=sb.toString();
					Log.i("GAP_ADTYPE_MANUFACTURER_SPECIFIC", s[3]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_POWER_LEVEL){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[4]=sb.toString();
					Log.i("GAP_ADTYPE_POWER_LEVEL", s[4]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SERVICE_DATA){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[5]=sb.toString();
					Log.i("GAP_ADTYPE_SERVICE_DATA", s[5]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_APPEARANCE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[6]=sb.toString();
					Log.i("GAP_ADTYPE_APPEARANCE", s[6]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_FLAGS){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[7]=sb.toString();
					Log.i("GAP_ADTYPE_FLAGS", s[7]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SLAVE_CONN_INTERVAL_RANGE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[8]=sb.toString();
					Log.i("GAP_ADTYPE_SLAVE_CONN_INTERVAL_RANGE", s[8]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SIGNED_DATA){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[9]=sb.toString();
					Log.i("GAP_ADTYPE_SIGNED_DATA", s[9]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SERVICES_LIST_16BIT){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[10]=sb.toString();
					Log.i("GAP_ADTYPE_SERVICES_LIST_16BIT", s[10]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SERVICES_LIST_128BIT){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[11]=sb.toString();
					Log.i("GAP_ADTYPE_SERVICES_LIST_128BIT", s[11]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_16BIT_COMPLETE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[12]=sb.toString();
					Log.i("GAP_ADTYPE_16BIT_COMPLETE", s[12]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_32BIT_MORE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[13]=sb.toString();
					Log.i("GAP_ADTYPE_32BIT_MORE", s[13]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_32BIT_COMPLETE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[14]=sb.toString();
					Log.i("GAP_ADTYPE_32BIT_COMPLETE", s[14]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_128BIT_COMPLETE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[15]=sb.toString();
					Log.i("GAP_ADTYPE_128BIT_COMPLETE", s[15]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_LOCAL_NAME_SHORT){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[16]=sb.toString();
					Log.i("GAP_ADTYPE_LOCAL_NAME_SHORT", s[16]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_OOB_CLASS_OF_DEVICE){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[17]=sb.toString();
					Log.i("GAP_ADTYPE_OOB_CLASS_OF_DEVICE", s[17]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_OOB_SIMPLE_PAIRING_HASHC){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[18]=sb.toString();
					Log.i("GAP_ADTYPE_OOB_SIMPLE_PAIRING_HASHC", s[18]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_OOB_SIMPLE_PAIRING_RANDR){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[19]=sb.toString();
					Log.i("GAP_ADTYPE_OOB_SIMPLE_PAIRING_RANDR", s[19]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SM_TK){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[20]=sb.toString();
					Log.i("GAP_ADTYPE_SM_TK", s[20]);
				}else if(Integer.parseInt(list.get(i).get(1),16)==GAP_ADTYPE_SM_OOB_FLAG){
					StringBuilder sb=new StringBuilder();
					for(int j=2;j<list.get(i).size();j++){
						sb.append(list.get(i).get(j));
					}
					s[21]=sb.toString();
					Log.i("GAP_ADTYPE_SM_OOB_FLAG", s[21]);
				}
			}
		}
		return s;
	}
	
	/** 
	* Convert byte[] to hex string. 把字节数组转化为字符串 
	* 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。 
	* @param src byte[] data 
	* @return hex string 
	*/     
   public static String bytesToHexString(byte[] src){  
	   StringBuilder stringBuilder = new StringBuilder("");  
       		if (src == null || src.length <= 0) {  
       			return null;  
       		}  
       		for (int i = 0; i < src.length; i++) {  
       			int v = src[i] & 0xFF;  
       			String hv = Integer.toHexString(v);  
       			if (hv.length() < 2) {  
               stringBuilder.append(0);  
       			}  
           stringBuilder.append(hv+" ");  
       }  
     
      return stringBuilder.toString();  
   } 
   
	/**
	 *   解析LocalName
	 * @param s 16进制组合的数据
	 * @return LocalName
	 */
	public static String toStringHex(String s){
		byte[] baKeyword = new byte[s.length()/2];
		for(int i = 0; i < baKeyword.length; i++){
			try{
				baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			try {
				s = new String(baKeyword, "utf-8");//UTF-16le:Not
			}catch (Exception e1){
				e1.printStackTrace();
			} 
		return s;
	}

	/**
	 *   将指定时间加上一分钟的时间
	 * @param data
	 * @return
	 */
	public static String timeAdd1Min(String dateTime){//yyy MM dd HH mm
		String newdateTime="";
		try {
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			long s=sf.parse(dateTime).getTime();
			s=s+(60*1000);
			newdateTime=sf.format(new Date(s));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newdateTime;
	}

	/**
	 *   获取指定时间段里面的所有时间
	 * @param dataS
	 * @param dataE
	 * @return
	 */
	public static ArrayList<String> dataTimes(String dataS,String dataE) {
		ArrayList<String> list=new ArrayList<>();
		if(dataS.equals("")||dataE.equals("")){
			return list;
		}
		try {
			list.add(dataS);
			String nextT=dataS;
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			long starttimelong=sf.parse(nextT).getTime();
			long eimelong=sf.parse(dataE).getTime();
			while(starttimelong<eimelong){
				nextT=timeAdd1Min(nextT);
				starttimelong=sf.parse(nextT).getTime();
				list.add(nextT);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 *   将一个字节的数据转为2进制
	 * @param b
	 * @return
	 */
   public static String getBinaryStrFromByte(byte b){  
        String result ="";  
        byte a = b; ;  
        for (int i = 0; i < 8; i++){  
            byte c=a;  
            a=(byte)(a>>1);//每移一位如同将10进制数除以2并去掉余数。  
            a=(byte)(a<<1);  
            if(a==c){  
                result="0"+result;  
            }else{  
                result="1"+result;  
            }  
            a=(byte)(a>>1);  
        }  
        return result;  
    }



}
