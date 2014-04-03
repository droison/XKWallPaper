/**
 * 我的页面中，我的评论fragment
 */
package com.xkwallpaper.ui.fragment;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.DeleteComment;
import com.xkwallpaper.http.GetList;
import com.xkwallpaper.http.HTTP;
import com.xkwallpaper.http.base.Comment;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.PicInfoActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.ui.VideoInfoActivity;
import com.xkwallpaper.ui.component.PullToRefreshView;
import com.xkwallpaper.util.DialogUtil;
import com.xkwallpaper.util.JsonUtil;
import com.xkwallpaper.util.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyCommetFragment extends BaiduMTJFragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

	private Activity parentActivity;
	private PullToRefreshView root;
	private LinearLayout my_comment_linearlayout;

	private boolean isGridRefresh = false;
	private boolean isGridLoadMore = false;
	private String serverUrl;
	private DialogUtil dialogUtil;
	private MyCommentAdapter adapter;
	private AsyncImageLoader imageLoader;

	private int page = 1;
	private String token;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		token = args.getString("token");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = (PullToRefreshView) inflater.inflate(R.layout.my_comment_fragment, null);
		setUpView();
		initData();
		return root;
	}

	private void setUpView() {
		parentActivity = getActivity();
		if (getActivity() instanceof MainActivity) {
			parentActivity = (MainActivity) getActivity();
		}

		dialogUtil = new DialogUtil();
		imageLoader = new AsyncImageLoader();
		my_comment_linearlayout = (LinearLayout) root.findViewById(R.id.my_comment_linearlayout);
		root.setOnHeaderRefreshListener(this);
		root.setOnFooterRefreshListener(this);
	}

	private void initData() {
		serverUrl = AppConstants.HTTPURL.commentAll + "?private_token=" + token + "&page=";
		ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
		dialogUtil.showProgressDialog(parentActivity, "正在读取数据...");
		isGridRefresh = true;
	}

	private Handler commentListHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			completeGridRefresh();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				List<Comment> commentList = (List<Comment>) msg.obj;
				int comment_num = msg.arg1;
				if (page == 1) {
					my_comment_linearlayout.removeAllViews();
				}
				if (commentList == null || commentList.size() == 0) {
					root.onFooterShow("已到最后了", false);
					Toast.makeText(parentActivity, "已到最后了", Toast.LENGTH_SHORT).show();
					return;
				}
				adapter = new MyCommentAdapter(commentList);
				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {
					View v = adapter.getDropDownView(i, null, null);
					my_comment_linearlayout.addView(v);
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

	class MyCommentAdapter extends BaseAdapter {

		List<Comment> commentList;
		LayoutInflater mInflater;

		public MyCommentAdapter(List<Comment> commentList) {
			this.commentList = commentList;
			mInflater = LayoutInflater.from(parentActivity);
		}

		@Override
		public int getCount() {
			return commentList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return commentList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			final Comment comment = commentList.get(arg0);
			convertView = mInflater.inflate(R.layout.my_comment_item, null);

			final ImageView img = (ImageView) convertView.findViewById(R.id.my_comment_pic);
			TextView content = (TextView) convertView.findViewById(R.id.my_comment_content);
			String dir = null;
			if(comment.getPaper_style()==1){
				dir = "pic";
			}else if (comment.getPaper_style()==2) {
				dir = "lock";
			}else{
				dir = "vid";
			}
				
			imageLoader.loadDrawable(parentActivity, AppConstants.HTTPURL.serverIP + comment.getSphoto(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					img.setImageBitmap(bm);
				}
			}, dir, comment.getPaper_id() + ".thumb");
			content.setText(comment.getContent());
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					showDeleteAlert(comment.getId());

					return false;
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialogUtil.showProgressDialog(parentActivity, "读取数据...");
					ThreadExecutor.execute(new GetPaperInfoThread(comment.getPaper_id(), comment.getPaper_style(), getPaperInfoHandler));
				}
			});
			return convertView;
		}

		private Handler deleteCommentHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case AppConstants.HANDLER_MESSAGE_NORMAL:
					page = 1;
					ThreadExecutor.execute(new GetList(parentActivity, commentListHandler, serverUrl + page, 4));
					break;
				case AppConstants.HANDLER_HTTPSTATUS_ERROR:
					Toast.makeText(parentActivity, "删除失败", Toast.LENGTH_SHORT).show();
					break;
				case AppConstants.HANDLER_MESSAGE_NONETWORK:
					dialogUtil.showNoNetWork(parentActivity);
					break;
				}
			};
		};

		private void showDeleteAlert(final int commentId) {
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
					ThreadExecutor.execute(new DeleteComment(deleteCommentHandler, AppConstants.HTTPURL.commentDelete + commentId));
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

	Handler getPaperInfoHandler = new Handler() {

		public void handleMessage(Message msg) {
			dialogUtil.dismissProgressDialog();
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				Paper paper = (Paper) msg.obj;
				if (paper.getStyle() == 3) {
					Intent toVidInfoActivity = new Intent(parentActivity, VideoInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("paper", paper);
					bundle.putString("dir", "vid");
					toVidInfoActivity.putExtras(bundle);
					parentActivity.startActivity(toVidInfoActivity);
				} else {
					String dir = paper.getStyle() == 1 ? "pic" : "lock";
					Intent toPicInfoActivity = new Intent(parentActivity, PicInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("paper", paper);
					bundle.putString("dir", dir);
					toPicInfoActivity.putExtras(bundle);
					parentActivity.startActivity(toPicInfoActivity);
				}
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(parentActivity, "失败，请稍后重试", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	class GetPaperInfoThread implements Runnable {
		int paper_id;
		int paper_style;
		Handler mHandler;

		public GetPaperInfoThread(int paper_id, int paper_style, Handler mHandler) {
			this.paper_id = paper_id;
			this.paper_style = paper_style;
			this.mHandler = mHandler;
		}

		@Override
		public void run() {
			Paper paper = getPaperInfo(paper_id, paper_style);
			if (paper == null) {
				mHandler.sendEmptyMessage(AppConstants.HANDLER_HTTPSTATUS_ERROR);
			} else {
				paper.setStyle(paper_style);
				mHandler.sendMessage(mHandler.obtainMessage(AppConstants.HANDLER_MESSAGE_NORMAL, paper));
			}

		}

	}

	private Paper getPaperInfo(int paper_id, int paper_style) {
		String url = "";
		if (paper_style == 1) {
			url = AppConstants.HTTPURL.picInfo + paper_id;
		} else if (paper_style == 2) {
			url = AppConstants.HTTPURL.lockInfo + paper_id;
		} else {
			url = AppConstants.HTTPURL.vidInfo + paper_id;
		}
		HttpResponseEntity hre = HTTP.get(url);
		Paper paper = null;
		if (hre != null && hre.getHttpResponseCode() == HttpStatus.SC_OK) {
			try {
				String json = StringUtil.byte2String(hre.getB());
				paper = (Paper) JsonUtil.jsonToObject(json, Paper.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return paper;
	}
}
