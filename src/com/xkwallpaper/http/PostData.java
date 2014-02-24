package com.xkwallpaper.http;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.CommentResult;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.LoginOrRegResult;
import com.xkwallpaper.http.base.OrderCreateResult;
import com.xkwallpaper.http.base.PraiseResult;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class PostData implements Runnable {
	private Context mContext;
	private Handler mHandler;
	private String url;
	private Object obj;
	private int type; // 1为赞，2为评论,3为短信验证,4为手机注册,5为登录,6为绑定帐号，7为第三方登录,8是忘记密码验证码，9是重置密码提交,10是订单生成
	
	private String TAG = "PostData";

	public PostData(Context mContext, Handler mHandler, Object obj, int type) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		switch (type) {
		case 1:
			this.url = AppConstants.HTTPURL.picPraise;
			break;
		case 2:
			this.url = AppConstants.HTTPURL.picComment;
			break;
		case 3:
			this.url = AppConstants.HTTPURL.regPhoneVerify;
			break;
		case 4:
			this.url = AppConstants.HTTPURL.reg;
			break;
		case 5:
			this.url = AppConstants.HTTPURL.login;
			break;
		case 6:
			this.url = AppConstants.HTTPURL.socialBind;
			break;
		case 7:
			this.url = AppConstants.HTTPURL.socialLogin;
			break;
		case 8:
			this.url = AppConstants.HTTPURL.lostPwdVerify;
			break;
		case 9:
			this.url = AppConstants.HTTPURL.lostPwdSubmit;
			break;
		case 10:
			this.url = AppConstants.HTTPURL.orderCreate;
			break;
		default:
			break;
		}
		this.type = type;
		this.obj = obj;
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

		HttpResponseEntity hre = HTTP.postByHttpUrlConnection(url, obj);
		switch (hre.getHttpResponseCode()) {
		case 200:
			try {
				String json = StringUtil.byte2String(hre.getB());
				Log.e(TAG, "type="+type+";JSON：" + json);
				switch (type) {
				case 1:
					PraiseResult pr = (PraiseResult) JsonUtil.jsonToObject(json, PraiseResult.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pr));
					break;
				case 2:
					CommentResult cr = (CommentResult) JsonUtil.jsonToObject(json, CommentResult.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, cr));
					break;
				case 3:
					LoginOrRegResult.PhoneVerify pv = (LoginOrRegResult.PhoneVerify) JsonUtil.jsonToObject(json, LoginOrRegResult.PhoneVerify.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pv));
					break;
				case 4:
					LoginOrRegResult.Reg reg = (LoginOrRegResult.Reg) JsonUtil.jsonToObject(json, LoginOrRegResult.Reg.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, reg)); 
					break;
				case 5:
					LoginOrRegResult.Login login = (LoginOrRegResult.Login) JsonUtil.jsonToObject(json, LoginOrRegResult.Login.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, login)); 
					break;
				case 6:
					LoginOrRegResult.Login login1 = (LoginOrRegResult.Login) JsonUtil.jsonToObject(json, LoginOrRegResult.Login.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, login1)); 
					break;
				case 7:
					LoginOrRegResult.Login login2 = (LoginOrRegResult.Login) JsonUtil.jsonToObject(json, LoginOrRegResult.Login.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, login2)); 
					break;
				case 8:
					LoginOrRegResult.PhoneVerify pv2 = (LoginOrRegResult.PhoneVerify) JsonUtil.jsonToObject(json, LoginOrRegResult.PhoneVerify.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pv2));
					break;
				case 9:
					LoginOrRegResult.PhoneVerify pv3 = (LoginOrRegResult.PhoneVerify) JsonUtil.jsonToObject(json, LoginOrRegResult.PhoneVerify.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, pv3));
					break;
				case 10:
					OrderCreateResult ocr = (OrderCreateResult) JsonUtil.jsonToObject(json, OrderCreateResult.class);
					mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, ocr));
					break;
				default:
					break;
				}

			} catch (Exception e) {
				mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
				Log.e(TAG, "type="+type+";问题：",e);
			}
			break;
		default:
			mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			Log.d(TAG, "type="+type+";问题："+hre.getHttpResponseCode());
			break;
		}
	}

}
