package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainTabFragment extends BaiduMTJFragment {

	private int fragmentNum;
	private View view;
	private MainActivity parentActivity = null;

	/*
	 * 选项卡部分
	 */
	private RelativeLayout card_info_choose_history;
	private RelativeLayout card_info_curr;
	private RelativeLayout card_info_phone;
	private RelativeLayout card_info_map;
	private TextView card_info_text1;
	private TextView card_info_text2;
	private TextView card_info_text3;
	private TextView card_info_text4;
	private ImageView card_info_line;
	private ImageView card_info_curr_line;
	private ImageView card_info_phone_line;
	private ImageView card_info_map_line;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		fragmentNum = args.getInt("fragmentNum");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.maintab_fragment, null);
		setUpView();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActivity() != null && getActivity() instanceof MainActivity) {
			parentActivity = (MainActivity) getActivity();
		}
	}

	private void setUpView() {
		card_info_choose_history = (RelativeLayout) view.findViewById(R.id.card_info_choose_history);
		card_info_curr = (RelativeLayout) view.findViewById(R.id.card_info_curr);
		card_info_phone = (RelativeLayout) view.findViewById(R.id.card_info_phone);
		card_info_map = (RelativeLayout) view.findViewById(R.id.card_info_map);
		card_info_text1 = (TextView) view.findViewById(R.id.card_info_text1);
		card_info_text2 = (TextView) view.findViewById(R.id.card_info_text2);
		card_info_text3 = (TextView) view.findViewById(R.id.card_info_text3);
		card_info_text4 = (TextView) view.findViewById(R.id.card_info_text4);

		card_info_line = (ImageView) view.findViewById(R.id.card_info_line);
		card_info_curr_line = (ImageView) view.findViewById(R.id.card_info_curr_line);
		card_info_phone_line = (ImageView) view.findViewById(R.id.card_info_phone_line);
		card_info_map_line = (ImageView) view.findViewById(R.id.card_info_map_line);

		card_info_choose_history.setOnClickListener(new CheckListener());
		card_info_curr.setOnClickListener(new CheckListener());
		card_info_phone.setOnClickListener(new CheckListener());
		card_info_map.setOnClickListener(new CheckListener());
	}

	public void switchTab(int viewId) {
		switch (viewId) {
		case R.id.iv_tab_pic:
			card_info_text1.setTextColor(Color.BLACK);
			card_info_text2.setTextColor(getResources().getColor(R.color.grey));
			card_info_text3.setTextColor(getResources().getColor(R.color.grey));
			card_info_text4.setTextColor(getResources().getColor(R.color.grey));
			card_info_line.setVisibility(View.VISIBLE);
			card_info_curr_line.setVisibility(View.GONE);
			card_info_phone_line.setVisibility(View.GONE);
			card_info_map_line.setVisibility(View.GONE);

			break;
		case R.id.iv_tab_lockpaper:
			card_info_text1.setTextColor(getResources().getColor(R.color.grey));
			card_info_text2.setTextColor(Color.BLACK);
			card_info_text3.setTextColor(getResources().getColor(R.color.grey));
			card_info_text4.setTextColor(getResources().getColor(R.color.grey));
			card_info_line.setVisibility(View.GONE);
			card_info_curr_line.setVisibility(View.VISIBLE);
			card_info_phone_line.setVisibility(View.GONE);
			card_info_map_line.setVisibility(View.GONE);

			break;
		case R.id.iv_tab_video:
			card_info_text1.setTextColor(getResources().getColor(R.color.grey));
			card_info_text2.setTextColor(getResources().getColor(R.color.grey));
			card_info_text3.setTextColor(Color.BLACK);
			card_info_text4.setTextColor(getResources().getColor(R.color.grey));
			card_info_line.setVisibility(View.GONE);
			card_info_curr_line.setVisibility(View.GONE);
			card_info_phone_line.setVisibility(View.VISIBLE);
			card_info_map_line.setVisibility(View.GONE);

			break;
		case R.id.iv_tab_my:
			card_info_text1.setTextColor(getResources().getColor(R.color.grey));
			card_info_text2.setTextColor(getResources().getColor(R.color.grey));
			card_info_text3.setTextColor(getResources().getColor(R.color.grey));
			card_info_text4.setTextColor(Color.BLACK);
			card_info_line.setVisibility(View.GONE);
			card_info_curr_line.setVisibility(View.GONE);
			card_info_phone_line.setVisibility(View.GONE);
			card_info_map_line.setVisibility(View.VISIBLE);
			break;
		}
	}

	private class CheckListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {

			case R.id.card_info_choose_history:

				switchTab(R.id.iv_tab_pic);
				parentActivity.setFragmentNum(R.id.iv_tab_pic);

				PicFragment toPic = new PicFragment();
				Bundle bundle = new Bundle();
				bundle.putString("dir", "pic");
				toPic.setArguments(bundle);
				parentActivity.switchContent(toPic,2);

				break;
			case R.id.card_info_curr:
				switchTab(R.id.iv_tab_lockpaper);
				parentActivity.setFragmentNum(R.id.iv_tab_lockpaper);

				PicFragment toLock = new PicFragment();
				Bundle bundle1 = new Bundle();
				bundle1.putString("dir", "lock");
				toLock.setArguments(bundle1);
				parentActivity.switchContent(toLock,3);

				break;
			case R.id.card_info_phone:
				switchTab(R.id.iv_tab_video);
				parentActivity.setFragmentNum(R.id.iv_tab_video);
				PicFragment toVideo = new PicFragment();
				Bundle bundle2 = new Bundle();
				bundle2.putString("dir", "vid");
				toVideo.setArguments(bundle2);
				parentActivity.switchContent(toVideo,4);

				break;
			case R.id.card_info_map:
				switchTab(R.id.iv_tab_my);
				parentActivity.setFragmentNum(R.id.iv_tab_my);
				parentActivity.switchContent(new MyFragment(), 5);
				break;
			default:
				break;
			}
		}
	};

}
