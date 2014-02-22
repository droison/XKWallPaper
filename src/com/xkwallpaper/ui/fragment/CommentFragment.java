package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.http.GetList;
import com.xkwallpaper.http.base.Comment;
import com.xkwallpaper.http.base.CommentResult;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.CommentActivity;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.adapter.CommentAdapter;
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
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

	private PullToRefreshView root;
	private Activity parentActivity;
	private TextView comment_count, comment_button;

	private boolean isGridRefresh = false;
	private boolean isGridLoadMore = false;

	private boolean isCompleteView = false;

	private String serverUrl;
	private DialogUtil dialogUtil;
	private LinearLayout comment_linearlayout;

	private int page = 1;
	private Bundle bundle;
	private Paper paper;
	private AccountDAO accountDAO;
	private DbAccount account;

	private CommentAdapter adapter;

	public void setArguments(Bundle args) {
		this.bundle = args;
		this.paper = (Paper) args.getSerializable("paper");
		serverUrl = AppConstants.HTTPURL.picAllComment + "?paper_id=" + paper.getId() + "&page=";
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = (PullToRefreshView) inflater.inflate(R.layout.comment_fragment, null);
		setUpView();
		setUpListener();
		if (!isCompleteView)
			initData();
		return root;
	}

	private void initData() {
		ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
		dialogUtil.showProgressDialog(parentActivity, "正在更新数据...");
		isGridRefresh = true;
		isCompleteView = true;
	}

	public void setUpView() {

		if (getActivity() != null)
			parentActivity = getActivity();
		else
			return;

		dialogUtil = new DialogUtil();
		accountDAO = new AccountDAO(parentActivity);
		comment_count = (TextView) root.findViewById(R.id.comment_count);
		comment_button = (TextView) root.findViewById(R.id.comment_button);
		comment_linearlayout = (LinearLayout) root.findViewById(R.id.comment_linearlayout);
	}

	private void setUpListener() {
		root.setOnHeaderRefreshListener(this);
		root.setOnFooterRefreshListener(this);
		comment_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				account = accountDAO.getAccount();
				if (account == null) {
					Intent toLogin = new Intent(parentActivity, LoginActivity.class);
					parentActivity.startActivity(toLogin);
				} else {
					Intent toCommentActivity = new Intent(parentActivity, CommentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("isRe", false);
					bundle.putInt("paper_id", paper.getId());
					bundle.putString("token", account.getToken());
					toCommentActivity.putExtras(bundle);
					startActivityForResult(toCommentActivity, AppConstants.Comment_Activity_Code);
				}
			}
		});
	}

	private Handler commentListHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			completeGridRefresh();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Comment> commentList = (List<Comment>) msg.obj;
				int comment_num = msg.arg1;
				comment_count.setText(comment_num + "条评论");
				if (page == 1) {
					comment_linearlayout.removeAllViews();
				}
				adapter = new CommentAdapter(commentList, parentActivity, paper.getId(), CommentFragment.this);
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					View v = adapter.getDropDownView(i, null, null);
					comment_linearlayout.addView(v);
				}
				if (commentList.size() == 0) {
					root.onFooterShow("已到最后了", false);
					if (comment_linearlayout.getChildCount() != 0)
						Toast.makeText(parentActivity, "已到最后了", Toast.LENGTH_SHORT).show();
				}
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(parentActivity, "网络访问出错", Toast.LENGTH_SHORT).show();
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
		ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
		// getServiceRun(currentPageNo);
		System.out.println("onFooterRefresh");
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		isGridRefresh = true;
		page = 1;
		ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == parentActivity.RESULT_CANCELED)
			return;
		CommentResult cr = (CommentResult) data.getExtras().getSerializable("commentResult");
		comment_count.setText(cr.getComment_num() + "条评论");
		if (cr.isResult()) {
			isGridRefresh = true;
			page = 1;
			ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}