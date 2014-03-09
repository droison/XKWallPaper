package com.xkwallpaper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.sharesdk.framework.ShareSDK;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.xkwallpaper.lockpaper.LockService;
import com.xkwallpaper.ui.fragment.MainTabFragment;
import com.xkwallpaper.ui.fragment.MenuFragment2;
import com.xkwallpaper.ui.fragment.PicFragment;
import com.xkwallpaper.ui.fragment.SearchFragment;
import com.xkwallpaper.ui.fragment.TitleBarFragment;
import com.xkwallpaper.updateapp.CheckVersionService;
import com.xkwallpaper.updateapp.UpdateHandler;

public class MainActivity extends SlidingFragmentActivity {
	private TitleBarFragment mTitleBar;
	private Fragment mContent;
	private MainTabFragment maintabFragment;
	private MenuFragment2 menuFragment;
	private FragmentManager fm;
	private ImageView start_bg;
	private int fragmentNum = R.id.iv_tab_pic; // 四个值：R.id.ll_tab_pic;R.id.ll_tab_lockpaper;R.id.ll_tab_video;R.id.ll_tab_my;
	private boolean isMainTab = true;
	private boolean isMenuOpen = false;
	private boolean isSearch = false;

	public boolean isMainTab() {
		return isMainTab;
	}

	public void setMainTab(boolean isMainTab) {
		this.isMainTab = isMainTab;
	}

	public int getFragmentNum() {
		return fragmentNum;
	}

	public void setFragmentNum(int fragmentNum) {
		this.fragmentNum = fragmentNum;
	}

	private FrameLayout tab_frame;
	private SlidingMenu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.responsive_content_frame);
		start_bg = (ImageView) this.findViewById(R.id.start_bg);
		Handler startHandler = new Handler();
		startHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				start_bg.setBackgroundDrawable(null);
			}
		}, 3000);

		mTitleBar = new TitleBarFragment();
		maintabFragment = new MainTabFragment();
		menuFragment = new MenuFragment2();
		tab_frame = (FrameLayout) this.findViewById(R.id.tab_frame);
		fm = getSupportFragmentManager();

		getSupportFragmentManager().beginTransaction().replace(R.id.titlebar_frame, mTitleBar).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.tab_frame, maintabFragment).commit();
		initSlidingMenu(savedInstanceState);

		SharedPreferences lockpaper = getSharedPreferences("lockpaper", 0);
		if (lockpaper.getBoolean("is_create", false)) {
			LockService.startLockService(this);
		}

		UpdateHandler uhandler = new UpdateHandler(this);
		new Thread(new CheckVersionService(this, uhandler)).start();

		ShareSDK.initSDK(this);
	}

	private void initSlidingMenu(Bundle savedInstanceState) {
		// check if the content frame contains the menu frame
		menu = getSlidingMenu();

		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);

			menu.setMode(SlidingMenu.LEFT);
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenW = dm.widthPixels;// 获取分辨率宽度
			menu.setFadeDegree(0.35f);
			menu.setBehindOffset(screenW / 5); // 设置多宽

			menu.setSlidingEnabled(true);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setTouchmodeMarginThreshold(50);
			// show home as up so we can toggle
		}
		// else {
		// // add a dummy view
		// View v = new View(this);
		// setBehindContentView(v);
		// menu.setSlidingEnabled(false);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// }

		menu.setOnOpenListener(new OnOpenListener() {

			@Override
			public void onOpen() {
				isMenuOpen = true;
				if (isMainTab) {
					menuFragment.switchImageView((ImageView) MainActivity.this.findViewById(fragmentNum));
				}
				menuFragment.setUserInfo();
			}
		});
		menu.setOnCloseListener(new OnCloseListener() {

			@Override
			public void onClose() {
				isMenuOpen = false;
				if (isMainTab) {
					tab_frame.setVisibility(View.VISIBLE);
					maintabFragment.switchTab(fragmentNum);
				} else {
					tab_frame.setVisibility(View.GONE);
				}
			}
		});

		mContent = new PicFragment();
		Bundle bundle = new Bundle();
		bundle.putString("dir", "pic");
		mContent.setArguments(bundle);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menuFragment).commit();

		// menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		// menu.setBehindScrollScale(0.25f);
		// menu.setFadeDegree(0.25f);
	}

	public void switchContent(final Fragment fragmentContent, int fragmentTitle) {
		mTitleBar.setTitleText(fragmentTitle);
		getSlidingMenu().showContent();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				if (fragmentContent != null) {
					mContent = fragmentContent;
					fm.beginTransaction().replace(R.id.content_frame, fragmentContent).commit();
				}
			}
		}, 100);
		if (fragmentTitle <= 5 && fragmentTitle >= 2) {
			tab_frame.setVisibility(View.VISIBLE);
		}else{
			tab_frame.setVisibility(View.GONE);
		}
	}

	public void switchSearchContent(SearchFragment searchFragment) {
		if (searchFragment != null) {
			mContent = searchFragment;
			fm.beginTransaction().replace(R.id.content_frame, searchFragment).commit();
		}
		isSearch = searchFragment.getArguments().getBoolean("isSearch", false);
		tab_frame.setVisibility(View.GONE);
		mTitleBar.setTitleText(1);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}

	public void setTabView(boolean isVisable) {

		if (isVisable && !isMainTab) {
			isMainTab = isVisable;

		} else if (!isVisable && isMainTab) {
			isMainTab = isVisable;

		}
	}

	public void onBirdPressed(int pos) {
		Intent intent = new Intent(this, PicViewActivity.class);
		intent.putExtra("pos", pos);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// getSupportFragmentManager().putFragment(outState, "mContent",
		// mContent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监听返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isSearch) {
				SearchFragment sf = new SearchFragment();
				sf.setArguments(new Bundle());
				switchSearchContent(sf);
			} else {
				AlertDialog qdialog = new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_info).setTitle("确认退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						fm.beginTransaction().remove(mContent).commit();
						finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create();
				qdialog.show();
			}
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}
}
