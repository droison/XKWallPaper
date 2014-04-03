/***
 * 关于界面的fragment
 * 
 */
package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.ui.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends BaiduMTJFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.content_about, null);
	}

}
