package com.xkwallpaper.http;

import org.apache.http.HttpStatus;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.app.Activity;
import android.os.AsyncTask;

public class ViewVideoTask extends AsyncTask<String, Integer, String> {

	// onPreExecute方法用于在执行后台任务前做一些UI操作
	private Activity mActivity;
	private Paper paper;
	private DialogUtil dialogUtil;
	private DownViewCallBack downCompleteCallBack;
	private final String TAG = "ViewVideoTask";

	public interface DownViewCallBack {
		public void call(boolean isSuccess, String path);
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
		HttpResponseEntity hre = HTTP.get(AppConstants.HTTPURL.vidInfo + paper.getId());
		if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
			try {
				String json = StringUtil.byte2String(hre.getB());
				paper = (Paper) JsonUtil.jsonToObject(json, Paper.class);
				if (paper.getVideo() != null && paper.getVideo().length() != 0) {
					SaveVideo sv = new SaveVideo();
					return sv.saveVideoToLocal(paper);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
				downCompleteCallBack.call(false, "");
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
