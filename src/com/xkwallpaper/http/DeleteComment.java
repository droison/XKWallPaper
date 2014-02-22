package com.xkwallpaper.http;

import com.xkwallpaper.constants.AppConstants;

import android.os.Handler;

public class DeleteComment implements Runnable {
	private Handler mHandler;
	private String url;
	
	public DeleteComment(Handler mHandler,String url){
		this.mHandler = mHandler;
		this.url = url;
	}
	public void run() {
		boolean b = HTTP.deleteMothod(url);
		if(b){
				mHandler.sendEmptyMessage(AppConstants.HANDLER_MESSAGE_NORMAL);
		}else{
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
		}
	}
	
}