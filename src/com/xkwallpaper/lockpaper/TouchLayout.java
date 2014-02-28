package com.xkwallpaper.lockpaper;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PraiseDAO;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.ShareTask;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.http.base.PostPraiseBase;
import com.xkwallpaper.http.base.PraiseResult;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DpSpDip2Px;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TouchLayout extends RelativeLayout {
	private SlidingCompleteListener slidingCompleteListener;
	float down = 0;
	float move = 0;
	float up = 0;

	private int mWidth, mHight;
	private int mShareViewWidth;
	private ImageView mShareView, mPraiseView;
	private DpSpDip2Px dp2px;
	private TextView praisePlus;
	private PraiseDAO praiseDAO;
	private Paper paper;
	private Animation appearAnimation;
	private View bottomLayout;
	private AccountDAO accountDAO;
	private DbAccount account;

	public void setPaper(Paper paper) {
		this.paper = paper;
		if (praiseDAO.isExist(paper.getId() + "")) {
			mPraiseView.setTag("已赞");
		} else {
			mPraiseView.setTag("未赞");
		}
	}

	public TouchLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews(context);

	}

	public TouchLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		initViews(context);

	}

	public TouchLayout(Context context) {
		super(context, null);
		initViews(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			mWidth = r;
			mHight = b;
			getViewMeasure();
			setChildViewLayout();
		}
	}

	public void setSlidingCompleteListener(SlidingCompleteListener slidingCompleteListener) {
		this.slidingCompleteListener = slidingCompleteListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d("testing", "_______________DDDDDDaaaaDDDDDDDDD________________-");
			down = y;
			if (down < (mHight - (mHight/5)))
				return false;
			break;
		case MotionEvent.ACTION_MOVE:
			move = down - y;
			if (move > 0) {
				int height = getHeight();
				android.view.animation.Animation animation = new android.view.animation.TranslateAnimation(0, 0, -move, 0);
				animation.setDuration(5);
				animation.setFillBefore(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						Log.d("testing", "___________onAnimationStart_______________-");
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						Log.d("testing", "____________onAnimationRepeat________________-");
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if (up > 0 && up < y/4) {
							ObjectAnimator yBouncer = ObjectAnimator.ofFloat(TouchLayout.this, "y", -up, 0).setDuration(1500);
							yBouncer.setInterpolator(new BounceInterpolator());
							Animator bounceAnim = new AnimatorSet();
							((AnimatorSet) bounceAnim).play(yBouncer);
							bounceAnim.start();
						}
						Log.d("testing", "_____________onAnimationEnd________________-");
					}
				});
				startAnimation(animation);
			}
			break;
		case MotionEvent.ACTION_UP:
			up = down - y;
			if (up > y/4) {
				android.view.animation.Animation animation = new android.view.animation.TranslateAnimation(0, 0, -move, -y);
				animation.setDuration(800);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						TouchLayout.this.setVisibility(View.GONE);
						slidingCompleteListener.onCall();
					}
				});
				startAnimation(animation);
			}
			Log.d("testing", "_______________UUUUUUUUUUUUUUUUUU________________-");
			break;
		}
		return true;
	}

	public interface SlidingCompleteListener {
		public void onCall();
	}

	/**
	 * 此处开始添加图标
	 */

	// 获取图标，将获取的图标添加入FxLockView，设置图标的可见性
	private void initViews(final Context context) {
		if (dp2px == null)
			dp2px = new DpSpDip2Px(context);
		if(accountDAO==null)
			accountDAO = new AccountDAO(context);
		praiseDAO = new PraiseDAO(context);
		appearAnimation = AnimationUtils.loadAnimation(context, R.anim.appear);

		praisePlus = new TextView(context);
		praisePlus.setText("+1");
		praisePlus.setVisibility(View.GONE);
		praisePlus.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		praisePlus.setGravity(Gravity.CENTER);
		praisePlus.setTextSize(dp2px.sp2px(23));
		addView(praisePlus);

		mShareView = new ImageView(context);
		mShareView.setImageResource(R.drawable.share);
		mShareView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareTask shareTask = new ShareTask((Activity)getContext(), paper, "lock");
				shareTask.execute();
			}
		});
		mShareView.setVisibility(View.VISIBLE);

		mPraiseView = new ImageView(context);
		mPraiseView.setImageResource(R.drawable.praise);
		mPraiseView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getTag().equals("已赞")) {
					return;
				} 
				account = accountDAO.getAccount();
				if(account == null){
					Toast.makeText(getContext(), "请先进入应用登录", Toast.LENGTH_SHORT).show();
				}
				else {
					mPraiseView.setTag("已赞");
					PostPraiseBase praiseBase = new PostPraiseBase();
					praiseBase.setPaper_id(paper.getId());
					praiseBase.setPrivate_token(account.getToken());
					ThreadExecutor.execute(new PostData(context, praiseHandler, praiseBase,1));
				}

			}
		});
		mPraiseView.setVisibility(View.VISIBLE);

		bottomLayout = getBottomLayout(getContext());
		bottomLayout.setVisibility(View.VISIBLE);
		
		addView(bottomLayout);
		setViewsLayout(mShareView);
		setViewsLayout(mPraiseView);

	}

	// 设置获取图标的参数，并添加到FxLockView
	private void setViewsLayout(ImageView image) {
		image.setScaleType(ScaleType.CENTER);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(image);
	}

	// 获取各个图标的宽、高
	private void getViewMeasure() {
		mShareViewWidth = dp2px.dip2px(25);
		//
		// mPraiseView.measure(View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED));
		// mPraiseViewWidth = mPraiseView.getMeasuredWidth();
		// mPraiseViewHigh = mPraiseView.getMeasuredHeight();
	}

	// 设置各图标在FxLockView中的布局
	private void setChildViewLayout() {
		if (mShareView != null)
			mShareView.layout((mWidth / 3) - mShareViewWidth, (mHight - (mHight >> 3)) - mShareViewWidth, (mWidth / 3) + mShareViewWidth, (mHight - (mHight >> 3)) + mShareViewWidth);
		if (mPraiseView != null)
			mPraiseView.layout((mWidth / 6) - mShareViewWidth, (mHight - (mHight >> 3)) - mShareViewWidth, (mWidth / 6) + mShareViewWidth, (mHight - (mHight >> 3)) + mShareViewWidth);
		if (praisePlus != null)
			praisePlus.layout((mWidth / 6) - mShareViewWidth, (mHight - (mHight >> 3)) - 15 - 3 * mShareViewWidth, (mWidth / 6) + mShareViewWidth, (mHight - (mHight >> 3)) - 15 - mShareViewWidth);
		if (bottomLayout != null)
			bottomLayout.layout(0, mHight - dp2px.dip2px(40), mWidth, mHight);
	}

	// 平方和计算
	private float dist2(float dx, float dy) {
		return dx * dx + dy * dy;
	}

	final Handler praiseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:

				PraiseResult pr = (PraiseResult) msg.obj;
				if (pr.isResult()) {
					mPraiseView.setTag("已赞");
					paper.setPraise(pr.getPraise_num());
					praiseDAO.save(paper, "lock");
					praisePlus.startAnimation(appearAnimation);
				} else {
					mPraiseView.setTag("未赞");
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				mPraiseView.setTag("未赞");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				mPraiseView.setTag("未赞");
				break;
			}
		};
	};

	private View getBottomLayout(Context mContext) {
		DpSpDip2Px dp2px = new DpSpDip2Px(mContext);
		LinearLayout bottomLayout = new LinearLayout(mContext);
		RelativeLayout.LayoutParams rllp = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px.dip2px(35));
		rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rllp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bottomLayout.setLayoutParams(rllp);
		bottomLayout.setGravity(Gravity.CENTER);
		bottomLayout.setOrientation(LinearLayout.HORIZONTAL);

		ImageView bottomImage = new ImageView(mContext);
		bottomImage.setLayoutParams(new LinearLayout.LayoutParams(dp2px.dip2px(45), dp2px.dip2px(35)));
		bottomImage.setImageResource(R.drawable.lock_prompt);
		bottomLayout.addView(bottomImage);

		TextView bottomText = new TextView(mContext);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		lllp.setMargins(dp2px.dip2px(5), 0, 0, 0);
		bottomText.setLayoutParams(lllp);
		bottomText.setGravity(Gravity.CENTER);
		bottomText.setText("向上滑动解锁");
		bottomText.setTextSize(dp2px.sp2px(13));
		bottomLayout.addView(bottomText);

		return bottomLayout;
	}
}