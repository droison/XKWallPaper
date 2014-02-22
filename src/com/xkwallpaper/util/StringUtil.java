package com.xkwallpaper.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class StringUtil {

	private static final String TAG = "StringUtil";

	// 将输入流转换成字符串
	public static String inStream2String(InputStream is) throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return new String(baos.toByteArray());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}

	}

	public static boolean isEmpty(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String byte2String(byte[] is) throws Exception {
		String srt = new String(is, "UTF-8");
		return srt;
	}

	public static byte[] inStream2byte(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static boolean checkPhone(String phone){
        Pattern pattern = Pattern.compile("^13/d{9}||15[8,9]/d{8}$");
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher matcher = p.matcher(phone);
        
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
	
	public static boolean checkpasswd(String passwd){

        Pattern p = Pattern.compile("^.{6}$");
        Matcher matcher = p.matcher(passwd);
        
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
	
	//去除引号和逗号
	public static String delOper(String input){
		if(input == null)
			return null;
		input = input.replace("“", "");
		input = input.replace("”", "");
		input = input.replace(",", " ");
		return input;
	}
}
