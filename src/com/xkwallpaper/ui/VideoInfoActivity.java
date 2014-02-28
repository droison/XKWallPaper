package com.xkwallpaper.ui;

import java.io.File;

import com.xkwallpaper.baidumtj.BaiduMTJFragmentActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PraiseDAO;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.ViewVideoTask.DownViewCallBack;
import com.xkwallpaper.http.ViewVideoTask;
import com.xkwallpaper.http.AsyncImageLoader.ImageCallback;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.http.base.PostPraiseBase;
import com.xkwallpaper.http.base.PraiseResult;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.component.PicInfoLayout;
import com.xkwallpaper.ui.component.PicInfoLayout.OnPicInfoTouchListener;
import com.xkwallpaper.ui.fragment.BottomTabFragment;
import com.xkwallpaper.ui.fragment.CommentFragment;
import com.xkwallpaper.ui.fragment.InfoPayFragment;
import com.xkwallpaper.util.DpSpDip2Px;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class VideoInfoActivity extends BaiduMTJFragmentActivity {
	private RelativeLayout videoinfo_layout;
	private RelativeLayout videoContainer;
	private PicInfoLayout picContainer;
	private ImageView title_one_name_image;
	private FrameLayout layout_paperpay;

	private int mVideoContentHigh = 200;
	private DpSpDip2Px dp2px;

	private ImageView startbtn;
	private Bundle bundle;
	private Paper paper;
	private CommentFragment commentFragment;
	private BottomTabFragment bottomTabFragment;
	private FragmentTransaction ft;
	private AsyncImageLoader imageLoader;
	private String dir;
	private TextView mButtonBg, mPraise;
	private PraiseDAO praiseDAO;
	private ViewVideoTask viewViewTask;
	private AccountDAO accountDAO;
	private DbAccount account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoinfo);
		initData();
		setUpView();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void initData() {
		bundle = getIntent().getExtras();
		paper = (Paper) bundle.getSerializable("paper");
		dir = bundle.getString("dir");
	}

	// 初始化view和初始化fragment
	private void setUpView() {
		videoinfo_layout = (RelativeLayout) this.findViewById(R.id.videoinfo_layout);
		picContainer = (PicInfoLayout) this.findViewById(R.id.picContainer);
		videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);

		layout_paperpay = (FrameLayout) this.findViewById(R.id.layout_paperpay);

		title_one_name_image = (ImageView) this.findViewById(R.id.title_one_name_image);
		title_one_name_image.setImageResource(R.drawable.title_shipin);

		picContainer.setVisibility(View.VISIBLE);
		videoContainer.setVisibility(View.GONE);

		ft = getSupportFragmentManager().beginTransaction();

		if (!paper.isPay()) {
			layout_paperpay.setVisibility(View.GONE);
		} else {
			InfoPayFragment infoPayFragment = new InfoPayFragment();
			infoPayFragment.setArguments(bundle);
			ft.replace(R.id.layout_paperpay, infoPayFragment);
		}

		commentFragment = new CommentFragment();
		commentFragment.setArguments(bundle);
		ft.replace(R.id.vid_comment_frame, commentFragment);

		bottomTabFragment = new BottomTabFragment();
		bottomTabFragment.setArguments(bundle);
		ft.replace(R.id.video_bottom_frame, bottomTabFragment).commit();

		praiseDAO = new PraiseDAO(this);
		// 设置默认图片
		picContainer.setmBg(null, VideoInfoActivity.this, paper.getPraise());
		mButtonBg = picContainer.getButtonBg();
		mPraise = picContainer.getPraiseCount();
		if (praiseDAO.isExist(paper.getId() + "")) {
			mButtonBg.setTag("已赞");
			mButtonBg.setPressed(true);
		}

		dp2px = new DpSpDip2Px(this);
		accountDAO = new AccountDAO(this);

		if (paper.getMphoto() != null) {
			imageLoader = new AsyncImageLoader();
			imageLoader.loadDrawable(this, AppConstants.HTTPURL.serverIP + paper.getMphoto(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					if (bm != null) {
						picContainer.setmBg(bm, VideoInfoActivity.this, paper.getPraise());

						videoContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dp2px.getPicInfoHigh()));

						startbtn = new ImageView(VideoInfoActivity.this);
						startbtn.setImageResource(R.drawable.start_btn);
						RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(dp2px.dip2px(40), dp2px.dip2px(40));
						rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
						startbtn.setLayoutParams(rllp);
						videoinfo_layout.addView(startbtn);
						startbtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								String path = AppConstants.APP_FILE_PATH + "/download/" + paper.getId() + ".mp4";
								File newfile = new File(path);
								if (newfile.exists()) {
									Intent toVideoPlay = new Intent(VideoInfoActivity.this, VideoPlayActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("paper", paper);
									toVideoPlay.putExtras(bundle);
									VideoInfoActivity.this.startActivity(toVideoPlay);

								} else {
									viewViewTask = new ViewVideoTask(VideoInfoActivity.this, paper, new DownViewCallBack() {

										@Override
										public void call(boolean isSuccess, String path) {
											if (isSuccess) {
												Intent toVideoPlay = new Intent(VideoInfoActivity.this, VideoPlayActivity.class);
												Bundle bundle = new Bundle();
												bundle.putSerializable("paper", paper);
												toVideoPlay.putExtras(bundle);
												VideoInfoActivity.this.startActivity(toVideoPlay);

											}else{
												Toast.makeText(VideoInfoActivity.this, "失败，请稍后重试", Toast.LENGTH_SHORT).show();
											}

										}
									});
									viewViewTask.execute();
								}
							}
						});

					}
				}
			}, dir, paper.getId() + ".info");
		}

		picContainer.setOnPicInfoTouchListener(new OnPicInfoTouchListener() {

			@Override
			public void onTouchPraise(View arg0) {
				if (arg0.getTag().equals("已赞")) {
					return;
				}
				account = accountDAO.getAccount();
				if (account == null) {
					Intent toLogin = new Intent(VideoInfoActivity.this, LoginActivity.class);
					startActivityForResult(toLogin, 12);
				} else {
					arg0.setTag("已赞");
					PostPraiseBase praiseBase = new PostPraiseBase();
					praiseBase.setPaper_id(paper.getId());
					praiseBase.setPrivate_token(account.getToken());
					ThreadExecutor.execute(new PostData(VideoInfoActivity.this, praiseHandler, praiseBase, 1));
				}
			}

			@Override
			public void onTouchBg() {

			}
		});

	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

		Bitmap bm = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bm);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);

		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		int mScreenWidth = dm.widthPixels;// 屏幕宽
		int bmHigh = bm.getHeight();
		int bmWidth = bm.getWidth();
		mVideoContentHigh = (int) mScreenWidth * bmHigh / bmWidth;
		return Bitmap.createScaledBitmap(bm, mScreenWidth, mVideoContentHigh, true);
	}

	final Handler praiseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:
				PraiseResult pr = (PraiseResult) msg.obj;
				if (pr.isResult()) {
					mButtonBg.setTag("已赞");
					mButtonBg.setPressed(true);
					paper.setPraise(pr.getPraise_num());
					mPraise.setText(String.valueOf(paper.getPraise()));
					praiseDAO.save(paper, dir);
				} else {
					mButtonBg.setTag("未赞");
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				mButtonBg.setTag("未赞");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				mButtonBg.setTag("未赞");
				break;
			}
		};
	};
}
