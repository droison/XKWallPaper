package com.xkwallpaper.lockpaper;

import cn.sharesdk.framework.ShareSDK;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.lockpaper.TouchLayout.SlidingCompleteListener;
import com.xkwallpaper.ui.R;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.Window;
import android.widget.RelativeLayout;

public class LockActivity extends BaiduMTJActivity {
	private TouchLayout touchLayout;
	private RelativeLayout lock_linearlayout;
	float temp = 0;

	private static final boolean DBG = true;
	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	private static final String TAG = "MainActivity";
	public static StatusViewManager mStatusViewManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_lockpaper);

		mStatusViewManager = new StatusViewManager(this, this.getApplicationContext());

		lock_linearlayout = (RelativeLayout) this.findViewById(R.id.lock_linearlayout);
		touchLayout = (TouchLayout) this.findViewById(R.id.lock_touchtest);

		SharedPreferences lockpaper = getSharedPreferences("lockpaper", 0);
		String paperPath = lockpaper.getString("paper_path", "");
		int paper_Id = lockpaper.getInt("paper_id", 0);
		String paper_thumb = lockpaper.getString("paper_thumb", "");
		Drawable dw = Drawable.createFromPath(paperPath);
		if (dw != null && paper_Id != 0 && !paperPath.equals("") && lockpaper.getBoolean("is_create", false)) {
			Paper paper = new Paper();
			paper.setId(paper_Id);
			paper.setMphoto(paper_thumb);
			lock_linearlayout.setBackgroundDrawable(dw);
			touchLayout.setBackgroundDrawable(dw);
			touchLayout.setPaper(paper);
		} else {
			finish();
		}

		touchLayout.setSlidingCompleteListener(new SlidingCompleteListener() {

			@Override
			public void onCall() {
				finish();
			}
		});
		
		ShareSDK.initSDK(this);
		// LockService.startLockService(this);
	}


	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return disableKeycode(keyCode, event);
	}

	private boolean disableKeycode(int keyCode, KeyEvent event) {
		int key = event.getKeyCode();
		switch (key) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ShareSDK.stopSDK(this);
		if (DBG)
			Log.d(TAG, "onDestroy()");

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DBG)
			Log.d(TAG, "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		if (DBG)
			Log.d(TAG, "onDetachedFromWindow()");
	}

}
