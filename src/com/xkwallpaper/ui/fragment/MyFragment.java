/**
 * 我的页面fragment，其中或嵌套三个子fragment
 */
package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends BaiduMTJFragment implements OnClickListener {

	private View root;
	private TextView my_download, my_collect, my_comment;
	private AccountDAO accountDAO;
	private DbAccount account;
	private MainActivity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.my_fragment_frame, null);
		getFragmentManager().beginTransaction().replace(R.id.my_content_frame, new MyDownloadFragment()).commit();
		my_download = (TextView) root.findViewById(R.id.my_download);
		my_collect = (TextView) root.findViewById(R.id.my_collect);
		my_comment = (TextView) root.findViewById(R.id.my_comment);
		my_download.setOnClickListener(this);
		my_collect.setOnClickListener(this);
		my_comment.setOnClickListener(this);
		accountDAO = new AccountDAO(parentActivity);

		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentActivity = (MainActivity) getActivity();
	}

	private void switchContent(Fragment fragment) {
		if (fragment != null) {
			getFragmentManager().beginTransaction().replace(R.id.my_content_frame, fragment).commit();
		}

	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.my_download:
			switchContent(new MyDownloadFragment());
			switchTab((TextView) arg0);
			break;
		case R.id.my_collect:
			switchContent(new MyCollectFragment());
			switchTab((TextView) arg0);
			break;
		case R.id.my_comment:
			account = accountDAO.getAccount();
			if(account==null){
				Intent toLogin = new Intent(parentActivity, LoginActivity.class);
				parentActivity.startActivity(toLogin);
			}else{
				MyCommetFragment toMyComment = new MyCommetFragment();
				Bundle b = new Bundle();
				b.putString("token", account.getToken());
				toMyComment.setArguments(b);
				switchContent(toMyComment);
				switchTab((TextView) arg0);
			}
			break;
		}
	}

	private void switchTab(TextView arg0) {

		my_download.setTextColor(getResources().getColor(R.color.grey));
		my_collect.setTextColor(getResources().getColor(R.color.grey));
		my_comment.setTextColor(getResources().getColor(R.color.grey));
		my_download.setBackgroundDrawable(null);
		my_collect.setBackgroundDrawable(null);
		my_comment.setBackgroundDrawable(null);

		arg0.setTextColor(Color.WHITE);
		arg0.setBackgroundResource(R.drawable.my_tab_bg_select);

	}

}
