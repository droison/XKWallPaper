package com.xkwallpaper.ui;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.base.LoginOrReg;
import com.xkwallpaper.http.base.LoginOrRegResult;
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

public class RegisterActivity extends BaiduMTJActivity implements OnClickListener {

	private EditText reg_username, reg_password, reg_repassword, reg_phone, reg_checknum;
	private TextView reg_check, reg_reg, reg_login;
	private DialogUtil dialogUtil;
	private AccountDAO accountDAO;
	private DbAccount dbAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initData();
		setUpView();

	}

	// 初始化view并初始化fragment
	private void setUpView() {
		reg_username = (EditText) this.findViewById(R.id.reg_username);
		reg_password = (EditText) this.findViewById(R.id.reg_password);
		reg_repassword = (EditText) this.findViewById(R.id.reg_repassword);
		reg_phone = (EditText) this.findViewById(R.id.reg_phone);
		reg_checknum = (EditText) this.findViewById(R.id.reg_checknum);

		reg_check = (TextView) this.findViewById(R.id.reg_check);
		reg_reg = (TextView) this.findViewById(R.id.reg_reg);
		reg_login = (TextView) this.findViewById(R.id.reg_login);
		((ImageView) this.findViewById(R.id.title_one_name_image)).setImageResource(R.drawable.title_zhuce);
		reg_check.setTag("未验证");
		reg_check.setOnClickListener(this);
		reg_reg.setOnClickListener(this);
		reg_login.setOnClickListener(this);

		dialogUtil = new DialogUtil();
		accountDAO = new AccountDAO(this);
		dbAccount = new DbAccount();
	}

	private void initData() {

	}

	private Handler phoneVerifyHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			reg_check.setTag("未验证");
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.PhoneVerify pv = (LoginOrRegResult.PhoneVerify) msg.obj;
				if (pv.isResult()) {
					reg_check.setTag("正在验证");
					dialogUtil.showSetPicToast(RegisterActivity.this, "发送成功，请注意查收");
					ThreadExecutor.execute(timeRunable);
				} else {
					dialogUtil.showSetPicToast(RegisterActivity.this, "失败：手机号已经注册");
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(RegisterActivity.this, "发送验证码失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(RegisterActivity.this, "发送验证码失败，请稍后重试");
				break;
			}

		};
	};

	private Handler phoneRegHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.Reg reg = (LoginOrRegResult.Reg) msg.obj;
				if (reg.isResult()) {
					dbAccount.setToken(reg.getPrivate_token());
					accountDAO.save(dbAccount);
					setResult(RESULT_OK);
					dialogUtil.showSetPicToast(RegisterActivity.this, "成功");
					finish();
				} else {
					dialogUtil.showSetPicToast(RegisterActivity.this, reg.getMessage());
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(RegisterActivity.this, "注册失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(RegisterActivity.this, "注册失败，请稍后重试");
				break;
			}

		};
	};

	private Handler timerHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 112:
				reg_check.setText("验证");
				reg_check.setTag("未验证");
				break;
			default:
				reg_check.setText(msg.what + "s...");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			timerHandler.sendEmptyMessage(112);
		}
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.reg_login:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.reg_check:
			String phone = reg_phone.getText().toString();
			if (TextUtils.isEmpty(phone) || phone.length() != 11) {
				dialogUtil.showSetPicToast(this, "号码为空或长度不对");
			} else {
				if (reg_check.getTag().equals("未验证")) {
					dialogUtil.showDownloadDialog(this, "正在发送");
					reg_check.setTag("正在验证");
					LoginOrReg.PhoneVerify ver = new LoginOrReg.PhoneVerify(phone);
					ThreadExecutor.execute(new PostData(this, phoneVerifyHandler, ver, 3));
				}

			}
			break;
		case R.id.reg_reg:
			phone = reg_phone.getText().toString();
			String username = reg_username.getText().toString();
			String password = reg_password.getText().toString();
			String rePassword = reg_repassword.getText().toString();
			String verify = reg_checknum.getText().toString();
			if (TextUtils.isEmpty(username)) {
				dialogUtil.showSetPicToast(this, "用户名不能为空");
			} else if (TextUtils.isEmpty(password)) {
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
				LoginOrReg.Reg reg = new LoginOrReg.Reg(username, password, rePassword, phone, verify);
				dialogUtil.showDownloadDialog(this, "正在注册");
				ThreadExecutor.execute(new PostData(this, phoneRegHandler, reg, 4));
				dbAccount.setBind_qq(false);
				dbAccount.setBind_weibo(false);
				dbAccount.setPhone(phone);
				dbAccount.setUsername(username);
				dbAccount.setFace("");
			}
			break;
		default:
			break;
		}
	}

}
