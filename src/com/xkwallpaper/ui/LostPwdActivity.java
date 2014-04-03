/**
 * 找回密码页面
 */
package com.xkwallpaper.ui;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.base.LoginOrReg;
import com.xkwallpaper.http.base.LoginOrRegResult;
import com.xkwallpaper.http.base.LoginOrRegResult.PhoneVerify;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.util.DialogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LostPwdActivity extends BaiduMTJActivity implements OnClickListener {

	private EditText lostpwd_password, lostpwd_repassword, lostpwd_phone, lostpwd_checknum;
	private TextView lostpwd_check, lostpwd_submit;
	private DialogUtil dialogUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostpwd);
		initData();
		setUpView();

	}

	// 初始化view并初始化fragment
	private void setUpView() {
		lostpwd_password = (EditText) this.findViewById(R.id.lostpwd_password);
		lostpwd_repassword = (EditText) this.findViewById(R.id.lostpwd_repassword);
		lostpwd_phone = (EditText) this.findViewById(R.id.lostpwd_phone);
		lostpwd_checknum = (EditText) this.findViewById(R.id.lostpwd_checknum);

		lostpwd_check = (TextView) this.findViewById(R.id.lostpwd_check);
		lostpwd_submit = (TextView) this.findViewById(R.id.lostpwd_submit);
		((ImageView) this.findViewById(R.id.title_one_name_image)).setImageResource(R.drawable.title_zhaohuimima);
		lostpwd_check.setTag("未验证");
		lostpwd_check.setOnClickListener(this);
		lostpwd_submit.setOnClickListener(this);

		dialogUtil = new DialogUtil();
	}

	private void initData() {

	}

	private Handler phoneVerifyHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			lostpwd_check.setTag("未验证");
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.PhoneVerify pv = (LoginOrRegResult.PhoneVerify) msg.obj;
				if (pv.isResult()) {
					lostpwd_check.setTag("正在验证");
					dialogUtil.showSetPicToast(LostPwdActivity.this, "发送成功，请注意查收");
					ThreadExecutor.execute(timeRunable);
				} else {
					dialogUtil.showSetPicToast(LostPwdActivity.this, "失败：手机号已经注册");
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(LostPwdActivity.this, "发送验证码失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(LostPwdActivity.this, "发送验证码失败，请稍后重试");
				break;
			}

		};
	};

	private Handler submiltHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.PhoneVerify pv3 = (PhoneVerify) msg.obj;
				if (pv3.isResult()) {
					dialogUtil.showSetPicToast(LostPwdActivity.this, "修改成功");
					finish();
				} else {
					dialogUtil.showSetPicToast(LostPwdActivity.this, "错误："+pv3.getMessage());
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(LostPwdActivity.this, "修改失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(LostPwdActivity.this, "修改失败，请稍后重试");
				break;
			}

		};
	};

	private Handler timerHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 112:
				lostpwd_check.setText("验证");
				lostpwd_check.setTag("未验证");
				break;
			default:
				lostpwd_check.setText(msg.what + "s...");
				break;
			}
		}
	};

	Runnable timeRunable = new Runnable() {

		@Override
		public void run() {
			int i = 10;
			while (i-- > 0) {
				timerHandler.sendEmptyMessage(i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			timerHandler.sendEmptyMessage(112);
		}
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.lostpwd_check:
			String phone = lostpwd_phone.getText().toString();
			if (TextUtils.isEmpty(phone) || phone.length() != 11) {
				dialogUtil.showSetPicToast(this, "号码为空或长度不对");
			} else {
				if (lostpwd_check.getTag().equals("未验证")) {
					dialogUtil.showDownloadDialog(this, "正在发送");
					lostpwd_check.setTag("正在验证");
					LoginOrReg.PhoneVerify ver = new LoginOrReg.PhoneVerify(phone);
					ThreadExecutor.execute(new PostData(this, phoneVerifyHandler, ver, 8));
				}

			}
			break;
		case R.id.lostpwd_submit:
			phone = lostpwd_phone.getText().toString();
			String password = lostpwd_password.getText().toString();
			String rePassword = lostpwd_repassword.getText().toString();
			String verify = lostpwd_checknum.getText().toString();
			if (TextUtils.isEmpty(password)) {
				dialogUtil.showSetPicToast(this, "密码不能为空");
			} else if (TextUtils.isEmpty(rePassword)) {
				dialogUtil.showSetPicToast(this, "请在输入一次密码");
			} else if (TextUtils.isEmpty(phone)) {
				dialogUtil.showSetPicToast(this, "手机号不能为空");
			} else if (TextUtils.isEmpty(verify)) {
				dialogUtil.showSetPicToast(this, "验证码不能为空");
			} else if (password.length() < 8) {
				dialogUtil.showSetPicToast(this, "密码长度要大于8位");
			} else if (!password.equals(rePassword)) {
				dialogUtil.showSetPicToast(this, "两次密码输入不一致");
			} else if (phone.length() != 11) {
				dialogUtil.showSetPicToast(this, "手机号格式错误");
			} else {
				LoginOrReg.Reg reg = new LoginOrReg.Reg(null, password, rePassword, phone, verify);
				dialogUtil.showDownloadDialog(this, "正在提交");
				ThreadExecutor.execute(new PostData(this, submiltHandler, reg, 9));
			}
			break;
		default:
			break;
		}
	}

}
