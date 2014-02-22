package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.GetList;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.PicInfoActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.VideoInfoActivity;
import com.xkwallpaper.ui.adapter.PicGridAdapter;
import com.xkwallpaper.ui.component.PullToRefreshView;
import com.xkwallpaper.util.DialogUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PicFragment extends BaiduMTJFragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

	private ViewPager viewPager;
	private List<ImageView> imageViews;

	private List<View> dots;

	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;

	private PullToRefreshView root;
	private MainActivity parentActivity;

	private boolean isGridRefresh = false;
	private boolean isGridLoadMore = false;

	private boolean isCompleteView = false;

	private String serverUrl;
	private DialogUtil dialogUtil;
	private LinearLayout pic_linearLayout1;
	private LinearLayout pic_linearLayout2;
	private String pptUrl;
	private AsyncImageLoader asynImageLoader;
	private String dir;

	private PicGridAdapter picGridAdapter;
	private int page = 1;
	private int type = 1; // 1为壁纸，2为锁屏，3为视频

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};

	public void setArguments(Bundle args) {
		this.dir = args.getString("dir");
		if (dir.equals("pic")) {
			serverUrl = AppConstants.HTTPURL.picAll;
			pptUrl = AppConstants.HTTPURL.picPPT;
			type = 1;
		} else if (dir.equals("lock")) {
			serverUrl = AppConstants.HTTPURL.lockAll;
			pptUrl = AppConstants.HTTPURL.lockPPT;
			type = 2;
		} else {
			serverUrl = AppConstants.HTTPURL.vidAll;
			pptUrl = AppConstants.HTTPURL.vidPPT;
			type = 3;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = (PullToRefreshView) inflater.inflate(R.layout.pic_fragment, null);
		setUpView();
		setUpListener();
		if (!isCompleteView)
			initData();
		return root;
	}

	private void initData() {

		ThreadExecutor.execute(new GetList(parentActivity, pptHandler, pptUrl, 5));
		ThreadExecutor.execute(new GetList(parentActivity, picListHandler, serverUrl + page, type));
		dialogUtil.showProgressDialog(parentActivity, "正在更新数据...");
		isGridRefresh = true;

		isCompleteView = true;

	}

	Handler pptHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Paper> paperList = (List<Paper>) msg.obj;

				if (paperList != null && paperList.size() == 3) {
					imageViews = new ArrayList<ImageView>();

					for (int i = 0; i < paperList.size(); i++) {
						final Paper thisPPTpaper = paperList.get(i);
						final ImageView imageView = new ImageView(parentActivity);
						asynImageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + thisPPTpaper.getPphoto(), new ImageCallback() {

							@Override
							public void imageLoaded(Bitmap bm, String imageUrl) {
								imageView.setImageBitmap(bm);
							}
						}, dir, paperList.get(i).getId() + ".ppt");
						imageView.setScaleType(ScaleType.CENTER_CROP);
						imageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (dir.equals("vid")) {
									Intent toVidInfoActivity = new Intent(parentActivity, VideoInfoActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("paper", thisPPTpaper);
									bundle.putString("dir", dir);
									toVidInfoActivity.putExtras(bundle);
									parentActivity.startActivity(toVidInfoActivity);
								} else {
									Intent toPicInfoActivity = new Intent(parentActivity, PicInfoActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("paper", thisPPTpaper);
									bundle.putString("dir", dir);
									toPicInfoActivity.putExtras(bundle);
									parentActivity.startActivity(toPicInfoActivity);
								}

							}
						});
						imageViews.add(imageView);
					}
					dots = new ArrayList<View>();
					dots.add(root.findViewById(R.id.v_dot0));
					dots.add(root.findViewById(R.id.v_dot1));
					dots.add(root.findViewById(R.id.v_dot2));

					viewPager.setAdapter(new MyAdapter());

					scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
					scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
				}
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(parentActivity, "ppt访问出错", 1).show();
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				break;
			}
		};
	};

	public void setUpView() {

		if (getActivity() != null && getActivity() instanceof MainActivity)
			parentActivity = (MainActivity) getActivity();

		if (parentActivity == null)
			return;

		viewPager = (ViewPager) root.findViewById(R.id.vp);
		dialogUtil = new DialogUtil();
		asynImageLoader = new AsyncImageLoader();
		pic_linearLayout1 = (LinearLayout) root.findViewById(R.id.pic_linearLayout1);
		pic_linearLayout2 = (LinearLayout) root.findViewById(R.id.pic_linearLayout2);
		picGridAdapter = new PicGridAdapter(pic_linearLayout1, pic_linearLayout2, parentActivity, dir);

	}

	private void setUpListener() {
		root.setOnHeaderRefreshListener(this);
		root.setOnFooterRefreshListener(this);

		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		if (scheduledExecutorService != null)
			scheduledExecutorService.shutdown();
		super.onDestroy();
	}

	private Handler picListHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			completeGridRefresh();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Paper> paperList = (List<Paper>) msg.obj;
				if (page == 1) {
					picGridAdapter.removeAll();
				}
				picGridAdapter.addView(paperList);
				if (paperList.size() == 0) {
					root.onFooterShow("已到最后了", false);
					Toast.makeText(parentActivity, "已到最后了", 1).show();
				}
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(parentActivity, "网络访问出错", 1).show();
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				dialogUtil.showNoNetWork(parentActivity);
				break;
			}
		};
	};

	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;

		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		isGridLoadMore = true;
		page++;
		ThreadExecutor.execute(new GetList(parentActivity, picListHandler, serverUrl + page, type));
		// getServiceRun(currentPageNo);
		System.out.println("onFooterRefresh");
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		isGridRefresh = true;
		page = 1;
		ThreadExecutor.execute(new GetList(parentActivity, picListHandler, serverUrl + page, type));
		System.out.println("onHeaderRefresh");
	}

	// 此处用户完成“刷新”和“更多”后的头部和尾部的UI变化
	void completeGridRefresh() {

		if (isGridRefresh) {
			root.onHeaderRefreshComplete("上次刷新时间是:" + new Date().toLocaleString());
			root.onHeaderRefreshComplete();
			isGridRefresh = false;
		}
		if (isGridLoadMore) {
			root.onFooterRefreshComplete();
			isGridLoadMore = false;
		}

	}

}