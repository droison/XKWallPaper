/**
 * 登录页面
 */
package com.xkwallpaper.ui;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.base.LoginOrReg;
import com.xkwallpaper.http.base.LoginOrRegResult;
import com.xkwallpaper.http.base.SocialBase;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.util.DialogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends BaiduMTJActivity implements OnClickListener {

	private EditText login_phone, login_password;
	private TextView login_lostpass, login_login, login_register, login_weibo, login_weixin, login_qq;
	private DialogUtil dialogUtil;
	private AccountDAO accountDAO;
	private static final int REGISTERCODE = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ShareSDK.initSDK(this);
		initData();
		setUpView();

	}

	// 初始化view并初始化fragment
	private void setUpView() {
		login_phone = (EditText) this.findViewById(R.id.login_phone);
		login_password = (EditText) this.findViewById(R.id.login_password);
		login_lostpass = (TextView) this.findViewById(R.id.login_lostpass);
		login_login = (TextView) this.findViewById(R.id.login_login);
		login_qq = (TextView) this.findViewById(R.id.login_qq);
		login_register = (TextView) this.findViewById(R.id.login_register);
		login_weibo = (TextView) this.findViewById(R.id.login_weibo);
		login_weixin = (TextView) this.findViewById(R.id.login_weixin);
		((ImageView) this.findViewById(R.id.title_one_name_image)).setImageResource(R.drawable.title_denglu);

		login_lostpass.setOnClickListener(this);
		login_login.setOnClickListener(this);
		login_register.setOnClickListener(this);
		login_weibo.setOnClickListener(this);
		login_weixin.setOnClickListener(this);
		login_qq.setOnClickListener(this);
		dialogUtil = new DialogUtil();
		accountDAO = new AccountDAO(this);
	}

	private void initData() {
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.login_register:
			Intent toRegister = new Intent(this, RegisterActivity.class);
			startActivityForResult(toRegister, REGISTERCODE);
			break;

		case R.id.login_weibo:
			Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
			if(weibo.isValid()){
				weibo.removeAccount();
			}
			dialogUtil.showDownloadDialog(LoginActivity.this, "正在登录");
			weibo.SSOSetting(true);
			weibo.setPlatformActionListener(new PlatformActionListener() {

				public void onError(Platform platform, int action, Throwable t) {
					// 操作失败的处理代码
					dialogUtil.dismissDownloadDialog();
				}

				public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
					String weiboId = platform.getDb().get("weibo");
					String imageUrl = (String) res.get("profile_image_url");
					String nickname = (String) res.get("screen_name");
					SocialBase sb = new SocialBase();
					sb.setFace(imageUrl);
					sb.setType(1);
					sb.setUid(weiboId);
					sb.setUsername(nickname);
					ThreadExecutor.execute(new PostData(LoginActivity.this, loginHandler, sb, 7));
				}

				public void onCancel(Platform platform, int action) {
					dialogUtil.dismissDownloadDialog();
					// 操作取消的处理代码
				}

			});
			weibo.showUser(null);
			break;
		case R.id.login_qq:
			Platform qq = ShareSDK.getPlatform(this, QZone.NAME);
			if(qq.isValid())
			{
				qq.removeAccount();
			}
			dialogUtil.showDownloadDialog(LoginActivity.this, "正在登录");
			qq.SSOSetting(true);
			qq.setPlatformActionListener(new PlatformActionListener() {

				public void onError(Platform platform, int action, Throwable t) {
					// 操作失败的处理代码
					dialogUtil.dismissDownloadDialog();
				}

				public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
					
					String weiboId = platform.getDb().get("weibo");
					String nickname = (String) res.get("nickname");
					String imageUrl = (String) res.get("figureurl_2");
					SocialBase sb = new SocialBase();
					sb.setFace(imageUrl);
					sb.setType(2);
					sb.setUid(weiboId);
					sb.setUsername(nickname);
					ThreadExecutor.execute(new PostData(LoginActivity.this, loginHandler, sb, 7));
				}

				public void onCancel(Platform platform, int action) {
					// 操作取消的处理代码
					dialogUtil.dismissDownloadDialog();
				}

			});
			qq.showUser(null);
			break;
		case R.id.login_login:
			String phone = login_phone.getText().toString();
			String password = login_password.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				dialogUtil.showSetPicToast(this, "手机号不能为空");
			} else if (TextUtils.isEmpty(password)) {
				dialogUtil.showSetPicToast(this, "密码不能为空");
			} else if (phone.length() != 11) {
				dialogUtil.showSetPicToast(this, "手机号格式错误");
			} else {
				LoginOrReg.Login login = new LoginOrReg.Login(phone, password);
				dialogUtil.showDownloadDialog(this, "正在登录");
				ThreadExecutor.execute(new PostData(this, loginHandler, login, 5));
			}
			break;
		case R.id.login_lostpass:
			Intent toLostPwd = new Intent(LoginActivity.this, LostPwdActivity.class);
			startActivity(toLostPwd);
			break;
		}
	}
	
	private Handler loginHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.Login loginResult= (LoginOrRegResult.Login)msg.obj;
				if(loginResult.isResult()){
					accountDAO.save(loginResult);
					setResult(RESULT_OK);
					finish();
				}else{
					dialogUtil.showSetPicToast(LoginActivity.this, loginResult.getMessage());
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(LoginActivity.this, "登录失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(LoginActivity.this, "登录失败，请稍后重试");
				break;
			}

		};
	};

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED)
			return;
		else if(resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
