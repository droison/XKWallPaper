package com.xkwallpaper.util;

import com.xkwallpaper.http.base.DownloadResult;
import com.xkwallpaper.http.base.Paper;

import android.content.Context;

public class DpSpDip2Px {

	float scale;
	float fontScale;
	int screenPxWidth;
	float screenDpWidth;
	float screenDpHigh;

	public DpSpDip2Px(Context context) {
		scale = context.getResources().getDisplayMetrics().density;
		fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		screenPxWidth = context.getResources().getDisplayMetrics().widthPixels;
		screenDpHigh = context.getResources().getDisplayMetrics().heightPixels;
	}

	public int getPicInfoHigh() {
		return 2 * screenPxWidth / 3;
	}
	public int getPicInfoWidth() {
		return screenPxWidth;
	}
	//间距24DP 6+ 2+2 + 4 +2+2 +6
	public int getPicThumbHigh(){
		return (screenPxWidth-dip2px(24)) / 3;
	}

	// 这是dp
	public int getScreenDpWidth() {
		return px2dip(screenPxWidth);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue) {
		return (int) (dpValue * scale + 0.5f);
	}

	public int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}

	public int px2sp(float pxValue) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	public int sp2px(float spValue) {
		return (int) (spValue * fontScale + 0.5f);
	}

	public int sp2dp(float spValue) {
		float pxValue = spValue * fontScale + 0.5f;
		return (int) (pxValue / scale + 0.5f);
	}

	// 返回的是像素
	public int getGridItemWidth() {
		// 间距24dp，阴影8px
		return (screenPxWidth - 8 - dip2px(24)) / 2;
	}

	// 返回的是文字个数
	public int getGridTextNum(float spValue) {
		return (int) (getGridItemWidth() / sp2px(spValue));
	}

	// h:480 800 960 1280
	public String getSuitPhoto(Paper paper) {
		if (screenDpHigh <= 480) {
			return paper.getPhoto_1();
		} else if (screenDpHigh <= 800) {
			return paper.getPhoto_2();
		} else if (screenDpHigh <= 960) {
			return paper.getPhoto_3();
		} else {
			return paper.getPhoto_4();
		}

	}

	public String getSuitPhoto(DownloadResult downloadResult) {
		if (screenDpHigh <= 480) {
			return downloadResult.getPhoto_1();
		} else if (screenDpHigh <= 800) {
			return downloadResult.getPhoto_2();
		} else if (screenDpHigh <= 960) {
			return downloadResult.getPhoto_3();
		} else {
			return downloadResult.getPhoto_4();
		}

	}
}
