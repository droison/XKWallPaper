package com.xkwallpaper.http;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.InfoUpdateBase;
import com.xkwallpaper.http.base.LoginOrRegResult;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class PutData implements Runnable {
	private Context mContext;
	private Handler mHandler;
	private String url;
	private Bitmap bm = null;
	private int type; // 1为昵称，2为手机，3为密码
	private InfoUpdateBase iub;
	private boolean isFace = false;
	private String private_token;

	private String TAG = "PutData";

	public PutData(Context mContext, Handler mHandler, String content, String token, int type) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.url = AppConstants.HTTPURL.infoUpdate;
		switch (type) {
		case 1:
			iub = new InfoUpdateBase(content, null, null);
			break;
		case 2:
			iub = new InfoUpdateBase(null, null, content);
			break;
		case 3:
			iub = new InfoUpdateBase(null, content, null);
			break;
		}
		iub.setPrivate_token(token);
		this.type = type;
	}

	public PutData(Context mContext, Handler mHandler, String token, Bitmap bm) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.url = AppConstants.HTTPURL.infoUpdate;
		this.private_token = token;
		this.bm = bm;
		isFace = true;
	}

	public void run() {

		Boolean b = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			b = networkInfo.isAvailable();
		}
		if (!b) {
			mHandler.sendEmptyMessage(AppConstants.HANDLER_MESSAGE_NONETWORK);
			return;
		}

		// HttpResponseEntity hre = HTTP.put(url, map, bm == null ? null :
		// bitmap2Bytes(bm));
		HttpResponseEntity hre = null;
		if (isFace) {
			hre = HTTP.put(url, private_token, bm);
		} else
			hre = HTTP.putByHttpUrlConnection(url, iub);
		if (hre == null) {
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			Log.e(TAG, "type=" + type + ";hre＝null");
			return;
		}
		switch (hre.getHttpResponseCode()) {
		case 200:
			try {
				String json = StringUtil.byte2String(hre.getB());
				Log.e(TAG, "type=" + type + ";JSON：" + json);
				LoginOrRegResult.Login login = (LoginOrRegResult.Login) JsonUtil.jsonToObject(json, LoginOrRegResult.Login.class);
				mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, login));
			} catch (Exception e) {
				mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
				Log.e(TAG, "type=" + type, e);
			}
			break;
		case 500:
			mHandler.sendEmptyMessage(AppConstants.HANDLER_MESSAGE_IMAGE500);
			Log.e(TAG, "type=" + type + ";问题：500");
			break;
		default:
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			Log.e(TAG, "type=" + type + ";问题：" + hre.getHttpResponseCode());
			break;
		}
	}

}
