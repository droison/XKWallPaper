package com.xkwallpaper.lockpaper;

import com.xkwallpaper.ui.R;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class SlideBar extends RelativeLayout {

	private static final String TAG = "SlideBar";
	private static final boolean DEBUG = false;

	View mGradientView;
	private int gradientViewStartX;
	private float mEventDownX;
	private float mGradientViewIndicateLeft;
	private OnTriggerListener mOnTriggerListener;
	private VelocityTracker mVelocityTracker = null;
	private int mMinVelocityXToUnlock;
	private int mMinDistanceToUnlock;
	private int mLeftAnimationDuration;
	private int mRightAnimationDuration;
	private ObjectAnimator animLeftMoveAnimator;
	private ObjectAnimator animRightMoveAnimator;
	private static final int MaxDistance = 400;

	public interface OnTriggerListener {
		public void onTrigger();
	}

	public SlideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		gradientViewStartX = 0;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideBar);
		mMinVelocityXToUnlock = a.getInt(R.styleable.SlideBar_MinVelocityXToUnlock, 2000);
		mMinDistanceToUnlock = a.getInt(R.styleable.SlideBar_MinDistanceToUnlock, 300);
		mLeftAnimationDuration = a.getInt(R.styleable.SlideBar_LeftAnimationDuratioin, 250);
		mRightAnimationDuration = a.getInt(R.styleable.SlideBar_RightAnimationDuratioin, 300);
		a.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getActionMasked();
		boolean handled = false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(event);

		switch (action) {
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			if (DEBUG)
				Log.v(TAG, "*** DOWN ***");
			handleDown(event);
			handled = true;
			break;

		case MotionEvent.ACTION_MOVE:
			if (DEBUG)
				Log.v(TAG, "*** MOVE ***");
			handleMove(event);
			handled = true;
			break;

		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			if (DEBUG)
				Log.v(TAG, "*** UP ***");
			handleUp(event);
			handled = true;
			break;

		case MotionEvent.ACTION_CANCEL:
			if (DEBUG)
				Log.v(TAG, "*** CANCEL ***");
			handled = true;
			break;

		}
		invalidate();
		return handled ? true : super.onTouchEvent(event);
	}

	private void handleUp(MotionEvent event) {

		Log.v(TAG, "handleUp,mIndicateLeft:" + mGradientViewIndicateLeft);
		// 1. if user slide some distance, unlock
		if (mGradientViewIndicateLeft >= mMinDistanceToUnlock) {
			unlockSuccess();
			return;
		}
		// 2. if user slide very fast, unlock
		if (velocityTrigUnlock()) {
			return;
		}
		// otherwise reset the controls
		resetControls();
	}

	/**
	 * another way to unlock, if user slide very fast
	 */
	private boolean velocityTrigUnlock() {
		final VelocityTracker velocityTracker = mVelocityTracker;
		velocityTracker.computeCurrentVelocity(1000);

		int velocityX = (int) velocityTracker.getXVelocity();

		Log.v(TAG, "velocityX:" + velocityX);

		if (velocityX > mMinVelocityXToUnlock) {
			unlockSuccess();
			return true;
		}

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
		return false;
	}

	private void unlockSuccess() {
		mOnTriggerListener.onTrigger();
		if (android.os.Build.VERSION.SDK_INT > 10) {
			animRightMoveAnimator = ObjectAnimator.ofFloat(mGradientView, "x", mGradientView.getX(), MaxDistance).setDuration(mRightAnimationDuration);
			animRightMoveAnimator.start();
		}else{
			
		}
	}

	private void handleMove(MotionEvent event) {

		mGradientViewIndicateLeft = event.getX() - mEventDownX + gradientViewStartX;
		if (mGradientViewIndicateLeft <= gradientViewStartX) {
			mGradientViewIndicateLeft = gradientViewStartX;
		}
		if (android.os.Build.VERSION.SDK_INT > 10)
			mGradientView.setX(mGradientViewIndicateLeft);
		else {
			android.view.animation.Animation animation = new android.view.animation.TranslateAnimation(mGradientViewIndicateLeft, 0, 0, 0);
			animation.setDuration(1500);
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

				}
			});
			mGradientView.startAnimation(animation);
		}
	}

	private void handleDown(MotionEvent event) {
		mEventDownX = event.getX();
		if (mGradientView == null) {
			mGradientView = findViewById(R.id.lock_touchtest);
		}

	}

	public void setOnTriggerListener(OnTriggerListener listener) {
		mOnTriggerListener = listener;
	}

	private void resetControls() {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			animLeftMoveAnimator = ObjectAnimator.ofFloat(mGradientView, "x", mGradientView.getX(), gradientViewStartX).setDuration(mLeftAnimationDuration);
			animLeftMoveAnimator.start();
		}
	}

}
