package com.xkwallpaper.ui.adapter;

import java.util.List;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.base.Comment;
import com.xkwallpaper.ui.CommentActivity;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private List<Comment> list;

	private LayoutInflater mInflater;

	private Activity mcContext = null;
	private AsyncImageLoader imageLoader;
	private int paper_id;
	private Fragment mFragment;
	private AccountDAO accountDAO;
	private DbAccount account;

	public CommentAdapter(List<Comment> list, Activity activity, int paper_id, Fragment mFragment) {
		super();
		this.list = list;
		this.mcContext = activity;
		this.paper_id = paper_id;
		this.mFragment = mFragment;
		this.accountDAO = new AccountDAO(activity);
		mInflater = LayoutInflater.from(activity);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final Comment comment = list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment_item, null);
			viewHolder = new ViewHolder();

			viewHolder.icon = (ImageView) convertView.findViewById(R.id.comment_icon);
			viewHolder.username = (TextView) convertView.findViewById(R.id.comment_username);
			viewHolder.content = (TextView) convertView.findViewById(R.id.comment_content);

			String reName = comment.getParent_username().length() == 0 ? "" : "  @" + comment.getParent_username();
			if (comment.getContent() != null)
				viewHolder.content.setText(comment.getContent());
			if (comment.getUsername() != null)
				viewHolder.username.setText(comment.getUsername() + reName);
			if (comment.getFace() != null && !comment.getFace().equals("")) {
				if (imageLoader == null) {
					imageLoader = new AsyncImageLoader();
				}
				final ImageView icon = viewHolder.icon;
				imageLoader.loadDrawable(mcContext, AppConstants.HTTPURL.serverIP + comment.getFace(), new ImageCallback() {

					@Override
					public void imageLoaded(Bitmap bm, String imageUrl) {
						icon.setImageBitmap(bm);
					}
				}, "icon", comment.getUsername() + ".png");
			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				account = accountDAO.getAccount();
				if (account == null) {
					Intent toLogin = new Intent(mcContext, LoginActivity.class);
					mcContext.startActivity(toLogin);
				} else {
					Intent toCommentActivity = new Intent(mcContext, CommentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("comment", comment);
					bundle.putInt("paper_id", paper_id);
					bundle.putBoolean("isRe", true);
					bundle.putString("token", account.getToken());
					toCommentActivity.putExtras(bundle);
					mFragment.startActivityForResult(toCommentActivity, AppConstants.Comment_Activity_Code);
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView username;
		TextView content;

	}

}
