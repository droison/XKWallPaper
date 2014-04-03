/**
 * 该组件未使用
 */
package com.xkwallpaper.ui.component;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class TagButton extends Button{

	private int tagHeight,tagWidth;
	
	public TagButton(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		tagHeight = View.MeasureSpec.getSize(heightMeasureSpec);
		tagWidth = View.MeasureSpec.getSize(widthMeasureSpec);
//		setMeasuredDimension(width, height);   此处用来设定view的大小
	}

	public int getTagHeight() {
		return tagHeight;
	}

	public void setTagHeight(int tagHeight) {
		this.tagHeight = tagHeight;
	}

	public int getTagWidth() {
		return tagWidth;
	}

	public void setTagWidth(int tagWidth) {
		this.tagWidth = tagWidth;
	}
	
}
