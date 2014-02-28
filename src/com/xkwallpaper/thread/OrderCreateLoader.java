package com.xkwallpaper.thread;

import com.xkwallpaper.alipay.AliPayThread;
import com.xkwallpaper.alipay.Result;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PayDAO;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.base.OrderCreateBase;
import com.xkwallpaper.http.base.OrderCreateResult;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.util.DialogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class OrderCreateLoader {

	private AccountDAO accountDAO;
	private DbAccount account;
	private Activity mActivity;
	private Paper paper;
	private OnPayCompleteListener onPayCompleteListener;
	private PayDAO payDAO;
	private DialogUtil dialogUtil;

	//false为付款失败，true为各种成功，反正就是继续往下走的意思
	public interface OnPayCompleteListener {
		public void call(boolean isComplete);
	}

	public void loadOrder(Activity mActivity, Paper paper, OnPayCompleteListener onPayCompleteListener) {
		// 这也不是支付啊！
		if (!paper.isPay()) {
			onPayCompleteListener.call(true);
			return;
		}

		this.mActivity = mActivity;
		this.paper = paper;
		this.onPayCompleteListener = onPayCompleteListener;

		accountDAO = new AccountDAO(mActivity);
		payDAO = new PayDAO(mActivity);
		account = accountDAO.getAccount();

		//没有登录
		if (account == null) {
			Toast.makeText(mActivity, "您需要先登录", Toast.LENGTH_SHORT).show();
			Intent toLogin = new Intent(mActivity, LoginActivity.class);
			mActivity.startActivity(toLogin);
			return;
		}
		//当前客户端就付过费
		if (payDAO.isExist(account.getToken(), paper.getId())) {
			onPayCompleteListener.call(true);
			return;
		}
		//一个POST判断当前用户是否付过费，此处应该添加progress
		dialogUtil = new DialogUtil();
		OrderCreateBase orb = new OrderCreateBase(account.getToken(), paper.getId());
		ThreadExecutor.execute(new PostData(mActivity, orderCreateHandler, orb, 10));
		
	}
	
	private Handler orderCreateHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				OrderCreateResult ocr = (OrderCreateResult) msg.obj;
				if(ocr.isResult()){
					//如果服务端支付过
					if(ocr.isStatus()){
						onPayCompleteListener.call(true);
						payDAO.save(account.getToken(), paper.getId());
					}else{
						//如果未支付过，根据生成的订单创建支付宝快捷支付
						String order_id = String.valueOf(ocr.getOrder_id());
						ThreadExecutor.execute(new AliPayThread(mActivity, alipayHandler, paper, order_id));
					}
				}else{
					Toast.makeText(mActivity, "错误："+ocr.getMessage(), Toast.LENGTH_SHORT).show();
				}
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(mActivity, "网络访问出错", Toast.LENGTH_SHORT).show();
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showNoNetWork(mActivity);
				break;
			}
		};
	};
	
	Handler alipayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			switch (msg.what) {
			case AppConstants.RQF_PAY:
				//此处再次判断result 如果成功
				if(result.isSuccess()){
					onPayCompleteListener.call(true);
					payDAO.save(account.getToken(), paper.getId());
				}
				//失败
				else{
					onPayCompleteListener.call(false);
					Toast.makeText(mActivity, result.getResult(), Toast.LENGTH_SHORT).show();
				}
				
				break;
			case AppConstants.RQF_LOGIN:
				break;
			default:
				break;
			}
		};
	};
}
