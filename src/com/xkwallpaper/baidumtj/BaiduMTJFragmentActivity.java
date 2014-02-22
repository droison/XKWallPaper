package com.xkwallpaper.baidumtj;

import com.baidu.mobstat.StatService;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class BaiduMTJFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	public void finishView(View view) {
		finish();
	}
}
