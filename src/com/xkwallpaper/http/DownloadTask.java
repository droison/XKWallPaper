package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.DownloadDAO;
import com.xkwallpaper.http.base.DownloadResult;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.DpSpDip2Px;
import com.xkwallpaper.util.ImageUtil;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, String> {

	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private Activity mActivity;
	private String downUrl;
	private Paper paper;
	private String dir;
	private DialogUtil dialogUtil;
	private DpSpDip2Px dp2px;
	private DownloadDAO downloadDAO;
	private DownCompleteCallBack downCompleteCallBack;
	private final String TAG = "DownTask";

	public interface DownCompleteCallBack {
		public void call(boolean isSuccess);
	}

	// dir来识别是壁纸、锁屏还是视频
	public DownloadTask(Activity mActivity, String dir, Paper paper, DownCompleteCallBack downCompleteCallBack) {
		this.mActivity = mActivity;
		this.paper = paper;
		this.dir = dir;
		this.downCompleteCallBack = downCompleteCallBack;
		downUrl = AppConstants.HTTPURL.downloadUrl;
		dialogUtil = new DialogUtil();
		dp2px = new DpSpDip2Px(mActivity);
		downloadDAO = new DownloadDAO(mActivity);
	}

	@Override
	protected void onPreExecute() {
		// 此处下显示progressDialog
		dialogUtil.showDownloadDialog(mActivity, "正在下载...");
	}

	@Override
	protected String doInBackground(String... arg0) {
		String imageUrl = "";
		String oldName = "";
		String newName = "";

		boolean isGetUrlSuccess = false;
		DownPostBase dpb = new DownPostBase();
		dpb.setPaper_id(paper.getId());
		HttpResponseEntity hre = HTTP.postByHttpUrlConnection(downUrl, dpb);
		if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
			try {
				String json = StringUtil.byte2String(hre.getB());
				DownloadResult dr = (DownloadResult) JsonUtil.jsonToObject(json, DownloadResult.class);
				if (dr.isResult()) {
					if (dir.equals("vid")) {
						if (dr.getVideo() != null && dr.getVideo().length() != 0) {
							isGetUrlSuccess = true;
							imageUrl = dr.getVideo();
							newName = paper.getId() + ".mp4";
							paper.setVideo(dr.getVideo());
							oldName = newName + ".temp";
						}
					} else {
						if (dp2px.getSuitPhoto(dr) != null && dp2px.getSuitPhoto(dr).length() != 0) {
							isGetUrlSuccess = true;
							imageUrl = AppConstants.HTTPURL.serverIP + dp2px.getSuitPhoto(dr);
							newName = paper.getId() + ".jpg";
							oldName = newName + ".temp";
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!isGetUrlSuccess)
			return null;

		if (dir.equals("vid")) {
			SaveVideo sv = new SaveVideo();
			return sv.saveVideoToLocal(paper);
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/download", oldName);
		File newfile = new File(AppConstants.APP_FILE_PATH + "/download", newName);
		File preViewFile = new File(AppConstants.APP_FILE_PATH + "/" + dir, paper.getId() + ".pre");

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

		boolean isComplete = false;
		if (null != path) {
			downloadDAO.save(paper, dir);
			isComplete = true;
		}
		dialogUtil.dismissDownloadDialog();
		if (isComplete) {
			dialogUtil.showSetPicToast(mActivity, "下载成功！");
			if (downCompleteCallBack != null) {
				downCompleteCallBack.call(true);
			}
		} else {
			dialogUtil.showSetPicToast(mActivity, "下载失败，请稍后重试");
			if (downCompleteCallBack != null) {
				downCompleteCallBack.call(false);
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	class DownPostBase {
		private int paper_id;

		public int getPaper_id() {
			return paper_id;
		}

		public void setPaper_id(int paper_id) {
			this.paper_id = paper_id;
		}
	}

}
