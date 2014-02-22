package com.xkwallpaper.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.android.app.sdk.AliPay;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.Paper;

public class AliPayThread implements Runnable {
	public final String TAG = "alipay-sdk";
	private Activity mActivity;
	private Handler mHandler;
	private Paper paper;
	private String order_id;

	public AliPayThread(Activity mActivity, Handler mHandler, Paper paper,String order_id) {
		this.mActivity = mActivity;
		this.mHandler = mHandler;
		this.paper = paper;
		this.order_id = order_id;
	}

	public void run() {

		String info = getNewOrderInfo();
		String sign = Rsa.sign(info, Keys.PRIVATE);
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&" + getSignType();
		Log.i(TAG, "start pay");
		// start the pay.
		Log.i(TAG, "info = " + info);

		final String orderInfo = info;

		AliPay alipay = new AliPay(mActivity, mHandler);

		// 设置为沙箱模式，不设置默认为线上环境
		// alipay.setSandBox(true);

		String result = alipay.pay(orderInfo);

		Log.i(TAG, "result = " + result);
		Message msg = new Message();
		msg.what = AppConstants.RQF_PAY;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(paper.getTitle());
		sb.append("\"&body=\"");
		sb.append(paper.getTags().toString());
		sb.append("\"&total_fee=\"");
		sb.append(paper.getPrice());
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	private String getOutTradeNo() {
		return order_id;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	// 快登
	private void doLogin(String appUserId) {
		final String orderInfo = trustLogin(Keys.DEFAULT_PARTNER, appUserId);
		new Thread() {
			public void run() {
				String result = new AliPay(mActivity, mHandler).pay(orderInfo);

				Log.i(TAG, "result = " + result);
				Message msg = new Message();
				msg.what = AppConstants.RQF_LOGIN;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	// 快登
	private String trustLogin(String partnerId, String appUserId) {
		StringBuilder sb = new StringBuilder();
		sb.append("app_name=\"mc\"&biz_type=\"trust_login\"&partner=\"");
		sb.append(partnerId);
		Log.d("TAG", "UserID = " + appUserId);
		if (!TextUtils.isEmpty(appUserId)) {
			appUserId = appUserId.replace("\"", "");
			sb.append("\"&app_id=\"");
			sb.append(appUserId);
		}
		sb.append("\"");

		String info = sb.toString();

		// 请求信息签名
		String sign = Rsa.sign(info, Keys.PRIVATE);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		info += "&sign=\"" + sign + "\"&" + getSignType();

		return info;
	}

}