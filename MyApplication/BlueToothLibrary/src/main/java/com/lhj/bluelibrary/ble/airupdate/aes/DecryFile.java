package com.lhj.bluelibrary.ble.airupdate.aes;

import android.content.Context;

public class DecryFile {
	 public DecryFile(){

	    }

	    public byte[] startForAssets(Context context, String filename, String secretKey){
	        String encryStr = TypeConversionUtils.byte2StringNotHex(FileUtil.getAssets(context,filename));
	        //AES����
	        String decryStr = AesUtils.decrypt(secretKey, encryStr);
	        byte[] bytes = TypeConversionUtils.String2byteNotHex(decryStr);
	        return bytes;
	    }

	    public byte[] start(String encryFilepath,String secretKey){
	        String encryStr = TypeConversionUtils.byte2StringNotHex(FileUtil.readBytes(encryFilepath));
	        //AES����
	        String decryStr = AesUtils.decrypt(secretKey, encryStr);
	        byte[] bytes = TypeConversionUtils.String2byteNotHex(decryStr);
	        return bytes;
	    }

	    public void startLocal(String pathLocal,String encryFilepath,String secretKey){
	        String encryStr = TypeConversionUtils.byte2StringNotHex(FileUtil.readBytes(encryFilepath));
	        //AES����
	        String decryStr = AesUtils.decrypt(secretKey, encryStr);
	        byte[] bytes = TypeConversionUtils.String2byteNotHex(decryStr);
	        FileUtil.writeBytes(pathLocal,bytes);
	    }

	    public void startLocalForAssets(Context context, String filename,String secretKey,String pathLocal){
	        String encryStr = TypeConversionUtils.byte2StringNotHex(FileUtil.getAssets(context,filename));
	        //AES����
	        String decryStr = AesUtils.decrypt(secretKey, encryStr);
	        byte[] bytes = TypeConversionUtils.String2byteNotHex(decryStr);
	        FileUtil.writeBytes(pathLocal,bytes);
	    }

}
