package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.util.DpSpDip2Px;
import com.xkwallpaper.util.ImageUtil;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SeekBar;

public class PreviewTask extends AsyncTask<String, Integer, String> {
	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private String paperUrl;
	private Paper paper;
	private String dir;
	private SeekBar bar;
	private final String TAG = "PreviewTask";
	private PreviewCallBack previewCallBack;
	private DpSpDip2Px dp2px;

	public interface PreviewCallBack {
		public void onTaskCancle();

		public void onDownComplete(boolean isSuccess, String path);
	}

	public PreviewTask(Activity mActivity, SeekBar bar, Paper paper, String dir, PreviewCallBack previewCallBack) {
		this.paper = paper;
		this.dir = dir;
		this.bar = bar;
		this.previewCallBack = previewCallBack;
		dp2px = new DpSpDip2Px(mActivity);
		if(dir.equals("lock")){
			paperUrl = AppConstants.HTTPURL.lockInfo + paper.getId();
		}else if(dir.equals("pic")){
			paperUrl = AppConstants.HTTPURL.picInfo + paper.getId();
		}else{
			paperUrl = AppConstants.HTTPURL.vidInfo + paper.getId();
		}
	}

	@Override
	protected void onPreExecute() {
		// 此处下显示progressDialog
	}

	// doInBackground方法内部执行后台任务,不可在此方法内修改UI
	// 这个类现在已经完全作为下载图片到本地的类了
	@Override
	protected String doInBackground(String... params) {

		HttpResponseEntity hre = HTTP.get(paperUrl);
		if(hre.getHttpResponseCode()==HttpStatus.SC_OK){
			String json;
			try {
				json = StringUtil.byte2String(hre.getB());
				paper = (Paper) JsonUtil.jsonToObject(json, Paper.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
		
		String imageUrl = dp2px.getSuitPhoto(paper);
		
		String oldName = "";
		String newName = "";

		if (imageUrl != null && imageUrl.length() != 0) {
			imageUrl = AppConstants.HTTPURL.serverIP + imageUrl;
			newName = paper.getId() + ".pre";
			oldName = newName + ".temp";
		} else {
			return null;
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/" + dir, oldName);
		File newfile = new File(AppConstants.APP_FILE_PATH + "/" + dir, newName);
		File downfile = new File(AppConstants.APP_FILE_PATH + "/download/" + paper.getId() + ".jpg");

		File filePath = new File(AppConstants.APP_FILE_PATH + "/download");
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}

		boolean isSuccess = false;
		if (!newfile.exists()) {
			if (downfile.exists()) {
				try {
					ImageUtil.copyFile(downfile.getAbsolutePath(), newfile.getAbsolutePath());
					isSuccess = true;
				} catch (Exception e) {
					Log.e(TAG, "fileCopy", e);
				}
			} else {

				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(imageUrl);
					HttpResponse response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						InputStream is = entity.getContent();

						long total = entity.getContentLength();
						Log.i("total", String.valueOf(total));
						OutputStream OS = new FileOutputStream(oldfile);
						byte[] buffer = new byte[1024 * 4];
						int len = -1;
						int count = 0;
						while ((len = is.read(buffer)) != -1) {
							OS.write(buffer, 0, len);
							count += len;
							publishProgress((int) ((count / (float) total) * 100));
						}
						OS.flush();
						OS.close();
						is.close();
						oldfile.renameTo(newfile);
						isSuccess = true;
					}
				} catch (IOException e) {
					Log.e(TAG, "http", e);
				}
			}
		} else {
			isSuccess = true;
		}
		if (isSuccess)
			return newfile.getAbsolutePath();
		else
			return null;

	}

	// onProgressUpdate方法用于更新进度信息
	@Override
	protected void onProgressUpdate(Integer... progresses) {
		bar.setProgress(progresses[0]);
	}

	@Override
	protected void onPostExecute(String path) {

		if (null != path) {
			if (previewCallBack != null)
				previewCallBack.onDownComplete(true, path);
		} else {
			if (previewCallBack != null)
				previewCallBack.onDownComplete(false, path);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (previewCallBack != null)
			previewCallBack.onTaskCancle();
	}
}
