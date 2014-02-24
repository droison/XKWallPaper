package com.xkwallpaper.ui.fragment;

import com.xkwallpaper.baidumtj.BaiduMTJFragment;
import com.xkwallpaper.db.SearchDAO;
import com.xkwallpaper.ui.MainActivity;
import com.xkwallpaper.ui.R;
import com.xkwallpaper.util.DialogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SearchFragment extends BaiduMTJFragment implements OnClickListener {

	private View root;
	private MainActivity parentActivity;

	private boolean isSearch;
	private Bundle bundle;
	private String keyword;
	private int search_type;// 1代表关键词搜索，2代表标签搜索
	private SearchDAO searchDAO;
	private ImageView search_btnClear, search_btnSearch;
	private EditText search_key;
	private DialogUtil dialogUtil;
	private SearchDefaultFragment sdf;
	private SearchResultFragment srf;
	private final String TAG = "SearchFragment";

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		isSearch = args.getBoolean("isSearch", false);
		bundle = args;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.content_search, null);
		setUpView();
		setUpListener();
		return root;
	}

	private void setUpView() {
		search_btnClear = (ImageView) root.findViewById(R.id.search_btnClear);
		search_btnSearch = (ImageView) root.findViewById(R.id.search_btnSearch);
		search_key = (EditText) root.findViewById(R.id.search_key);
		dialogUtil = new DialogUtil();

		if (!isSearch) {
			sdf = new SearchDefaultFragment();
			getFragmentManager().beginTransaction().replace(R.id.search_frame, sdf).commit();
		} else {
			srf = new SearchResultFragment();
			srf.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.search_frame, srf).commit();
			keyword = bundle.getString("keyword");
			search_type = bundle.getInt("search_type");
			search_key.setText(keyword);
			searchDAO = new SearchDAO(parentActivity);
			searchDAO.save(keyword, search_type);
		}
	}

	private void setUpListener() {
		search_btnClear.setOnClickListener(this);
		search_btnSearch.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	// 不显示了
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(isSearch){
				getFragmentManager().beginTransaction().remove(srf).commit();
			}else{
				getFragmentManager().beginTransaction().remove(sdf).commit();
			}
		} catch (IllegalStateException e) {
			Log.e(TAG, "关闭子搜索fragment时出现一场", e);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.search_btnClear:
			search_key.setText("");
			break;
		case R.id.search_btnSearch:
			String keyword = search_key.getText().toString().trim();
			((InputMethodManager) parentActivity.getSystemService(parentActivity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(parentActivity.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			if (TextUtils.isEmpty(keyword)) {
				search_key.setText("");
				dialogUtil.showSetPicToast(parentActivity, "关键词不能为空");
			} else {
				SearchFragment searchFragment = new SearchFragment();
				Bundle b = new Bundle();
				b.putBoolean("isSearch", true);
				b.putString("keyword", keyword);
				b.putInt("search_type", 1);
				b.putInt("paper_type", 0);
				searchFragment.setArguments(b);
				parentActivity.switchSearchContent(searchFragment);
			}
			break;
		default:
			break;
		}
	}
}
