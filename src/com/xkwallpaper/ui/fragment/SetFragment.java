package com.xkwallpaper.ui.fragment;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.PutData;
import com.xkwallpaper.http.base.BindBase;
import com.xkwallpaper.http.base.LoginOrRegResult;
import com.xkwallpaper.http.base.LoginOrRegResult.Login;
import com.xkwallpaper.lockpaper.LockService;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DialogUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetFragment extends BaiduMTJFragment implements OnClickListener {

	private View root;
	private RelativeLayout set_closelock, set_startlock, set_headlayout, set_nicklayout, set_phonelayout, set_pwdlayout, set_snslayout;
	private MainActivity parentActivity;
	private SharedPreferences lockpaper;
	private SharedPreferences.Editor editor;
	private ImageView set_headimg;
	private TextView set_nicktext, set_phonetext, set_logout, set_login;
	private LinearLayout set_snsiconlayout, set_loginlayout, set_logoutlayout;
	private DbAccount account;
	private AsyncImageLoader imageLoader;
	private AccountDAO accountDAO;
	private DialogUtil dialogUtil = new DialogUtil();
	private AlertDialog dlg;
	private final int PHOTO_REQUEST_CUT = 13;
	private final int PHOTO_REQUEST_TAKEPHOTO = 14;
	private final int PHOTO_REQUEST_GALLERY = 15;
	private File tempFile = new File(AppConstants.TEMP_HEAD_FILE_PATH);
	private final String TAG = "SetFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.set_fragment, null);
		setUpView();
		setUpListener();
		return root;
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

	private void setUpView() {
		parentActivity = (MainActivity) getActivity();
		set_closelock = (RelativeLayout) root.findViewById(R.id.set_closelock);
		set_startlock = (RelativeLayout) root.findViewById(R.id.set_startlock);

		lockpaper = parentActivity.getSharedPreferences("lockpaper", 0);
		editor = lockpaper.edit();
		if (!lockpaper.getBoolean("is_create", false)) {
			set_closelock.setVisibility(View.GONE);
			String paperPath = lockpaper.getString("paper_path", "");
			int paper_Id = lockpaper.getInt("paper_id", -1);
			if (!"".equals(paperPath) && paper_Id != -1) {
				set_startlock.setVisibility(View.VISIBLE);
			}
		}

		set_headlayout = (RelativeLayout) root.findViewById(R.id.set_headlayout);
		set_nicklayout = (RelativeLayout) root.findViewById(R.id.set_nicklayout);
		set_phonelayout = (RelativeLayout) root.findViewById(R.id.set_phonelayout);
		set_pwdlayout = (RelativeLayout) root.findViewById(R.id.set_pwdlayout);
		set_snslayout = (RelativeLayout) root.findViewById(R.id.set_snslayout);

		set_loginlayout = (LinearLayout) root.findViewById(R.id.set_loginlayout);
		set_logoutlayout = (LinearLayout) root.findViewById(R.id.set_logoutlayout);

		set_snsiconlayout = (LinearLayout) root.findViewById(R.id.set_snsiconlayout);
		set_headimg = (ImageView) root.findViewById(R.id.set_headimg);
		set_nicktext = (TextView) root.findViewById(R.id.set_nicktext);
		set_phonetext = (TextView) root.findViewById(R.id.set_phonetext);
		set_logout = (TextView) root.findViewById(R.id.set_logout);
		set_login = (TextView) root.findViewById(R.id.set_login);

		accountDAO = new AccountDAO(parentActivity);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUserInfo();
	}

	private void setUserInfo() {
		account = accountDAO.getAccount();
		if (account == null) {
			set_loginlayout.setVisibility(View.GONE);
			set_logoutlayout.setVisibility(View.VISIBLE);
		} else {
			set_loginlayout.setVisibility(View.VISIBLE);
			set_logoutlayout.setVisibility(View.GONE);

			if (imageLoader == null)
				imageLoader = new AsyncImageLoader();
			set_snsiconlayout.removeAllViews();
			if (account.isBind_qq()) {
				ImageView qq = new ImageView(parentActivity);
				qq.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				qq.setImageResource(R.drawable.set_qqicon);
				set_snsiconlayout.addView(qq);
			}
			if (account.isBind_weibo()) {
				ImageView weibo = new ImageView(parentActivity);
				weibo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				weibo.setImageResource(R.drawable.set_sinaicon);
				set_snsiconlayout.addView(weibo);
			}

			imageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + account.getFace(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					set_headimg.setImageBitmap(bm);
				}
			}, "icon", "head");

			set_nicktext.setText(account.getUsername());
			set_phonetext.setText(account.getPhone());
		}
	}

	private void setUpListener() {
		set_closelock.setOnClickListener(this);
		set_startlock.setOnClickListener(this);
		set_headlayout.setOnClickListener(this);
		set_nicklayout.setOnClickListener(this);
		set_phonelayout.setOnClickListener(this);
		set_pwdlayout.setOnClickListener(this);
		set_snslayout.setOnClickListener(this);
		set_logout.setOnClickListener(this);
		set_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.set_closelock:
			editor.putBoolean("is_create", false);
			LockService.stopLockService(parentActivity);
			editor.commit();
			set_closelock.setVisibility(View.GONE);
			set_startlock.setVisibility(View.VISIBLE);
			break;
		case R.id.set_startlock:
			editor.putBoolean("is_create", true);
			LockService.startLockService(parentActivity);
			editor.commit();
			set_closelock.setVisibility(View.VISIBLE);
			set_startlock.setVisibility(View.GONE);
			break;
		case R.id.set_headlayout:
			showImgDialog();
			break;
		case R.id.set_nicklayout:
			showInfoUpdateAlert(1);
			break;
		case R.id.set_phonelayout:
			if (account.getPhone() == null || account.getPhone().equals(""))
				showInfoUpdateAlert(2);
			break;
		case R.id.set_pwdlayout:
			showInfoUpdateAlert(3);
			break;
		case R.id.set_snslayout:
			if (!account.isBind_weibo() || !account.isBind_qq())
				showBindAlert(account);
			break;
		case R.id.set_logout:
			accountDAO.delete();
			set_loginlayout.setVisibility(View.GONE);
			set_logoutlayout.setVisibility(View.VISIBLE);
			parentActivity.toggle();
			break;
		case R.id.set_login:
			Intent toLogin = new Intent(parentActivity, LoginActivity.class);
			startActivity(toLogin);
			break;
		default:
			break;
		}
	}

	private Handler bindHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dlg.cancel();
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.Login login = (Login) msg.obj;
				if (!login.isResult()) {
					dialogUtil.showSetPicToast(parentActivity, "失败:" + login.getMessage());
				} else {
					accountDAO.save(login);
					dialogUtil.showSetPicToast(parentActivity, "成功！");
					setUserInfo();
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(parentActivity, "绑定失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(parentActivity, "绑定失败，请稍后重试");
				break;
			}
		};
	};

	private Handler infoUpdateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialogUtil.dismissDownloadDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				LoginOrRegResult.Login login = (Login) msg.obj;
				if (!login.isResult()) {
					dialogUtil.showSetPicToast(parentActivity, "失败:" + login.getMessage());
				} else {
					accountDAO.save(login);
					dialogUtil.showSetPicToast(parentActivity, "成功！");
					setUserInfo();
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showSetPicToast(parentActivity, "修改失败，请稍后重试");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				dialogUtil.showSetPicToast(parentActivity, "修改失败，请稍后重试");
				break;
			case AppConstants.HANDLER_MESSAGE_IMAGE500:
				dialogUtil.showSetPicToast(parentActivity, "成功！");
				setUserInfo();
				break;
			}
		};
	};

	private void showBindAlert(final DbAccount account) {
		dlg = new AlertDialog.Builder(parentActivity).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.dialog_bindsns);

		final BindBase bindBase = new BindBase();
		bindBase.setFace(account.getFace());
		bindBase.setPrivate_token(account.getToken());

		bindBase.setUsername(account.getUsername());

		TextView bind_weibo = (TextView) window.findViewById(R.id.bind_weibo);
		if (account.isBind_weibo()) {
			bind_weibo.setVisibility(View.GONE);
		} else {
			bind_weibo.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						bindBase.setType(1);
						Platform weibo = ShareSDK.getPlatform(parentActivity, SinaWeibo.NAME);
						weibo.SSOSetting(true);
						dialogUtil.showDownloadDialog(parentActivity, "正在绑定");
						if (weibo.isValid()) {
							weibo.removeAccount();
						}

						weibo.setPlatformActionListener(new PlatformActionListener() {

							public void onError(Platform platform, int action, Throwable t) {
								dialogUtil.dismissDownloadDialog();
							}

							public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
								bindBase.setUid(platform.getDb().get("weibo"));
								ThreadExecutor.execute(new PostData(parentActivity, bindHandler, bindBase, 6));
							}

							public void onCancel(Platform platform, int action) {
								dialogUtil.dismissDownloadDialog();
							}

						});
						weibo.showUser(null);
					} catch (Exception e) {
						dialogUtil.showSetPicToast(parentActivity, "绑定异常，请重试");
						Log.e(TAG, "绑定微博异常", e);
					}
				}
			});
		}
		// 为确认按钮添加事件,执行退出应用操作
		TextView bind_qq = (TextView) window.findViewById(R.id.bind_qq);
		if (account.isBind_qq()) {
			bind_qq.setVisibility(View.GONE);
		} else {
			bind_qq.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						bindBase.setType(2);
						Platform qq = ShareSDK.getPlatform(parentActivity, QZone.NAME);
						qq.SSOSetting(true);
						dialogUtil.showDownloadDialog(parentActivity, "正在绑定");
						if (qq.isValid()) {
							qq.removeAccount();
						}
						qq.setPlatformActionListener(new PlatformActionListener() {

							public void onError(Platform platform, int action, Throwable t) {
								dialogUtil.dismissDownloadDialog();
							}

							public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
								bindBase.setUid(platform.getDb().get("weibo"));
								ThreadExecutor.execute(new PostData(parentActivity, bindHandler, bindBase, 6));
							}

							public void onCancel(Platform platform, int action) {
								dialogUtil.dismissDownloadDialog();
							}

						});
						qq.showUser(null);
					} catch (Exception e) {
						dialogUtil.showSetPicToast(parentActivity, "绑定异常，请重试");
						Log.e(TAG, "绑定QQ异常", e);
					}
				}
			});
		}
	}

	// 1是昵称，2是手机，3是密码
	private void showInfoUpdateAlert(final int type) {
		String text = "";
		switch (type) {
		case 1:
			text = "请输入新昵称";
			break;
		case 2:
			text = "请输入新手机号";
			break;
		case 3:
			text = "请输入新密码";
			break;
		}

		LayoutInflater factory = LayoutInflater.from(parentActivity);
		final View textEntryView = factory.inflate(R.layout.dialog_infoupdate, null);
		final EditText infoupdate_content = (EditText) textEntryView.findViewById(R.id.infoupdate_content);
		TextView infoupdate_prompt_text = (TextView) textEntryView.findViewById(R.id.infoupdate_prompt_text);
		infoupdate_content.setHint(text);
		infoupdate_prompt_text.setText(text);
		dlg = new AlertDialog.Builder(parentActivity).setView(textEntryView).setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String str = infoupdate_content.getText().toString();
				if (TextUtils.isEmpty(str)) {
					Toast.makeText(parentActivity, "不能为空", Toast.LENGTH_SHORT).show();
				} else if (type == 2 && str.length() != 11) {
					Toast.makeText(parentActivity, "手机号输入不正确", Toast.LENGTH_SHORT).show();
				} else if (type == 3 && str.length() < 8) {
					Toast.makeText(parentActivity, "密码应当大于8位", Toast.LENGTH_SHORT).show();
				} else {
					dialogUtil.showDownloadDialog(parentActivity, "正在更新");
					ThreadExecutor.execute(new PutData(parentActivity, infoUpdateHandler, str, account.getToken(), type));
				}

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).create();
		dlg.show();

	}

	private void showImgDialog() {
		final String items[] = { "拍照", "相册选择" };
		new AlertDialog.Builder(parentActivity).setTitle("头像设置").setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					dialog.dismiss();
					// 调用系统的拍照功能
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
					startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					break;
				case 1:
					dialog.dismiss();
					Intent i = new Intent(Intent.ACTION_PICK, null);
					i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(i, PHOTO_REQUEST_GALLERY);
				}
			}
		}).show();

	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 114);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 114);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) {
				Bitmap bm = data.getParcelableExtra("data");
				dialogUtil.showDownloadDialog(parentActivity, "正在修改");
				ThreadExecutor.execute(new PutData(parentActivity, infoUpdateHandler, account.getToken(), bm));
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
