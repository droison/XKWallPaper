package com.xkwallpaper.ui.fragment;

import java.util.List;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.CollectDAO;
import com.xkwallpaper.db.DbPaper;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.PicInfoActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.VideoInfoActivity;
import com.xkwallpaper.util.DpSpDip2Px;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

public class MyCollectFragment extends BaiduMTJFragment {

	private Activity parentActivity;
	private View root;
	private LinearLayout my_collect_linearLayout1, my_collect_linearLayout2;
	private DpSpDip2Px dp2px;
	private AsyncImageLoader imageLoader;

	private CollectDAO collectDAO;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.my_collect_fragment, null);
		setUpView();
		initData();
		return root;
	}

	private void setUpView() {
		parentActivity = getActivity();
		if (getActivity() instanceof MainActivity) {
			parentActivity = (MainActivity) getActivity();
		}
		my_collect_linearLayout1 = (LinearLayout) root.findViewById(R.id.my_collect_linearLayout1);
		my_collect_linearLayout2 = (LinearLayout) root.findViewById(R.id.my_collect_linearLayout2);
		dp2px = new DpSpDip2Px(parentActivity);
	}

	private void initData() {
		collectDAO = new CollectDAO(parentActivity);
		setUpAdapter();
	}

	private void setUpAdapter() {
		my_collect_linearLayout1.removeAllViews();
		my_collect_linearLayout2.removeAllViews();
		CollectAdapter adapter = new CollectAdapter();
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, dp2px.dip2px(3), 0, 0);
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = adapter.getDropDownView(i, null, null);
			v.setLayoutParams(lp);
			if (i % 2 == 0) {
				my_collect_linearLayout1.addView(v);
			} else {
				my_collect_linearLayout2.addView(v);
			}
		}
	}

	class CollectAdapter extends BaseAdapter {

		List<DbPaper> collects;
		LayoutInflater mInflater;

		public CollectAdapter() {
			this.collects = collectDAO.getAll();
			mInflater = LayoutInflater.from(parentActivity);
		}

		@Override
		public int getCount() {
			return collects.size();
		}

		@Override
		public Object getItem(int arg0) {
			return collects.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			final DbPaper paper = collects.get(arg0);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_grid_item, null);
				final ImageView img = (ImageView) convertView.findViewById(R.id.my_grid_item_img);
				RelativeLayout my_grid_root_relativelayout = (RelativeLayout) convertView.findViewById(R.id.my_grid_root_relativelayout);
				if (imageLoader == null) {
					imageLoader = new AsyncImageLoader();
				}

				// 处理下载图片了
				if (paper.getSphoto() != null) {
					imageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + paper.getSphoto(), new ImageCallback() {

						@Override
						public void imageLoaded(Bitmap bm, String imageUrl) {
							if (bm != null){
								img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dp2px.getPicThumbHigh()));
								img.setImageBitmap(bm);
							}
								
						}
					}, paper.getDir(), paper.getId() + ".thumb");
				}

				ImageView iconbtn = new ImageView(parentActivity);
				RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(40, 40);
				rllp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.my_grid_item_img);
				rllp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.my_grid_item_img);
				iconbtn.setLayoutParams(rllp);
				if (paper.getDir().equals("vid")) {
					iconbtn.setImageResource(R.drawable.icon_vid);
				} else if (paper.getDir().equals("pic")) {
					iconbtn.setImageResource(R.drawable.icon_pic);
				} else {
					iconbtn.setImageResource(R.drawable.icon_lock);
				}
				my_grid_root_relativelayout.addView(iconbtn);

			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (paper.getDir().equals("vid")) {
						Intent toVidInfoActivity = new Intent(parentActivity, VideoInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("paper", paper);
						bundle.putString("dir", paper.getDir());
						toVidInfoActivity.putExtras(bundle);
						parentActivity.startActivity(toVidInfoActivity);
					} else {
						Intent toPicInfoActivity = new Intent(parentActivity, PicInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("paper", paper);
						bundle.putString("dir", paper.getDir());

						toPicInfoActivity.putExtras(bundle);
						parentActivity.startActivity(toPicInfoActivity);
					}
				}
			});

			convertView.setLongClickable(true);
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					showDeleteAlert(paper);
					return true;
				}
			});
			return convertView;
		}

		private void showDeleteAlert(final DbPaper paper) {
			final AlertDialog dlg = new AlertDialog.Builder(parentActivity).create();
			dlg.show();
			Window window = dlg.getWindow();
			// *** 主要就是在这里实现这种效果的.
			// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
			window.setContentView(R.layout.dialog_delete);
			// 为确认按钮添加事件,执行退出应用操作
			Button ok = (Button) window.findViewById(R.id.btn_ok);
			ok.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					collectDAO.delete(paper.getId() + "");
					setUpAdapter();
					dlg.cancel();
				}
			});
			// 关闭alert对话框架
			Button cancel = (Button) window.findViewById(R.id.btn_cancel);
			cancel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dlg.cancel();
				}
			});
		}
	}

}