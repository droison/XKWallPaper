package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuFragment2 extends BaiduMTJFragment implements OnClickListener {

	private LinearLayout ll_tab_search, ll_tab_pic, ll_tab_lockpaper, ll_tab_my, ll_tab_video, ll_tab_set, ll_tab_about;
	private ImageView iv_tab_search, iv_tab_pic, iv_tab_lockpaper, iv_tab_my, iv_tab_video, iv_tab_set, iv_tab_about;
	private View parent;
	private MainActivity parentActivity = null;
	private int fragmentNum;
	private RelativeLayout menu_login;
	private ImageView menu_head;
	private TextView menu_name;
	private AccountDAO accountDAO;
	private DbAccount account;
	private AsyncImageLoader imageLoader;

	// 启动fragment第一方法
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	// 启动fragment第二方法
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActivity() != null && getActivity() instanceof MainActivity) {
			parentActivity = (MainActivity) getActivity();
		}
	}

	// 启动fragment第三方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.menu_fragment, null);
		setUpView();
		setListener();
		return parent;
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity ra = (MainActivity) getActivity();
			ra.switchContent(fragment, 1);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void setListener() {
		ll_tab_search.setOnClickListener(this);
		ll_tab_pic.setOnClickListener(this);
		ll_tab_lockpaper.setOnClickListener(this);
		ll_tab_my.setOnClickListener(this);
		ll_tab_set.setOnClickListener(this);
		ll_tab_about.setOnClickListener(this);
		ll_tab_video.setOnClickListener(this);
		menu_login.setOnClickListener(this);
	}

	private void setUpView() {
		// parent = getActivity();
		ll_tab_search = (LinearLayout) parent.findViewById(R.id.ll_tab_search);
		ll_tab_pic = (LinearLayout) parent.findViewById(R.id.ll_tab_pic);
		ll_tab_lockpaper = (LinearLayout) parent.findViewById(R.id.ll_tab_lockpaper);
		ll_tab_my = (LinearLayout) parent.findViewById(R.id.ll_tab_my);
		ll_tab_video = (LinearLayout) parent.findViewById(R.id.ll_tab_video);
		ll_tab_about = (LinearLayout) parent.findViewById(R.id.ll_tab_about);
		ll_tab_set = (LinearLayout) parent.findViewById(R.id.ll_tab_set);
		iv_tab_search = (ImageView) parent.findViewById(R.id.iv_tab_search);
		iv_tab_pic = (ImageView) parent.findViewById(R.id.iv_tab_pic);
		iv_tab_lockpaper = (ImageView) parent.findViewById(R.id.iv_tab_lockpaper);
		iv_tab_my = (ImageView) parent.findViewById(R.id.iv_tab_my);
		iv_tab_video = (ImageView) parent.findViewById(R.id.iv_tab_video);
		iv_tab_set = (ImageView) parent.findViewById(R.id.iv_tab_set);
		iv_tab_about = (ImageView) parent.findViewById(R.id.iv_tab_about);

		menu_login = (RelativeLayout) parent.findViewById(R.id.menu_login);
		menu_head = (ImageView) parent.findViewById(R.id.menu_head);
		menu_name = (TextView) parent.findViewById(R.id.menu_name);
		accountDAO = new AccountDAO(parentActivity);
	}

	public void switchImageView(ImageView iv) {
		iv_tab_search.setPressed(false);
		iv_tab_pic.setPressed(false);
		iv_tab_lockpaper.setPressed(false);
		iv_tab_my.setPressed(false);
		iv_tab_video.setPressed(false);
		iv_tab_set.setPressed(false);
		iv_tab_about.setPressed(false);
		iv.setPressed(true);
	}

	@Override
	public void onClick(View arg0) {

		if (parentActivity == null) {
			return;
		}
		((InputMethodManager) parentActivity.getSystemService(parentActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		switch (arg0.getId()) {
		case R.id.ll_tab_search:
			switchImageView(iv_tab_search);
			parentActivity.switchContent(new SearchFragment(), 1);
			parentActivity.setMainTab(false);
			break;
		case R.id.ll_tab_pic:
			switchImageView(iv_tab_pic);
			parentActivity.setFragmentNum(R.id.iv_tab_pic);
			parentActivity.setMainTab(true);
			PicFragment toPic = new PicFragment();
			Bundle bundle = new Bundle();
			bundle.putString("dir", "pic");
			toPic.setArguments(bundle);
			parentActivity.switchContent(toPic, 2);
			break;
		case R.id.ll_tab_lockpaper:
			switchImageView(iv_tab_lockpaper);
			parentActivity.setFragmentNum(R.id.iv_tab_lockpaper);
			parentActivity.setMainTab(true);
			PicFragment toLock = new PicFragment();
			Bundle bundle1 = new Bundle();
			bundle1.putString("dir", "lock");
			toLock.setArguments(bundle1);
			parentActivity.switchContent(toLock, 3);

			break;
		case R.id.ll_tab_my:
			switchImageView(iv_tab_my);
			parentActivity.setFragmentNum(R.id.iv_tab_my);
			parentActivity.setMainTab(true);
			parentActivity.switchContent(new MyFragment(), 5);

			break;
		case R.id.ll_tab_video:
			switchImageView(iv_tab_video);
			parentActivity.setFragmentNum(R.id.iv_tab_video);
			parentActivity.setMainTab(true);
			PicFragment toVideo = new PicFragment();
			Bundle bundle2 = new Bundle();
			bundle2.putString("dir", "vid");
			toVideo.setArguments(bundle2);
			parentActivity.switchContent(toVideo, 4);

			break;
		case R.id.ll_tab_set:
			switchImageView(iv_tab_set);
			parentActivity.setMainTab(false);

			SetFragment toSet = new SetFragment();
			Bundle bundle3 = new Bundle();
			bundle3.putSerializable("account", account);
			toSet.setArguments(bundle3);

			parentActivity.switchContent(toSet, 6);
			break;
		case R.id.ll_tab_about:
			switchImageView(iv_tab_about);
			parentActivity.setMainTab(false);
			parentActivity.switchContent(new AboutFragment(), 7);
			break;
		case R.id.menu_login:
			if (account == null) {
				Intent toLogin = new Intent(parentActivity, LoginActivity.class);
				startActivityForResult(toLogin, 12);
			} else {
				switchImageView(iv_tab_set);
				parentActivity.setMainTab(false);
				parentActivity.switchContent(new SetFragment(), 6);
			}
			break;
		}
	}

	public void setUserInfo() {
		account = accountDAO.getAccount();
		if (account != null) {
			if (imageLoader == null)
				imageLoader = new AsyncImageLoader();
			menu_name.setText(account.getUsername());
			imageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + account.getFace(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					menu_head.setImageBitmap(bm);
				}
			}, "icon", "head");
		} else {
			menu_name.setText("点此登录");
			menu_head.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == parentActivity.RESULT_CANCELED)
			return;
		else if (resultCode == parentActivity.RESULT_OK) {
			setUserInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
