package com.xkwallpaper.ui.fragment;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TitleBarFragment extends BaiduMTJFragment implements OnClickListener {

	private ImageView title_menu;
	private ImageView title_name_image;
	private View view;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.title_fragment, null);
		setUpView();
		return view;
	}

	private void setUpView() {
		title_menu = (ImageView) view.findViewById(R.id.title_menu);
		title_name_image = (ImageView) view.findViewById(R.id.title_name_image);
		title_menu.setOnClickListener(this);
	}
	
	//1是搜索 2是壁纸 3是锁屏 4是视频 5是我的 6是设置 7是关于
	public void setTitleText(int fragmentNum){
		switch (fragmentNum) {
		case 1:
			title_name_image.setImageResource(R.drawable.title_sousuo);
			break;
		case 6:
			title_name_image.setImageResource(R.drawable.title_shezhi);
			break;
		case 7:
			title_name_image.setImageResource(R.drawable.title_guanyu);
			break;
		default:
			title_name_image.setImageResource(R.drawable.title_xingku);
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_menu:
			if (getActivity() != null && getActivity() instanceof SlidingFragmentActivity) {
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.toggle();
			}
			break;

		default:
			break;
		}
	}

}
