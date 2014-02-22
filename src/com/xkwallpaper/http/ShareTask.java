package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.ImageUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class ShareTask extends AsyncTask<String, Integer, String> {

	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private Activity mActivity;
	private String imageUrl;
	private Paper paper;
	private DialogUtil dialogUtil;
	private final String TAG = "ShareTask";
	private String dir;

	// dir来识别是壁纸、锁屏还是视频
	public ShareTask(Activity mActivity, Paper paper, String dir) {
		this.mActivity = mActivity;
		this.paper = paper;
		dialogUtil = new DialogUtil();
		this.dir = dir;
	}

	@Override
	protected void onPreExecute() {
		// 此处下显示progressDialog
		dialogUtil.showDownloadDialog(mActivity, "准备数据...");
	}

	@Override
	protected String doInBackground(String... arg0) {
		imageUrl = AppConstants.HTTPURL.serverIP + paper.getMphoto();
		String newName = paper.getId() + ".jpg";
		String oldName = newName + ".thumb";

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/share", oldName);
		File newfile = new File(AppConstants.APP_FILE_PATH + "/share", newName);
		File thumbFile = new File(AppConstants.APP_FILE_PATH + "/" + dir, paper.getId() + ".thumb");

		File filePath = new File(AppConstants.APP_FILE_PATH + "/share");
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}

		boolean isSuccess = false;
		if (!newfile.exists()) {
			if (thumbFile.exists()) {
				try {
					ImageUtil.copyFile(thumbFile.getAbsolutePath(), newfile.getAbsolutePath());
					isSuccess = true;
				} catch (Exception e) {
					Log.e(TAG, "fileCopy", e);
				}
			} else {
				try {

					FileOutputStream fos = new FileOutputStream(oldfile);
					HttpResponseEntity hre = null;
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
		} else {
			isSuccess = true;
		}
		if (isSuccess)
			return newfile.getAbsolutePath();
		else
			return null;
	}

	@Override
	protected void onPostExecute(String path) {

		if (null != path) {

			OnekeyShare oks = new OnekeyShare();
			oks.setNotification(R.drawable.ic_launcher, mActivity.getString(R.string.app_name));
			oks.setTitle(mActivity.getString(R.string.share));
			oks.setTitleUrl(imageUrl);
			oks.setText(mActivity.getString(R.string.share_content));
			oks.setImagePath(path);
			oks.setImageUrl(imageUrl);
			oks.setUrl(imageUrl);
			// oks.setFilePath(AppConstants.HTTPURL.serverIP +
			// paper.getMphoto());
			oks.setComment(mActivity.getString(R.string.share));
			oks.setSite(mActivity.getString(R.string.app_name));
			oks.setSiteUrl(imageUrl);
			oks.setSilent(false);
			// 去除注释，可令编辑页面显示为Dialog模式
			oks.setDialogMode();
			// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
			oks.setCallback(new OneKeyShareCallback());

			oks.show(mActivity);
		}
		dialogUtil.dismissDownloadDialog();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public class OneKeyShareCallback implements PlatformActionListener {

		public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
			// Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
		}

		public void onError(Platform plat, int action, Throwable t) {
			Log.e("shareCallBack", "失败", t);
			// Toast.makeText(mActivity, "分享失败，请稍后重试",
			// Toast.LENGTH_SHORT).show();;
		}

		public void onCancel(Platform plat, int action) {
			// 在这里添加取消分享的处理代码
		}

	}

}
