/**
 * 该组件未使用
 */
package com.xkwallpaper.ui.component;

import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DpSpDip2Px;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class PicInfoLayout extends FrameLayout {
	private OnPicInfoTouchListener onPicInfoTouchListener;
	private int mBgWidth, mBgHigh, mButtonBgWidth, mButtonBgHigh, mPraiseWidth, mPraiseHigh;
	private ImageView mBg;
	private TextView mButtonBg;
	private TextView mPraise;
	private int praiseNum;

	private int buttonBgMarginTop = 30;
	private int buttonBgMarginLeft = 10;
	private int praiseNumMarginRight = 20;

	public TextView getPraiseCount() {
		return mPraise;
	}

	public TextView getButtonBg() {
		return mButtonBg;
	}

	public void setmBg(Bitmap bm, Context context, int praiseNum) {
		if (mBg == null)
			mBg = new ImageView(context);
		if (bm == null)
			mBg.setImageResource(R.drawable.img08);
		else
			mBg.setImageBitmap(bm);
		this.removeAllViews();
		this.praiseNum = praiseNum;
		DpSpDip2Px dp2px = new DpSpDip2Px(context);
		mBgWidth = dp2px.getPicInfoWidth();
		mBgHigh = dp2px.getPicInfoHigh();
		initViews(context);
	}

	public PicInfoLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PicInfoLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public PicInfoLayout(Context context) {
		super(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {

			getViewMeasure();

			setChildViewLayout();

		}
	}

	public void setOnPicInfoTouchListener(OnPicInfoTouchListener onPicInfoTouchListener) {
		this.onPicInfoTouchListener = onPicInfoTouchListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:

			break;

		case MotionEvent.ACTION_UP:
			if (onPicInfoTouchListener != null)
				onPicInfoTouchListener.onTouchBg();
			break;
		}
		return true;
	}

	public interface OnPicInfoTouchListener {
		public void onTouchPraise(View args);

		public void onTouchBg();
	}

	/**
	 * 此处开始添加图标
	 */

	// 获取图标，将获取的图标添加入FxLockView，设置图标的可见性
	private void initViews(Context context) {
		mBg.setVisibility(View.VISIBLE);
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mBgHigh));
		addView(mBg);

		if (mButtonBg == null) {
			mButtonBg = new TextView(context);
			mButtonBg.setBackgroundResource(R.drawable.praise_button_bg);
			mButtonBg.setVisibility(View.VISIBLE);
			mButtonBg.setTag("未赞");

			mButtonBg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (onPicInfoTouchListener != null)
						onPicInfoTouchListener.onTouchPraise(arg0);
				}
			});
		}
		addView(mButtonBg);
		if (mPraise == null) {
			mPraise = new TextView(context);
			mPraise.setText(praiseNum + "");
			mPraise.setTextColor(Color.WHITE);
			mPraise.setTextSize(16);
			mPraise.setVisibility(View.VISIBLE);
		}
		addView(mPraise);
	}

	// 设置获取图标的参数，并添加到FxLockView
	private void setViewsLayout(ImageView image) {
		image.setScaleType(ScaleType.CENTER);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		image.setAdjustViewBounds(true);
		addView(image);
	}

	// 获取各个图标的宽、高
	private void getViewMeasure() {
		// mBg.measure(View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED));
		if (mBg != null) {

			// mButtonBg.measure(View.MeasureSpec.makeMeasureSpec(0,
			// View.MeasureSpec.UNSPECIFIED),
			// View.MeasureSpec.makeMeasureSpec(0,
			// View.MeasureSpec.UNSPECIFIED));
			// mButtonBgWidth = mButtonBg.getMeasuredWidth();
			// mButtonBgHigh = mButtonBg.getMeasuredHeight();

			mButtonBgWidth = 5 * mBgHigh / 14;
			mButtonBgHigh = mButtonBgWidth * 50 / 144;
			mButtonBg.setLayoutParams(new LayoutParams(mButtonBgWidth, mButtonBgHigh));

			mPraise.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			mPraiseWidth = mPraise.getMeasuredWidth();
			mPraiseHigh = mPraise.getMeasuredHeight();
		}

	}

	// 设置各图标在FxLockView中的布局
	private void setChildViewLayout() {
		if (mBg != null) {
			mBg.layout(0, 0, mBgWidth, mBgHigh);

			int w = buttonBgMarginLeft + (mButtonBgWidth / 2);   //中心点横坐标
			int h = buttonBgMarginTop + (mButtonBgHigh / 2);     //中心点纵坐标
			mButtonBg.layout(w - (mButtonBgWidth / 2), h - (mButtonBgHigh / 2), w + (mButtonBgWidth / 2), h + (mButtonBgHigh / 2));

			int w2 = buttonBgMarginLeft + mButtonBgWidth - praiseNumMarginRight - (mPraiseWidth / 2);  //中心点横坐标
			mPraise.layout(w2 - (mPraiseWidth / 2), h - (mPraiseHigh / 2), w2 + (mPraiseWidth / 2), h + (mPraiseHigh / 2));

		}
	}

}
