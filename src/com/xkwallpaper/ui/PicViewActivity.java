/**
 * 壁纸、锁屏的预览页面
 */
package com.xkwallpaper.ui;

import cn.sharesdk.framework.ShareSDK;

import com.xkwallpaper.baidumtj.BaiduMTJFragmentActivity;
import com.xkwallpaper.http.PreviewTask;
import com.xkwallpaper.http.PreviewTask.PreviewCallBack;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.imagezoom.ImageViewTouch;
import com.xkwallpaper.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import com.xkwallpaper.ui.fragment.BottomTabFragment;
import com.xkwallpaper.util.ImageUtil;

import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
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
public class PicViewActivity extends BaiduMTJFragmentActivity {

	public static final String TAG = "下载图片";
	private String SPpath;
	private SeekBar bar;
	private ImageView iv;
	private Bitmap bitmap;
	private ImageViewTouch ivt;
	private boolean isShow;

	private FrameLayout pic_preview_bottom_frame;
	private Bundle bundle;
	private Paper paper;
	private String dir;
	private PreviewTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_picpreview);
		initData();
		ShareSDK.initSDK(this);
	}

	private void initData() {
		bundle = getIntent().getExtras();
		dir = bundle.getString("dir");
		paper = (Paper) bundle.getSerializable("paper");
		isShow = true;

		SPpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pic1.jpg"; // 放置之前下载好的缩略图位置

		bar = (SeekBar) findViewById(R.id.progressBar1);
		iv = (ImageView) findViewById(R.id.imageView1);
		ivt = (ImageViewTouch) findViewById(R.id.image);

		pic_preview_bottom_frame = (FrameLayout) this.findViewById(R.id.pic_preview_bottom_frame);

		iv.setOnTouchListener(OTL);
		ivt.setSingleTapListener(OIV);
		Log.e("TAG", SPpath + " ");
		bitmap = BitmapFactory.decodeFile(SPpath);
		if (bitmap != null)
			iv.setImageBitmap(bitmap);

		task = new PreviewTask(this, bar, paper, dir, new PreviewCallBack() {

			@Override
			public void onTaskCancle() {
				bar.setProgress(0);
				iv.setImageResource(R.drawable.color_blue);
				ivt.setImageResource(R.drawable.color_blue);
			}

			@Override
			public void onDownComplete(boolean isSuccess, String path) {
				if (isSuccess) {

					Log.i("iv", "jpg here" + path);

					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int w = dm.widthPixels; // 屏幕宽度
					int h = dm.heightPixels; // 屏幕高度
					ImageUtil.touchImageCal(path, ivt, h, w);

					iv.setVisibility(View.GONE);
					pic_preview_bottom_frame.setVisibility(View.VISIBLE);

					BottomTabFragment bottomTabFragment = new BottomTabFragment();
					bottomTabFragment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.pic_preview_bottom_frame, bottomTabFragment).commit();

					bar.setVisibility(View.GONE);
				}
			}
		});
		task.execute();
	}

	OnTouchListener OTL = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				if (isShow) {
					Animation hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_hide);
					hide.setFillAfter(true);
					// title.startAnimation(hide);
					isShow = false;
				} else {
					Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_show);
					show.setFillAfter(true);
					// title.startAnimation(show);
					isShow = true;
				}
			}
			return false;
		}
	};

	OnImageViewTouchSingleTapListener OIV = new OnImageViewTouchSingleTapListener() {

		@Override
		public void onSingleTapConfirmed() {
			if (isShow) {
				Animation hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_hide);
				hide.setFillAfter(true);

				isShow = false;
			} else {
				Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pic_menu_show);
				show.setFillAfter(true);

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
		if (task != null)
			task.cancel(true);

		if (iv != null)
			iv.setImageResource(R.drawable.color_blue);
		if (ivt != null)
			ivt.setImageResource(R.drawable.color_blue);
	}

}
