/**
 * 我的页面中，我的下载fragment
 */
package com.xkwallpaper.ui.fragment;

import java.util.List;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.DbPaper;
import com.xkwallpaper.db.DownloadDAO;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.ui.DownPicViewActivity;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.VideoPlayActivity;
import com.xkwallpaper.util.DpSpDip2Px;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

public class MyDownloadFragment extends BaiduMTJFragment {

	private Activity parentActivity;
	private View root;
	private LinearLayout my_collect_linearLayout1, my_collect_linearLayout2;
	private DownloadDAO downloadDAO;
	private AsyncImageLoader imageLoader;
	private DpSpDip2Px dp2px;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.my_collect_fragment, null);
		setUpView();
//		initData();
		return root;
	}

	private void setUpView() {
		parentActivity = getActivity();
		if (getActivity() instanceof MainActivity) {
			parentActivity = (MainActivity) getActivity();
		}
		my_collect_linearLayout1 = (LinearLayout) root.findViewById(R.id.my_collect_linearLayout1);
		my_collect_linearLayout2 = (LinearLayout) root.findViewById(R.id.my_collect_linearLayout2);
		downloadDAO = new DownloadDAO(parentActivity);
		imageLoader = new AsyncImageLoader();
		dp2px = new DpSpDip2Px(parentActivity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {

		setUpAdapter();
	}

	private void setUpAdapter() {
		my_collect_linearLayout1.removeAllViews();
		my_collect_linearLayout2.removeAllViews();
		DownloadAdapter adapter = new DownloadAdapter();
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

	class DownloadAdapter extends BaseAdapter {

		List<DbPaper> downloads;
		LayoutInflater mInflater;

		public DownloadAdapter() {
			this.downloads = downloadDAO.getAll();
			mInflater = LayoutInflater.from(parentActivity);
		}

		@Override
		public int getCount() {
			return downloads.size();
		}

		@Override
		public Object getItem(int arg0) {
			return downloads.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {

			final DbPaper paper = downloads.get(arg0);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_grid_item, null);
				final ImageView img = (ImageView) convertView.findViewById(R.id.my_grid_item_img);
				RelativeLayout my_grid_root_relativelayout = (RelativeLayout) convertView.findViewById(R.id.my_grid_root_relativelayout);
				imageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + paper.getSphoto(), new ImageCallback() {

					@Override
					public void imageLoaded(Bitmap bm, String imageUrl) {
						if (bm != null){
							img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dp2px.getPicThumbHigh()));
							img.setImageBitmap(bm);
						}
					}
				}, paper.getDir(), paper.getId() + ".thumb");

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

				// 此处添加缩略图,可能需要压缩
				// img.setImageBitmap(ImageUtil.getSmallBitmap(AppConstants.APP_FILE_PATH
				// + "/download/" + paper.getId() + ".jpg"));
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (paper.getDir().equals("vid")) {
						Intent toVideoPlay = new Intent(parentActivity, VideoPlayActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("paper", paper);
						toVideoPlay.putExtras(bundle);
						parentActivity.startActivity(toVideoPlay);
					} else {
						Intent toDownView = new Intent(parentActivity, DownPicViewActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("paper", paper);
						toDownView.putExtras(bundle);
						parentActivity.startActivity(toDownView);
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
					downloadDAO.delete(paper.getId() + "");
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
