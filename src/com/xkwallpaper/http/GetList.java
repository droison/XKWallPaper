package com.xkwallpaper.http;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.CommentList;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.LockList;
import com.xkwallpaper.http.base.PPTList;
import com.xkwallpaper.http.base.PicList;
import com.xkwallpaper.http.base.TagList;
import com.xkwallpaper.http.base.VidList;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetList implements Runnable {
	private Context mContext;
	private Handler mHandler;
	private String url;
	private int type;// 1为壁纸，2为锁屏，3为视频,4为评论,5为PPT，6为返回的热门标签
	private String TAG = "GetList";

	public GetList(Context mContext, Handler mHandler, String url, int type) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.url = url;
		this.type = type;
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

		HttpResponseEntity hre = HTTP.get(url);
		if(hre == null){
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			Log.e(TAG,"type="+type+";hre＝null");
			return;
		}
		switch (hre.getHttpResponseCode()) {
		case 200:
			try {
				String json = StringUtil.byte2String(hre.getB());
				Log.e(TAG, "type="+type+";JSON：" + json);
				switch (type) {
				case 1:
					PicList pl = (PicList) JsonUtil.jsonToObject(json, PicList.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pl.getPapers()));
					break;
				case 2:
					LockList ll = (LockList) JsonUtil.jsonToObject(json, LockList.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, ll.getScreens()));
					break;
				case 3:
					VidList vl = (VidList) JsonUtil.jsonToObject(json, VidList.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, vl.getMovies()));
					break;
				case 4:
					CommentList cl = (CommentList) JsonUtil.jsonToObject(json, CommentList.class);
					Message msg = new Message();
					msg.what = AppConstants.HANDLER_MESSAGE_NORMAL;
					msg.arg1 = cl.getComment_num();
					msg.obj = cl.getComments();
					mHandler.sendMessage(msg);
					break;
				case 5:
					PPTList pptl = (PPTList) JsonUtil.jsonToObject(json, PPTList.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pptl.getPpts()));
					break;
				case 6:
					TagList tl = (TagList) JsonUtil.jsonToObject(json, TagList.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, tl.getTags()));
					break;
				}

			} catch (Exception e) {
				mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
				Log.e("StringGet", "200", e);
			}
			break;
		default:
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			Log.d("StringGet", "" + hre.getHttpResponseCode());
			break;
		}
	}

}
