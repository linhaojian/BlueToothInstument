package com.lhj.bluelibrary.ble.airupdate.aes;

public class TypeConversionUtils {
	 /**
     *  byte ת String
     * @param buff
     * @return
     */
    public static String byte2String(byte[] buff) {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < buff.length; i++) {
            int tmp = buff[i] & 0XFF;
            String str = Integer.toHexString(tmp);
            if (str.length() == 1) {
                sbuf.append("0" + str);
            } else {
                sbuf.append(str);
            }

        }
        return sbuf.toString();
    }

    /**
     *  String ת byte
     * @param str
     * @return
     */
    public static byte[] String2byte(String str) {
        byte[] result = new byte[str.length() / 2];
        int index = 0;
        for (int i = 0; i < str.length(); i += 2) {
            result[index++] = (byte) Integer.parseInt(str.substring(i, i + 2),
                    16);
        }
        return result;
    }

    /**
     *  byte ת String(���ı��ֽ�����ת��--ISO-8859-1���ֽ�)
     * @param buff
     * @return
     */
    public static String byte2StringNotHex(byte[] buff){
        String str = null;
        try {
            str = new String(buff,"ISO-8859-1");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return str;
    }

    /**
     *  String ת byte(���ı��ֽ�����ת��--ISO-8859-1���ֽ�)
     * @param str
     * @return
     */
    public static byte[] String2byteNotHex(String str){
        byte[] bytes = null;
        try {
            bytes = str.getBytes("ISO-8859-1");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bytes;
    }


}
