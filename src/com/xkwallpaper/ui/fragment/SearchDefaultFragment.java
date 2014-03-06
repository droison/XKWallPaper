package com.xkwallpaper.ui.fragment;

import java.util.List;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.DbSearch;
import com.xkwallpaper.db.SearchDAO;
import com.xkwallpaper.http.GetList;
import com.xkwallpaper.http.base.Tag;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.DpSpDip2Px;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchDefaultFragment extends BaiduMTJFragment implements OnClickListener {

	private LinearLayout search_LinearLayout1,search_LinearLayout2;
	private View root;
	private MainActivity parentActivity;
	private DpSpDip2Px dsd2p;

	private SensorManager sensorManager;
	private Vibrator vibrator;

	private static final String TAG = "TestSensorActivity";
	private static final int SENSOR_SHAKE = 10;
	private DialogUtil dialogUtil;
	private SearchDAO searchDAO;
	private TextView search_text2;
	private boolean isTagUpdate = false;
	private int[] tag_bg = {R.drawable.tag_bg1,R.drawable.tag_bg2,R.drawable.tag_bg3,R.drawable.tag_bg4,R.drawable.tag_bg5};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		parentActivity = (MainActivity) getActivity();
		// 以下两行为传感器重力加速度使用
		sensorManager = (SensorManager) parentActivity.getSystemService(parentActivity.SENSOR_SERVICE);
		vibrator = (Vibrator) parentActivity.getSystemService(parentActivity.VIBRATOR_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.search_fragment_default, null);
		search_LinearLayout1 = (LinearLayout) root.findViewById(R.id.search_LinearLayout1);
		search_LinearLayout2 = (LinearLayout) root.findViewById(R.id.search_LinearLayout2);
		search_text2 = (TextView) root.findViewById(R.id.search_text2);
		dsd2p = new DpSpDip2Px(parentActivity);
		dialogUtil = new DialogUtil();
		searchDAO = new SearchDAO(parentActivity);

		ThreadExecutor.execute(new GetList(parentActivity, tagsHandler, AppConstants.HTTPURL.searchTags, 6));
		isTagUpdate = true;
		List<DbSearch> searchs = searchDAO.getAll();
		if(searchs!= null &&searchs.size()!=0){
			search_text2.setVisibility(View.VISIBLE);
			setUpHistoryTags(searchs);
		}
		
		
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}

	// 不显示了
	@Override
	public void onPause() {
		super.onPause();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
		    Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" +z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 16;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
								Message msg = new Message();
				msg.what = SENSOR_SHAKE;
				handler.sendMessage(msg);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	Handler tagsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			isTagUpdate = false;
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Tag> tags = (List<Tag>) msg.obj;
				if (tags != null && tags.size() != 0)
					setUpTags(tags);
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showNoNetWork(parentActivity);
				break;
			}
		}

	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				if(!isTagUpdate){
					isTagUpdate = true;
					vibrator.vibrate(400);
					ThreadExecutor.execute(new GetList(parentActivity, tagsHandler, AppConstants.HTTPURL.searchTags, 6));
				}
				break;
			}
		}

	};

	private void setUpTags(List<Tag> tags) {
		search_LinearLayout1.removeAllViews();
		int maxWidth = dsd2p.getScreenDpWidth() - 5;
		int len = tags.size();

		for (int i = 0; i < len;) {
			LinearLayout ll1 = new LinearLayout(parentActivity);
			ll1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ll1.setOrientation(LinearLayout.HORIZONTAL);
			int currWidth = 0;
			while (i < len) {
				currWidth += getTagWidth(tags.get(i).getTitle());
				if (currWidth <= maxWidth) {
					TextView textView = new TextView(parentActivity);
					LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp1.setMargins(10, 5, 10, 25);
					textView.setText(tags.get(i).getTitle());
					textView.setTextAppearance(parentActivity, R.style.tag_textview);
					textView.setPadding(dsd2p.dip2px(3), 0, dsd2p.dip2px(3), 0);
					textView.setBackgroundResource(tag_bg[i%5]);
					textView.setGravity(Gravity.CENTER);
					textView.setLayoutParams(lp1);
					textView.setOnClickListener(this);
					textView.setMaxLines(1);
					textView.setTag(2);
					ll1.addView(textView);
					i++;
				} else {
					break;
				}

			}
			search_LinearLayout1.addView(ll1);
		}
	}

	private void setUpHistoryTags(List<DbSearch> searchs) {
		int maxWidth = dsd2p.getScreenDpWidth() - 5;
		int len = searchs.size();
		search_LinearLayout2.removeAllViews();
		for (int i = 0; i < len;) {
			LinearLayout ll1 = new LinearLayout(parentActivity);
			ll1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ll1.setOrientation(LinearLayout.HORIZONTAL);
			int currWidth = 0;
			while (i < len) {
				currWidth += getTagWidth(searchs.get(i).getKey());
				if (currWidth <= maxWidth) {
					TextView textView = new TextView(parentActivity);
					LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp1.setMargins(10, 5, 10, 25);
					textView.setText(searchs.get(i).getKey());
					textView.setTextAppearance(parentActivity, R.style.tag_textview);
					textView.setPadding(dsd2p.dip2px(3), 0, dsd2p.dip2px(3), 0);
					textView.setBackgroundResource(R.drawable.ab_item_bg);
					textView.setLayoutParams(lp1);
					textView.setGravity(Gravity.CENTER);
					textView.setMaxLines(1);
					textView.setOnClickListener(this);
					textView.setTag(searchs.get(i).getType());
					ll1.addView(textView);
					i++;
				} else {
					break;
				}

			}
			search_LinearLayout2.addView(ll1);
		}
	}
	
	private int getTagWidth(String tagContent) {
		int tx1Width = 6 + dsd2p.px2dip(20) + tagContent.length() * dsd2p.sp2dp(16) + 3;
		return tx1Width;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 instanceof TextView) {
			String key = ((TextView) arg0).getText().toString();
			int type = (Integer) arg0.getTag();
			
			SearchFragment searchFragment = new SearchFragment();
			Bundle b = new Bundle();
			b.putBoolean("isSearch", true);
			b.putString("keyword", key);
			b.putInt("search_type", type);
			b.putInt("paper_type", 0);
			searchFragment.setArguments(b);
			parentActivity.switchSearchContent(searchFragment);
		}
	}
}
