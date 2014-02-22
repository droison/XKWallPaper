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
import com.xkwallpaper.ui.adapter.SearchGridAdapter;
import com.xkwallpaper.ui.component.PullToRefreshView;
import com.xkwallpaper.util.DialogUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultFragment extends BaiduMTJFragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

	private PullToRefreshView search_result_pullview;
	private View root;
	private MainActivity parentActivity;

	private boolean isGridRefresh = false;
	private boolean isGridLoadMore = false;

	private String serverUrl;
	private DialogUtil dialogUtil;
	private LinearLayout search_result_linearLayout1, search_result_linearLayout2;
	private AsyncImageLoader asynImageLoader;
	private TextView search_result_all, search_result_pic, search_result_lock, search_result_vid;

	private SearchGridAdapter searchGridAdapter;
	private int page = 1;
	private int type = 1; // 1为壁纸，2为锁屏，3为视频
	private Bundle bundle;
	private String keyword;
	private int search_type;
	private int paper_type;

	public void setArguments(Bundle args) {
		bundle = args;
		keyword = args.getString("keyword");
		search_type = args.getInt("search_type");
		paper_type = args.getInt("paper_type");
		serverUrl = AppConstants.HTTPURL.search + "?keyword=" + keyword + "&search_type=" + search_type + "&paper_type=" + paper_type + "&page=";
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.search_result_fragment, null);
		setUpView();
		setUpListener();
		initData();
		return root;
	}

	private void initData() {
		ThreadExecutor.execute(new GetList(parentActivity, searchResultHandler, serverUrl + page, type));
		dialogUtil.showProgressDialog(parentActivity, "正在搜索...");
		isGridRefresh = true;
	}

	public void setUpView() {

		if (getActivity() != null && getActivity() instanceof MainActivity)
			parentActivity = (MainActivity) getActivity();

		if (parentActivity == null)
			return;

		dialogUtil = new DialogUtil();
		asynImageLoader = new AsyncImageLoader();
		search_result_pullview = (PullToRefreshView) root.findViewById(R.id.search_result_pullview);
		search_result_linearLayout1 = (LinearLayout) root.findViewById(R.id.search_result_linearLayout1);
		search_result_linearLayout2 = (LinearLayout) root.findViewById(R.id.search_result_linearLayout2);
		search_result_all = (TextView) root.findViewById(R.id.search_result_all);
		search_result_pic = (TextView) root.findViewById(R.id.search_result_pic);
		search_result_lock = (TextView) root.findViewById(R.id.search_result_lock);
		search_result_vid = (TextView) root.findViewById(R.id.search_result_vid);
		searchGridAdapter = new SearchGridAdapter(search_result_linearLayout1, search_result_linearLayout2, parentActivity);
		switchTab(paper_type);
	}

	private void switchTab(int arg0) {
		TextView[] tabs = { search_result_all, search_result_pic, search_result_lock, search_result_vid };
		for (int i = 0; i < 4; i++) {
			if (i == arg0) {
				tabs[i].setTextColor(Color.WHITE);
				tabs[i].setBackgroundResource(R.drawable.my_tab_bg_select);
			} else {
				tabs[i].setTextColor(getResources().getColor(R.color.grey));
				tabs[i].setBackgroundDrawable(null);
			}
		}
	}

	private void setUpListener() {
		search_result_pullview.setOnHeaderRefreshListener(this);
		search_result_pullview.setOnFooterRefreshListener(this);
		search_result_all.setOnClickListener(tabClickListener);
		search_result_pic.setOnClickListener(tabClickListener);
		search_result_lock.setOnClickListener(tabClickListener);
		search_result_vid.setOnClickListener(tabClickListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private Handler searchResultHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			completeGridRefresh();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Paper> paperList = (List<Paper>) msg.obj;
				if (page == 1) {
					searchGridAdapter.removeAll();
				}
				searchGridAdapter.addView(paperList);
				if (paperList.size() == 0) {
					search_result_pullview.onFooterShow("已到最后了", false);
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

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		isGridLoadMore = true;
		page++;
		ThreadExecutor.execute(new GetList(parentActivity, searchResultHandler, serverUrl + page, type));
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		isGridRefresh = true;
		page = 1;
		ThreadExecutor.execute(new GetList(parentActivity, searchResultHandler, serverUrl + page, type));
	}

	// 此处用户完成“刷新”和“更多”后的头部和尾部的UI变化
	void completeGridRefresh() {

		if (isGridRefresh) {
			search_result_pullview.onHeaderRefreshComplete("上次刷新时间是:" + new Date().toLocaleString());
			search_result_pullview.onHeaderRefreshComplete();
			isGridRefresh = false;
		}
		if (isGridLoadMore) {
			search_result_pullview.onFooterRefreshComplete();
			isGridLoadMore = false;
		}

	}

	OnClickListener tabClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SearchFragment searchFragment = new SearchFragment();
			switch (arg0.getId()) {
			case R.id.search_result_all:
				bundle.putInt("paper_type", 0);
				break;
			case R.id.search_result_pic:
				bundle.putInt("paper_type", 1);
				break;
			case R.id.search_result_lock:
				bundle.putInt("paper_type", 2);
				break;
			case R.id.search_result_vid:
				bundle.putInt("paper_type", 3);
				break;
			}
			searchFragment.setArguments(bundle);
			parentActivity.switchSearchContent(searchFragment);
		}
	};

}