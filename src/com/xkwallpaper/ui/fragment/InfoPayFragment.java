package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PayDAO;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.thread.OrderCreateLoader;
import com.xkwallpaper.thread.OrderCreateLoader.OnPayCompleteListener;
import com.xkwallpaper.ui.LoginActivity;
import com.xkwallpaper.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class InfoPayFragment extends BaiduMTJFragment {

	private TextView paper_price, paper_pay;
	private View root;
	private Paper paper;
	private Activity parentActivity;
	private AccountDAO accountDAO;
	private DbAccount account;
	private PayDAO payDAO;
	private OrderCreateLoader orderCreateLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.info_pay_fragment, null);
		setUpView();
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentActivity = getActivity();
		accountDAO = new AccountDAO(parentActivity);
		payDAO = new PayDAO(parentActivity);
		account = accountDAO.getAccount();
		orderCreateLoader = new OrderCreateLoader();
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		this.paper = (Paper) args.getSerializable("paper");
	}

	public void setUpView() {
		paper_pay = (TextView) root.findViewById(R.id.paper_pay);
		paper_price = (TextView) root.findViewById(R.id.paper_price);

		paper_price.setText("￥" + paper.getPrice());
		paper_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				orderCreateLoader.loadOrder(parentActivity, paper, new OnPayCompleteListener() {
					
					@Override
					public void call(boolean isComplete) {
						if(isComplete){
							Toast.makeText(parentActivity, "已支付", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

}
