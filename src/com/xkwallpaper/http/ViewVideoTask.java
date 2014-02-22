package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.ImageUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class ViewVideoTask extends AsyncTask<String, Integer, String> {

	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private Activity mActivity;
	private Paper paper;
	private DialogUtil dialogUtil;
	private DownViewCallBack downCompleteCallBack;
	private final String TAG = "ViewVideoTask";

	public interface DownViewCallBack {
		public void call(boolean isSuccess,String path);
	}

	public ViewVideoTask(Activity mActivity, Paper paper, DownViewCallBack downCompleteCallBack) {
		this.mActivity = mActivity;
		this.paper = paper;
		this.downCompleteCallBack = downCompleteCallBack;
		dialogUtil = new DialogUtil();
	}

	@Override
	protected void onPreExecute() {
		// 此处下显示progressDialog
		dialogUtil.showDownloadDialog(mActivity, "正在读取，请稍后...");
	}

	@Override
	protected String doInBackground(String... arg0) {
		String imageUrl = "";
		String oldName = "";
		String newName = "";

		if (paper.getVideo() != null && paper.getVideo().length() != 0) {
			imageUrl = paper.getVideo();
			newName = paper.getId() + ".mp4";
			oldName = newName + ".temp";
		} else {
			return null;
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/download", oldName);
		File newfile = new File(AppConstants.APP_FILE_PATH + "/download", newName);
		File preViewFile = new File(AppConstants.APP_FILE_PATH + "/vid", paper.getId() + ".pre");

		File filePath = new File(AppConstants.APP_FILE_PATH + "/download");
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}

		boolean isSuccess = false;
		if (!newfile.exists()) {
			if (preViewFile.exists()) {
				try {
					ImageUtil.copyFile(preViewFile.getAbsolutePath(), newfile.getAbsolutePath());
					isSuccess = true;
				} catch (Exception e) {
					Log.e(TAG, "fileCopy", e);
				}
			} else {
				try {

					FileOutputStream fos = new FileOutputStream(oldfile);
					HttpResponseEntity hre = HTTP.get(imageUrl);

					byte[] is = null;
					if (hre != null) {
						if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
							is = hre.getB();
							for (int i = 0; i < is.length; i++) {
								fos.write(is[i]);
							}
							fos.close();
							oldfile.renameTo(newfile);
							isSuccess = true;
						} else {
							Log.e(TAG, "httpcode!=200");
						}
					} else {
						Log.e(TAG, "hre==null");
					}
				} catch (IOException e) {
					Log.e(TAG, "http", e);
				}
			}
		}
		if (isSuccess)
			return newfile.getAbsolutePath();
		else
			return null;
	}

	@Override
	protected void onPostExecute(String path) {
		dialogUtil.dismissDownloadDialog();
		if (null != path) {
			if (downCompleteCallBack != null) {
				downCompleteCallBack.call(true, path);
			}
		} else {
			if (downCompleteCallBack != null) {
				downCompleteCallBack.call(false, path);
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
