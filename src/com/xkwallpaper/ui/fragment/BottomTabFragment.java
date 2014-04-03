/*
 * 视频、壁纸、锁屏详情页的底部导航条的逻辑
 */
package com.xkwallpaper.ui.fragment;

import java.io.File;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.db.CollectDAO;
import com.xkwallpaper.http.DownloadTask;
import com.xkwallpaper.http.ShareTask;
import com.xkwallpaper.http.DownloadTask.DownCompleteCallBack;
import com.xkwallpaper.http.SetPicOrLockTask;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.thread.OrderCreateLoader;
import com.xkwallpaper.thread.OrderCreateLoader.OnPayCompleteListener;
import com.xkwallpaper.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BottomTabFragment extends BaiduMTJFragment {

	private Paper paper;
	private String dir; // pic为壁纸，lock锁屏，vid为视频
	private View view;
	private Activity parentActivity = null;

	private CollectDAO collectDAO;
	private boolean isCollect = false;
	private SharedPreferences lockpaper;
	private SharedPreferences.Editor lockpaperEdit;
	private SetPicOrLockTask setPicOrLockTask;
	private OrderCreateLoader orderCreateLoader;
	private DownloadTask downTask;
	
	/*
	 * 选项卡部分
	 */
	private ImageView bottom_tab_download, bottom_tab_setpaper, bottom_tab_collect, bottom_tab_share;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		dir = args.getString("dir");
		paper = (Paper) args.getSerializable("paper");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getActivity() != null) {
			parentActivity = getActivity();
			collectDAO = new CollectDAO(parentActivity);
			isCollect = collectDAO.isExist(paper.getId() + "");
			orderCreateLoader = new OrderCreateLoader();
		}
		view = inflater.inflate(R.layout.bottom_tab_fragment, null);
		setUpView();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void setUpView() {
		bottom_tab_download = (ImageView) view.findViewById(R.id.bottom_tab_download);
		bottom_tab_setpaper = (ImageView) view.findViewById(R.id.bottom_tab_setpaper);
		bottom_tab_collect = (ImageView) view.findViewById(R.id.bottom_tab_collect);
		bottom_tab_share = (ImageView) view.findViewById(R.id.bottom_tab_share);

		if (dir.equals("lock")) {
			bottom_tab_setpaper.setImageResource(R.drawable.bottom_tab_setlock);
		}
		bottom_tab_download.setTag("未下载");
		bottom_tab_download.setOnClickListener(new CheckListener());
		bottom_tab_share.setOnClickListener(new CheckListener());
		if (isCollect) {
			bottom_tab_collect.setImageResource(R.drawable.bottom_tab_collected);
		} else {
			bottom_tab_collect.setOnClickListener(new CheckListener());
		}
		if (!dir.equals("vid")) {
			bottom_tab_setpaper.setOnClickListener(new CheckListener());
		}

		lockpaper = parentActivity.getSharedPreferences("lockpaper", 0);
		lockpaperEdit = lockpaper.edit();
		
		if(dir.equals("vid"))
		{
			RelativeLayout bottom_setlayout = (RelativeLayout) view.findViewById(R.id.bottom_setlayout);
			bottom_setlayout.setVisibility(View.GONE);
		}
	}

	private class CheckListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.bottom_tab_download:
				if (bottom_tab_download.getTag().equals("已下载"))
					return;
				orderCreateLoader.loadOrder(parentActivity, paper, new OnPayCompleteListener() {

					@Override
					public void call(boolean isComplete) {
						if (isComplete) {
							// 1、判断下载目录有没有 2、判断临时的预览页面有没有，有就直接copy过来
						    downTask = new DownloadTask(parentActivity, dir, paper, new DownCompleteCallBack() {
								@Override
								public void call(boolean isSuccess) {
									if (!isSuccess)
										bottom_tab_download.setTag("下载失败");
								}
							});
							downTask.execute();
							bottom_tab_download.setTag("已下载");

						}
					}
				});
				break;
			case R.id.bottom_tab_setpaper:
				orderCreateLoader.loadOrder(parentActivity, paper, new OnPayCompleteListener() {

					@Override
					public void call(boolean isComplete) {
						if (isComplete) {
							// 1、下载，下载到固定目录"id.pre"，但却不是下载目录 2、下载成功返回drawable
							// 3、将drawable处理到固定位置
							if (!dir.equals("vid")) {
								setPicOrLockTask = new SetPicOrLockTask(parentActivity, lockpaperEdit, paper, dir);
								setPicOrLockTask.execute();
							} else {

							}
						}
					}
				});

				break;
			case R.id.bottom_tab_collect:
				if (!isCollect) {
					collectDAO.save(paper, dir);
					isCollect = true;
					bottom_tab_collect.setImageResource(R.drawable.bottom_tab_collected);
				}
				break;
			case R.id.bottom_tab_share:
				ShareTask shareTask = new ShareTask(parentActivity, paper, dir);
				shareTask.execute();
				break;
			}
		}
	}

	public void sharePhoto(String photoUri) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		File file = new File(photoUri);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.setType("image/jpeg");
		parentActivity.startActivity(Intent.createChooser(shareIntent, "分享"));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (setPicOrLockTask != null) {
			setPicOrLockTask.cancel(true);
		}
		if(downTask != null)
			downTask.cancel(true);
	}
}
