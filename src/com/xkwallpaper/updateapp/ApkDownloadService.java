package com.xkwallpaper.updateapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.xkwallpaper.constants.AppConstants;

import android.os.Handler;
import android.util.Log;

public class ApkDownloadService implements Runnable {

	private static final String TAG = "APK_DOWNLOAD_SERVICE";

	private String downloadUrl = null;

	private Handler mHandler = null;

	private String appname;

	public ApkDownloadService(String downloadUrl, Handler hanlder, String appname) {
		this.mHandler = hanlder;
		this.downloadUrl = downloadUrl;
		this.appname = appname;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(downloadUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			int length = conn.getContentLength();
			InputStream is = conn.getInputStream();

			File file = new File(AppConstants.APP_FILE_PATH);
			if (!file.exists()) {
				file.mkdir();
			}
			File ApkFile = new File(file, appname);
			FileOutputStream fos = new FileOutputStream(ApkFile);

			int count = 0;
			byte buf[] = new byte[1024];

			do {
				int numread = is.read(buf);
				count += numread;
				int progress = (int) (((float) count / length) * 100);
				// 更新进度
				mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_APK_DOWNLOAD_PROGRESS, progress));
				if (numread <= 0) {
					// 下载完成通知安装
					mHandler.sendEmptyMessage(AppConstants.HANDLER_APK_DOWNLOAD_FINISH);
					break;
				}
				fos.write(buf, 0, numread);
			} while (true);

			fos.close();
			is.close();
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}

	}

}
