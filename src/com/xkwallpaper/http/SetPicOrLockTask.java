package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.lockpaper.LockService;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.DpSpDip2Px;
import com.xkwallpaper.util.ImageUtil;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class SetPicOrLockTask extends AsyncTask<String, Integer, String> {
	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private String paperUrl;
	private DialogUtil dialogUtil;
	private Activity mActivity;
	private Paper paper;
	private String dir;
	private SharedPreferences.Editor lockpaperEdit;
	private final String TAG = "SetPicOrLockTask";
	private DpSpDip2Px dp2px;

	public SetPicOrLockTask(Activity mActivity, SharedPreferences.Editor lockpaperEdit, Paper paper, String dir) {
		this.mActivity = mActivity;
		this.paper = paper;
		this.dir = dir;
		this.lockpaperEdit = lockpaperEdit;
		dp2px = new DpSpDip2Px(mActivity);
		dialogUtil = new DialogUtil();
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
		dialogUtil.showProgressDialog(mActivity, "正在设置");
	}

	@Override
	protected String doInBackground(String... arg0) {

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

					FileOutputStream fos = new FileOutputStream(oldfile);
					hre = null;
					hre = HTTP.get(imageUrl);

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
		}else{
			isSuccess = true;
		}
		if (isSuccess)
			return newfile.getAbsolutePath();
		else
			return null;

	}

	@Override
	protected void onPostExecute(String path) {

		boolean isComplete = false;
		if (null != path) {
			if (dir.equals("lock")) {
				String filePath = AppConstants.Lock_Paper_Path;

				float w = mActivity.getResources().getDisplayMetrics().widthPixels;
				float h = mActivity.getResources().getDisplayMetrics().heightPixels;
				ImageUtil.bitMapCut(path, filePath, h / w);
				lockpaperEdit.putBoolean("is_create", true);
				lockpaperEdit.putString("paper_path", filePath);
				lockpaperEdit.putString("paper_thumb", paper.getMphoto());
				lockpaperEdit.putInt("paper_id", paper.getId());
				lockpaperEdit.commit();
				LockService.startLockService(mActivity);
				isComplete = true;

			} else if (dir.equals("pic")) {
				try {
					WallpaperManager wallpaperManager = WallpaperManager.getInstance(mActivity);
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					wallpaperManager.setBitmap(bitmap);
					bitmap.recycle();
					isComplete = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		dialogUtil.dismissProgressDialog();
		if (isComplete) {
			dialogUtil.showSetPicToast(mActivity, "设置成功");
		} else {
			dialogUtil.showSetPicToast(mActivity, "设置失败");
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
