package com.xkwallpaper.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.util.DpSpDip2Px;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class PicGridAdapter {
	private LinearLayout layout1;
	private LinearLayout layout2;
	private int len; // 1表示优先放在layout1，2表示优先放在layout2
	private List<Paper> papers;

	private List<Paper> papers1; // 临时变量，列表1
	private List<Paper> papers2; // 临时变量，列表2
	private MainActivity mActivity;
	private DpSpDip2Px dp2px;
	private int textNum;
	private String dir;

	public PicGridAdapter(LinearLayout layout1, LinearLayout layout2, MainActivity mActivity,String dir) {
		this.layout1 = layout1;
		this.layout2 = layout2;
		this.mActivity = mActivity;
		dp2px = new DpSpDip2Px(mActivity);
		textNum = dp2px.getGridTextNum(13);
		len = 1;
		this.dir = dir;
	}

	/**
	 * 每次都添加进来10个数据，如果少于10个，就认为是最后一页
	 * 
	 * @param papers
	 */
	public void addView(List<Paper> papers) {
		sortList(papers);
		completeAdd();

		if (this.papers == null) {
			this.papers = papers;
		} else {
			this.papers.addAll(papers);
		}
	}
	
	public void removeAll(){
		if(layout1!=null){
			layout1.removeAllViews();
		}
		if(layout2!=null){
			layout2.removeAllViews();
		}
		len = 1;
	}

	/**
	 * 此处用于将新添加的papers根据大小长度分为两组，根据len的值确定新的paper放在那个list 每次比较两个值a1和a2，
	 * 若a1=a2，l1.add(a1)，l2.add(a2) 若a1>a2&&len=0，l1.add(a1)，l2.add(a2)，len＝1
	 * 若a1>a2&&len=1，l1.add(a2)，l2.add(a1)，len＝0
	 * 若a1<a2&&len=0，l1.add(a2)，l2.add(a1)，len＝1
	 * 若a1<a2&&len=1，l1.add(a1)，l2.add(a2)，len＝0 循环比较下一个
	 * 
	 * @param papers
	 */
	private void sortList(List<Paper> papers) {
		papers1 = new ArrayList<Paper>();
		papers2 = new ArrayList<Paper>();
		int templen = papers.size();
		for (int i = 0; i < templen;) {
			if (i == templen - 1) {
				if (len == 1) {
					papers1.add(papers.get(i));
					len = 2;
				} else {
					papers2.add(papers.get(i));
					len = 1;
				}
				break;
			} else {
				boolean b1 = tagsToLen(papers.get(i).getTags());
				boolean b2 = tagsToLen(papers.get(i + 1).getTags());
				if ((b1 & b2) || (!b1 && !b2)) {
					papers1.add(papers.get(i));
					papers2.add(papers.get(i + 1));
				} else if (!b1 && b2) {
					switch (len) {
					case 1:
						papers1.add(papers.get(i));
						papers2.add(papers.get(i + 1));
						len = 2;
						break;
					case 2:
						papers1.add(papers.get(i + 1));
						papers2.add(papers.get(i));
						len = 1;
						break;
					}

				} else {
					switch (len) {
					case 1:
						papers1.add(papers.get(i + 1));
						papers2.add(papers.get(i));
						len = 2;
						break;
					case 2:
						papers1.add(papers.get(i));
						papers2.add(papers.get(i + 1));
						len = 1;
						break;
					}

				}

				i = i + 2;
			}
		}

	}

	/**
	 * 将分好的组进行初始化，新建item并写到对应的layout中，然后进行图片的异步下载，点赞和下载相关的也放在这里
	 */
	private void completeAdd() {
		PicGridListAdapter adapter = new PicGridListAdapter(papers1, mActivity,dir);
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, dp2px.dip2px(3), 0, 0);
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = adapter.getDropDownView(i, null, null);
			v.setLayoutParams(lp);
			layout1.addView(v);
		}
		adapter = new PicGridListAdapter(papers2, mActivity,dir);
		count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = adapter.getDropDownView(i, null, null);
			v.setLayoutParams(lp);
			layout2.addView(v);
		}
		papers1 = null;
		papers2 = null;
	}

	private String tagsToString(List<String> tags) {
		String result = "";
		for (int i = 0; i < tags.size(); i++) {
			result += tags.get(i);
			if (i + 1 != tags.size()) {
				result += "、";
			}
		}
		return result;
	}

	// 1行则是true
	private boolean tagsToLen(List<String> tags) {
		int len1 = tagsToString(tags).length();
		return len1 > textNum;
	}
}
