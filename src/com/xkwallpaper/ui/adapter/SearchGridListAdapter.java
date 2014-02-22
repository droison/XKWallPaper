package com.xkwallpaper.ui.adapter;

import java.util.List;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.DownloadDAO;
import com.xkwallpaper.db.PraiseDAO;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.DownloadTask;
import com.xkwallpaper.http.DownloadTask.DownCompleteCallBack;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.http.base.PostPraiseBase;
import com.xkwallpaper.http.base.PraiseResult;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.PicInfoActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.VideoInfoActivity;
import com.xkwallpaper.ui.fragment.SearchFragment;
import com.xkwallpaper.util.DpSpDip2Px;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchGridListAdapter extends BaseAdapter {

	private List<Paper> list;

	private LayoutInflater mInflater;

	private MainActivity mActivity = null;

	private AsyncImageLoader imageLoader;
	private PraiseDAO praiseDAO;
	private DownloadDAO downloadDAO;
	private AccountDAO accountDAO;
	private DbAccount account;
	private DpSpDip2Px dp2px;

	public SearchGridListAdapter(List<Paper> list, MainActivity mActivity) {
		super();
		this.list = list;
		this.mActivity = mActivity;
		mInflater = LayoutInflater.from(mActivity);
		praiseDAO = new PraiseDAO(mActivity);
		downloadDAO = new DownloadDAO(mActivity);
		accountDAO = new AccountDAO(mActivity);
		dp2px = new DpSpDip2Px(mActivity);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = new ViewHolder();

		final Paper paper = list.get(position);
		String temp = "";
		if (paper.getStyle() == 1) {
			temp = "pic";
		} else if (paper.getStyle() == 2) {
			temp = "lock";
		} else if (paper.getStyle() == 3) {
			temp = "vid";
		}
		final String dir = temp;

		convertView = mInflater.inflate(R.layout.main_grid_item, null);
		final RelativeLayout main_grid_root_relativelayout = (RelativeLayout) convertView.findViewById(R.id.main_grid_root_relativelayout);

		viewHolder.img = (ImageView) convertView.findViewById(R.id.grid_item_img);
		viewHolder.tags = (TextView) convertView.findViewById(R.id.main_grid_item_tags);
		viewHolder.down = (ImageButton) convertView.findViewById(R.id.main_grid_item_downbtn);
		viewHolder.praise = (ImageButton) convertView.findViewById(R.id.main_grid_item_praisebtn);
		viewHolder.downNum = (TextView) convertView.findViewById(R.id.grid_item_text2);
		viewHolder.praiseNum = (TextView) convertView.findViewById(R.id.grid_item_text1);

		viewHolder.tags.setText(tagsToString(paper.getTags()));
		viewHolder.tags.setMovementMethod(LinkMovementMethod.getInstance());
		viewHolder.downNum.setText(paper.getDownload() + "");
		viewHolder.praiseNum.setText(paper.getPraise() + "");
		
		final ImageView iconbtn = new ImageView(mActivity);
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(40, 40);
		rllp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.grid_item_img);
		rllp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.grid_item_img);
		iconbtn.setLayoutParams(rllp);
		if (paper.getStyle() == 3) {
			iconbtn.setImageResource(R.drawable.icon_vid);
		} else if (paper.getStyle() == 1) {
			iconbtn.setImageResource(R.drawable.icon_pic);
		} else {
			iconbtn.setImageResource(R.drawable.icon_lock);
		}

		final Handler praiseHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case AppConstants.HANDLER_MESSAGE_NORMAL:

					PraiseResult pr = (PraiseResult) msg.obj;
					if (pr.isResult()) {
						viewHolder.praise.setTag("已赞");
						viewHolder.praise.setImageResource(R.drawable.main_praised);
						paper.setPraise(pr.getPraise_num());
						viewHolder.praiseNum.setText(String.valueOf(paper.getPraise()));
						praiseDAO.save(paper);
					} else {
						viewHolder.praise.setTag("未赞");
					}
					break;
				case AppConstants.HANDLER_MESSAGE_NONETWORK:
					viewHolder.praise.setTag("未赞");
					break;
				case AppConstants.HANDLER_HTTPSTATUS_ERROR:
					viewHolder.praise.setTag("未赞");
					break;
				}
			};
		};

		if (!downloadDAO.isExist(paper.getId() + "")) { // 表示尚未下载
			viewHolder.down.setTag("未下载");
			viewHolder.down.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (arg0.getTag().equals("已下载")) {
						return;
					}
					DownloadTask downTask = new DownloadTask(mActivity, dir, paper, new DownCompleteCallBack() {

						@Override
						public void call(boolean isSuccess) {
							if (isSuccess) {
								viewHolder.down.setImageResource(R.drawable.main_downloaded);
								paper.setDownload(paper.getDownload() + 1);
								viewHolder.downNum.setText(String.valueOf(paper.getDownload()));
								viewHolder.down.getTag().equals("已下载");
							}
						}

					});
					downTask.execute();
				}
			});
		} else { // 已经下载
			viewHolder.down.setTag("已下载");
			viewHolder.down.setImageResource(R.drawable.main_downloaded);
		}

		if (!praiseDAO.isExist(paper.getId() + "")) { // 表示尚未下载
			viewHolder.praise.setTag("未赞");
			viewHolder.praise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (arg0.getTag().equals("已赞")) {
						return;
					} 
					account = accountDAO.getAccount();
					if(account == null){
						Intent toLogin = new Intent(mActivity, LoginActivity.class);
						mActivity.startActivity(toLogin);
					}else {
						viewHolder.praise.setTag("已赞");
						PostPraiseBase praiseBase = new PostPraiseBase();
						praiseBase.setPaper_id(paper.getId());
						praiseBase.setPrivate_token(account.getToken());
						ThreadExecutor.execute(new PostData(mActivity, praiseHandler, praiseBase,1));
					}
				}
			});
		} else { // 已经下载
			viewHolder.praise.setTag("已赞");
			viewHolder.praise.setImageResource(R.drawable.main_praised);
		}

		if (imageLoader == null) {
			imageLoader = new AsyncImageLoader();
		}

		// 处理下载图片了
		if (paper.getSphoto() != null) {
			imageLoader.loadDrawable(mActivity, AppConstants.HTTPURL.serverIP + paper.getSphoto(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					if (bm != null)
					{
						viewHolder.img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dp2px.getPicThumbHigh()));
						viewHolder.img.setImageBitmap(bm);
					}
					main_grid_root_relativelayout.addView(iconbtn);
				}
			}, dir, paper.getId() + ".thumb");
		}

		convertView.setTag(viewHolder);

		viewHolder.img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dir.equals("vid")) {
					Intent toVidInfoActivity = new Intent(mActivity, VideoInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("paper", paper);
					bundle.putString("dir", dir);
					toVidInfoActivity.putExtras(bundle);
					mActivity.startActivity(toVidInfoActivity);
				} else {
					Intent toPicInfoActivity = new Intent(mActivity, PicInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("paper", paper);
					bundle.putString("dir", dir);
					toPicInfoActivity.putExtras(bundle);
					mActivity.startActivity(toPicInfoActivity);
				}

			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView img;
		TextView tags;
		ImageButton down;
		ImageButton praise;
		TextView downNum;
		TextView praiseNum;

	}


	class MyURLSpan extends ClickableSpan {
		private String mUrl;

		/**
		 * 构造器
		 * 
		 * @param url
		 *            可以点击的关键字，构造时传入的
		 */
		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#000000")); // 设置超链接颜色
			ds.setUnderlineText(false); // 超链接去掉下划线
		}

		@Override
		public void onClick(View widget) {
			SearchFragment searchFragment = new SearchFragment();
			Bundle b = new Bundle();
			b.putBoolean("isSearch", true);
			b.putString("keyword", mUrl);
			b.putInt("search_type", 2);
			b.putInt("paper_type", 0);
			searchFragment.setArguments(b);
			mActivity.switchSearchContent(searchFragment);
		}
	}

	private SpannableStringBuilder tagsToString(List<String> tags) {
		String result = "";
		for (int i = 0; i < tags.size(); i++) {
			result += tags.get(i);
			if (i + 1 != tags.size()) {
				result += "、";
			}
		}
		SpannableStringBuilder style = new SpannableStringBuilder(result);

		int start = 0;
		for (int i = 0; i < tags.size(); i++) {
			style.setSpan(new MyURLSpan(tags.get(i)), start, start + tags.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			start += tags.get(i).length() + 1;
		}
		return style;
	}

}
