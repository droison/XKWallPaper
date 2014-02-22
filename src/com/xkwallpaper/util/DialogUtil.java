package com.xkwallpaper.util;

import com.xkwallpaper.ui.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DialogUtil {

	private ProgressDialog progressDialog;
	private Dialog downloadDialog;

	public void showProgressDialog(Context mContext, CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setIndeterminate(true);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:

					return true;
				}
				return false;
			}
		});
	}

	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	public void showDownloadDialog(Activity mActivity, String message) {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View v = inflater.inflate(R.layout.dialog_download, null);// 得到加载view
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dialog_progress);
		TextView tipTextView = (TextView) v.findViewById(R.id.dialog_tips);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.download_progress);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(message);// 设置加载信息

		downloadDialog = new Dialog(mActivity, R.style.download_dialog);// 创建自定义样式dialog

		downloadDialog.setCancelable(false);// 不可以用“返回键”取消
		downloadDialog.setContentView(v);// 设置布局
		downloadDialog.show();
	}

	public void dismissDownloadDialog() {
		if (downloadDialog != null) {
			downloadDialog.dismiss();
		}
	}

	public void showNoNetWork(Activity activity) {
		View view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.offline, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
		Toast toast = new Toast(activity);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(view);
		toast.show();
	}

	public void showSetPicToast(Activity activity, String message) {
		View view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.toast_set_pic, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) view.findViewById(R.id.toast_text);
		text.setText(message);

		Toast toast = new Toast(activity);
		// 设置Toast的位置
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		// 让Toast显示为我们自定义的样子
		toast.setView(view);
		toast.show();
	}
}
