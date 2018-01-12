package com.lhj.bluelibrary.ble.airupdate.aes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * ����URL��ȡ�ļ���
     * 
     * @param url
     *            URL
     * @return �ļ���
     */
    public static String getFileNameFromUrl(String url) {
        if (url.indexOf("/") != -1)
            return url.substring(url.lastIndexOf("/")).replace("/", "");
        else
            return url;
    }

    /**
     * TODO<����·��ɾ��ָ����Ŀ¼���ļ������۴������>
     * 
     * @return boolean
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // Ϊ�ļ�ʱ����ɾ���ļ�����
                return deleteFile(filePath);
            } else {
                // ΪĿ¼ʱ����ɾ��Ŀ¼����
                return deleteDir(filePath);
            }
        }
    }

    /**
     * TODO<�����ļ���>
     * 
     * @return File
     */
    public static File createDir(String path) {
        File dir = new File(path);
        if (!isExist(dir)) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * TODO<ɾ���ļ��м��ļ����µ��ļ�>
     * 
     * @return boolean
     */
    public static boolean deleteDir(String dirPath) {
        boolean flag = false;
        // ���dirPath�����ļ��ָ�����β���Զ�����ļ��ָ���
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);

        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

        flag = true;
        File[] files = dirFile.listFiles();
        // ����ɾ���ļ����µ������ļ�(������Ŀ¼)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // ɾ�����ļ�
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                // ɾ����Ŀ¼
                flag = deleteDir(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // ɾ����ǰ��Ŀ¼
        return dirFile.delete();
    }

    /**
     * TODO<��ȡָ��Ŀ¼���ļ��ĸ���>
     * 
     * @return int
     */
    public static int getFileCount(String dirPath) {
        int count = 0;

        // ���dirPath�����ļ��ָ�����β���Զ�����ļ��ָ���
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);

        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return count;
        }

        // ��ȡ��Ŀ¼�����е������ļ�(�ļ�����Ŀ¼)
        File[] files = dirFile.listFiles();
        // ����ɾ���ļ����µ������ļ�(������Ŀ¼)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // ɾ�����ļ�
                count += 1;
            }
        }

        return count;
    }

    /**
     * TODO<�����ļ�>
     * 
     * @return File
     */
    public static File createFile(String path, String fileName) {
        File file = new File(createDir(path), fileName);
        if (!isExist(file)) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * TODO<ɾ��ָ����ַ���ļ���>
     * 
     * @return void
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && isExist(file))
            return file.delete();
        return false;
    }

    /**
     *  ��ȡassetsָ���ļ�����byte[]
     * @param context
     * @param fileName
     * @return
     */
    public static byte[] getAssets(Context context,String fileName){
    	AssetManager am = context.getAssets();
        byte[] bytes = null;
    	try {
			InputStream is = am.open(fileName);
            int size = is.available();
            bytes = new byte[size];
            is.read(bytes);
            is.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
        return bytes;
    }

    /**
     *  ��ȡrawָ���ļ�����byte[]
     * @param context
     * @param rawId
     * @return
     */
    public static byte[] getRaws(Context context,int rawId){
        byte[] bytes = null;
        try {
            InputStream is = context.getResources().openRawResource(rawId);
            int size = is.available();
            bytes = new byte[size];
            is.read(bytes);
            is.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * ���ļ���д������
     *
     * @param filePath
     *            Ŀ���ļ�ȫ·��
     * @param data
     *            Ҫд�������
     * @return true��ʾд��ɹ�  false��ʾд��ʧ��
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ���ļ��ж�ȡ����
     *
     * @param file
     * @return
     */
    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���ļ��ж�ȡ����
     *
     * @param uri
     * @return
     */
    public static byte[] readBytes(Uri uri) {
        try {
            FileInputStream fis = new FileInputStream(uri.getPath());
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
     * ���Ƶ����ļ�
     * 
     * @param srcPath
     *            String ԭ�ļ�·��
     * @param desPath
     *            String Ŀ��·��
     */
    public static void copyFile(String srcPath, String desPath) {
        int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(srcPath);

        if (isExist(oldfile)) {// Դ�ļ�����
            try {
                InputStream is = new FileInputStream(srcPath); // ����ԭ�ļ�
                FileOutputStream os = new FileOutputStream(desPath);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread; // �ֽ��� �ļ���С
                    os.write(buffer, 0, byteread);
                }
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {// Դ�ļ�������
        }
    }

    /**
     * ���������ļ�������
     * 
     * @param srcPath
     *            String ԭ�ļ�·��
     * @param desPath
     *            String ���ƺ�·��
     */
    public static void copyFolder(String srcPath, String desPath) {

        try {
            (new File(desPath)).mkdirs(); // ����ļ��в����� �������ļ���
            File a = new File(srcPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (srcPath.endsWith(File.separator)) {
                    temp = new File(srcPath + file[i]);
                } else {
                    temp = new File(srcPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(desPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// ��������ļ���
                    copyFolder(srcPath + "/" + file[i], desPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * TODO<�ж�File������ָ��Ŀ¼���ļ��Ƿ����>
     * 
     * @return boolean
     */
    public static boolean isExist(File file) {
        return file.exists();
    }

}
