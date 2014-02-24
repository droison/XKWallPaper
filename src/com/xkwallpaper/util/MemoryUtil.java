package com.xkwallpaper.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.text.format.Formatter;
import android.util.Log;

public class MemoryUtil {

	public static int memory = 0;

	public static String getAvailMemory(Activity mActivity) {// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) mActivity.getSystemService(mActivity.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存

		return Formatter.formatFileSize(mActivity, mi.availMem);// 将获取的内存大小规格化
	}

	public static int getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		int initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split(" ");
			String result = "";
			int i=0;
			for (String num : arrayOfString) {
				if(!num.equals("")){
					i++;
					if(i==2)
						result = num;
					Log.i(str2, num);
				}
			}

			initial_memory = Integer.valueOf(result).intValue() / 1024;// 获得系统总内存，单位是KB,除以1024转成MB
			localBufferedReader.close();

		} catch (Exception e) {
			
		}
		return initial_memory;// Byte转换为KB或者MB，内存大小规格化
	}

	public static int bitMapSize() {
		if (memory == 0)
			memory = getTotalMemory();
		if (memory > 600) {
			return 1;
		} else {
			return 2;
		}
	}
}
