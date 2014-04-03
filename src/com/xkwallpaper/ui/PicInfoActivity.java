/**
 * 壁纸、锁屏的详情页面
 */
package com.xkwallpaper.ui;

import cn.sharesdk.framework.ShareSDK;

import com.xkwallpaper.baidumtj.BaiduMTJFragmentActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PraiseDAO;
import com.xkwallpaper.http.AsyncImageLoader;
import com.xkwallpaper.http.PostData;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class PicInfoActivity extends BaiduMTJFragmentActivity {
	private PicInfoLayout picinfo_layout;
	private ImageView title_one_name_image;
	private FrameLayout layout_paperpay;

	private Paper paper;
	private Bundle bundle;
	private AsyncImageLoader imageLoader;
	private CommentFragment commentFragment;
	private BottomTabFragment bottomTabFragment;
	private FragmentTransaction ft;
	private TextView mButtonBg, mPraise;
	private PraiseDAO praiseDAO;
	private String dir;
	private AccountDAO accountDAO;
	private DbAccount account;
	private DpSpDip2Px dp2px;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picinfo);
		initData();
		setUpView();
		setPicLayoutView();
		ShareSDK.initSDK(this);
	}

	// 设置本activity唯一个应该处理的view
	private void setPicLayoutView() {
		// 设置默认图片
		picinfo_layout.setmBg(null, PicInfoActivity.this, paper.getPraise());
		mButtonBg = picinfo_layout.getButtonBg();
		mPraise = picinfo_layout.getPraiseCount();
		if (praiseDAO.isExist(paper.getId() + "")) {
			mButtonBg.setTag("已赞");
			mButtonBg.setPressed(true);
		}

		if (paper.getMphoto() != null) {
			imageLoader = new AsyncImageLoader();
			imageLoader.loadDrawable(this, AppConstants.HTTPURL.serverIP + paper.getMphoto(), new ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bm, String imageUrl) {
					if (bm != null) {
						picinfo_layout.setmBg(bm, PicInfoActivity.this, paper.getPraise());
					}
				}
			}, dir, paper.getId() + ".info");
		}
		picinfo_layout.setOnPicInfoTouchListener(new OnPicInfoTouchListener() {

			@Override
			public void onTouchPraise(View arg0) {
				if (arg0.getTag().equals("已赞")) {

					return;
				}
				account = accountDAO.getAccount();
				if (account == null) {
					Intent toLogin = new Intent(PicInfoActivity.this, LoginActivity.class);
					startActivityForResult(toLogin, 12);
				} else {
					arg0.setTag("已赞");
					PostPraiseBase praiseBase = new PostPraiseBase();
					praiseBase.setPaper_id(paper.getId());
					praiseBase.setPrivate_token(account.getToken());
					ThreadExecutor.execute(new PostData(PicInfoActivity.this, praiseHandler, praiseBase, 1));
				}
			}

			@Override
			public void onTouchBg() {
				Intent toPicViewActivity = new Intent(PicInfoActivity.this, PicViewActivity.class);
				toPicViewActivity.putExtras(bundle);
				PicInfoActivity.this.startActivity(toPicViewActivity);
			}
		});
	}

	// 初始化view并初始化fragment
	private void setUpView() {
		picinfo_layout = (PicInfoLayout) this.findViewById(R.id.picinfo_layout);
		title_one_name_image = (ImageView) this.findViewById(R.id.title_one_name_image);

		layout_paperpay = (FrameLayout) this.findViewById(R.id.layout_paperpay);

		praiseDAO = new PraiseDAO(this);
		accountDAO = new AccountDAO(this);

		if (dir.equals("lock")) {
			title_one_name_image.setImageResource(R.drawable.title_suoping);
		}

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
		ft.replace(R.id.pic_comment_frame, commentFragment);

		bottomTabFragment = new BottomTabFragment();
		bundle.putString("dir", dir);
		bottomTabFragment.setArguments(bundle);
		ft.replace(R.id.pic_bottom_frame, bottomTabFragment).commit();

		dp2px = new DpSpDip2Px(this);
	}

	private void initData() {
		bundle = getIntent().getExtras();
		dir = bundle.getString("dir");
		paper = (Paper) bundle.getSerializable("paper");
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
		return Bitmap.createScaledBitmap(bm, mScreenWidth, (int) mScreenWidth * bmHigh / bmWidth, true);
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

	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	};
}
