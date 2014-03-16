package com.xkwallpaper.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.xkwallpaper.lockpaper.LockActivity;
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
	BroadcastReceiver myBroadcastReceivernew = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String homeKey = intent.getExtras().get("HomeKey").toString();
			Log.i("MainActivity", homeKey);
		}
	};

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
		initSlidingMenu(savedInstanceState);

		Set<String> tests = getIntent().getCategories();
		if (tests.contains("android.intent.category.LAUNCHER")) {
			setUpView();
		} else if (tests.contains("android.intent.category.HOME")) {
			ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
			check: if (!name.equals(LockActivity.class.getName())) {
				Intent mIntent = new Intent(Intent.ACTION_MAIN);
				mIntent.addCategory(Intent.CATEGORY_HOME);
				PackageManager pm = getPackageManager();
				List<ResolveInfo> ris = pm.queryIntentActivities(mIntent, 0);
				ResolveInfo xkri = null;
				for (ResolveInfo ri : ris) {
					SharedPreferences choosehome = getSharedPreferences("choosehome", 0);
					String choosehomepackagename = choosehome.getString("packagename", "-1");
					if (ri.activityInfo.packageName.equals(choosehomepackagename)) {
						Intent intent = pm.getLaunchIntentForPackage(choosehomepackagename);
						startActivity(intent);
						break check;
					}
					if (ri.activityInfo.packageName.equals(getApplication().getPackageName())) {
						xkri = ri;
					}
				}
				ris.remove(xkri);
				if (ris.size() == 1) {
					ResolveInfo res = ris.get(0); // 该应用的包名和主Activity
					String pkg = res.activityInfo.packageName;
					String cls = res.activityInfo.name;
					ComponentName componet = new ComponentName(pkg, cls);
					Intent i = new Intent();
					i.setComponent(componet);
					startActivity(i);
				} else if (ris.size() > 1) {
					Intent toChooseHome = new Intent(this, ChooseHomeActivity.class);
					startActivity(toChooseHome);
				}
			}
			finish();
		}
		// android.intent.category.LAUNCHER
	}

	private void setUpView() {

		start_bg = (ImageView) this.findViewById(R.id.start_bg);
		Handler startHandler = new Handler();
		startHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				start_bg.setBackgroundDrawable(null);
			}
		}, 3000);

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
		menuFragment = new MenuFragment2();
		mContent = new PicFragment();
		Bundle bundle = new Bundle();
		bundle.putString("dir", "pic");
		mContent.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menuFragment).commit();

		mTitleBar = new TitleBarFragment();
		maintabFragment = new MainTabFragment();

		tab_frame = (FrameLayout) this.findViewById(R.id.tab_frame);
		fm = getSupportFragmentManager();

		getSupportFragmentManager().beginTransaction().replace(R.id.titlebar_frame, mTitleBar).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.tab_frame, maintabFragment).commit();

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
		} else {
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
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
