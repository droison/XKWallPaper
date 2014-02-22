package com.xkwallpaper.baidumtj;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class BaiduMTJActivity extends Activity {

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
		setResult(RESULT_CANCELED);
		finish();
	}
}
