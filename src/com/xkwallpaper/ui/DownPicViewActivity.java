/**
 * 此处为我的下载页面点击图片、锁屏后跳转的页面，仅仅用于展示已经下载好的图片
 */
package com.xkwallpaper.ui;

import java.io.File;
import java.io.IOException;

import cn.sharesdk.framework.ShareSDK;

import com.xkwallpaper.baidumtj.BaiduMTJFragmentActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.CollectDAO;
import com.xkwallpaper.db.DbPaper;
import com.xkwallpaper.db.DownloadDAO;
import com.xkwallpaper.http.ShareTask;
import com.xkwallpaper.imagezoom.ImageViewTouch;
import com.xkwallpaper.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import com.xkwallpaper.lockpaper.LockService;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.ImageUtil;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * 图片显示原则：
 * JPG
 * 对于大的图片   —— 首先显示为自适应     双击放大scale 1.5  ——双击恢复
 * 对于小的图片   —— 首先显示为1.0       双击放大为自适应   ——双击恢复
 * GIF
 * 扩展到屏幕大小
 */
public class DownPicViewActivity extends BaiduMTJFragmentActivity {

	private Bitmap bitmap;
	private ImageViewTouch ivt;
	private ImageView down_tab_delete, down_tab_setpaper, down_tab_collect, down_tab_share;

	private LinearLayout down_bottom_layout;
	private Bundle bundle;
	private DbPaper paper;
	private boolean isShow = true;
	private DownloadDAO downloadDAO;
	private CollectDAO collectDAO;
	private String path;
	private DialogUtil dialogUtil;
	private SharedPreferences lockpaper;
	private SharedPreferences.Editor lockpaperEdit;
	ShareTask shareTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_down_picpreview);
		initData();
		ShareSDK.initSDK(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
		ivt.clear();
	}

	private void initData() {
		bundle = getIntent().getExtras();
		paper = (DbPaper) bundle.getSerializable("paper");

		ivt = (ImageViewTouch) findViewById(R.id.down_image);
		down_bottom_layout = (LinearLayout) findViewById(R.id.down_bottom_layout);
		down_tab_delete = (ImageView) findViewById(R.id.down_tab_delete);
		down_tab_setpaper = (ImageView) findViewById(R.id.down_tab_setpaper);
		down_tab_collect = (ImageView) findViewById(R.id.down_tab_collect);
		down_tab_share = (ImageView) findViewById(R.id.down_tab_share);
		if (paper.getDir().equals("lock"))
			down_tab_setpaper.setImageResource(R.drawable.bottom_tab_setlock);
		downloadDAO = new DownloadDAO(this);
		collectDAO = new CollectDAO(this);
		dialogUtil = new DialogUtil();
		lockpaper = getSharedPreferences("lockpaper", 0);
		lockpaperEdit = lockpaper.edit();
		if (collectDAO.isExist(paper.getId() + "")) {
			down_tab_collect.setTag("已收藏");
			down_tab_collect.setImageResource(R.drawable.bottom_tab_collected);
		} else {
			down_tab_collect.setTag("未收藏");
		}

		down_tab_delete.setOnClickListener(bottomTabListener);
		down_tab_setpaper.setOnClickListener(bottomTabListener);
		down_tab_collect.setOnClickListener(bottomTabListener);
		down_tab_share.setOnClickListener(bottomTabListener);
		ivt.setSingleTapListener(OIV);
		path = AppConstants.APP_FILE_PATH + "/download/" + paper.getId() + ".jpg";
		Handler hander = new Handler();
		hander.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setUpImage(path);
			}
		}, 500);
		
		
		shareTask = new ShareTask((Activity)this, paper, paper.getDir());
	}

	OnClickListener bottomTabListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.down_tab_delete:
				downloadDAO.delete(paper.getId() + "");
				(new File(path)).delete();
				finish();
				break;
			case R.id.down_tab_setpaper:
				if (paper.getDir().equals("lock")) {
					String filePath = AppConstants.Lock_Paper_Path;
					float w = DownPicViewActivity.this.getResources().getDisplayMetrics().widthPixels;
					float h = DownPicViewActivity.this.getResources().getDisplayMetrics().heightPixels;
					ImageUtil.bitMapCut(path, filePath, h / w);
					lockpaperEdit.putBoolean("is_create", true);
					lockpaperEdit.putString("paper_path", filePath);
					lockpaperEdit.putInt("paper_id", paper.getId());
					lockpaperEdit.commit();
					LockService.startLockService(DownPicViewActivity.this);
					dialogUtil.showSetPicToast(DownPicViewActivity.this, "设置成功!");
				} else {
					WallpaperManager wallpaperManager = WallpaperManager.getInstance(DownPicViewActivity.this);
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					try {
						wallpaperManager.setBitmap(bitmap);
						dialogUtil.showSetPicToast(DownPicViewActivity.this, "设置成功!");
					} catch (IOException e) {
						e.printStackTrace();
						dialogUtil.showSetPicToast(DownPicViewActivity.this, "设置失败，请稍后重试");
					}
					bitmap.recycle();
				}
				break;
			case R.id.down_tab_collect:
				if (down_tab_collect.getTag().equals("已收藏"))
					return;
				collectDAO.save(paper, paper.getDir());
				down_tab_collect.setImageResource(R.drawable.bottom_tab_collected);
				down_tab_collect.setTag("已收藏");
				break;
			case R.id.down_tab_share:
				shareTask.execute();
				break;
			}
		}
	};

	OnImageViewTouchSingleTapListener OIV = new OnImageViewTouchSingleTapListener() {

		@Override
		public void onSingleTapConfirmed() {
			if (isShow) {
				Animation hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_hide);
				hide.setFillAfter(true);
				down_bottom_layout.startAnimation(hide);
				isShow = false;
			} else {
				Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_show);
				show.setFillAfter(true);
				down_bottom_layout.startAnimation(show);
				isShow = true;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void setUpImage(String path) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels; // 屏幕宽度
		int h = dm.heightPixels; // 屏幕高度

		ImageUtil.touchImageCal(path, ivt, h, w);
	}

}
