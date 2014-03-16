package com.xkwallpaper.ui;

import java.util.List;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.base.Comment;
import com.xkwallpaper.http.base.CommentResult;
import com.xkwallpaper.http.base.PostCommentBase;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.util.DialogUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseHomeActivity extends BaiduMTJActivity {

	private Button home_cancel;
	private GridView home_gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choosehome);

		setUpView();

	}

	private void setUpView() {
		home_gridview = (GridView) findViewById(R.id.home_gridview);
		home_cancel = (Button) findViewById(R.id.home_cancel);
		home_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		HomeAdaper ha = new HomeAdaper();
		home_gridview.setAdapter(ha);
	}

	class HomeAdaper extends BaseAdapter {
		private List<ResolveInfo> ris;
		private LayoutInflater mInflater;
		private PackageManager pm;

		public HomeAdaper() {
			Intent mIntent = new Intent(Intent.ACTION_MAIN);
			mIntent.addCategory(Intent.CATEGORY_HOME);
			pm = getPackageManager();
			ris = pm.queryIntentActivities(mIntent, 0);
			for (ResolveInfo ri : ris) {
				if (ri.activityInfo.packageName.equals(getApplication().getPackageName())) {
					ris.remove(ri);
				}
			}
			mInflater = LayoutInflater.from(ChooseHomeActivity.this);
		}

		@Override
		public int getCount() {
			return ris.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			final ResolveInfo ri = ris.get(arg0);
			convertView = mInflater.inflate(R.layout.choose_home_item, null);

			TextView home_text = (TextView) convertView.findViewById(R.id.home_text);
			Drawable drawable = ri.loadIcon(pm);  
			drawable.setBounds(0, 0, 70, 70);  
			home_text.setCompoundDrawables(null, drawable, null, null);
			home_text.setText(ri.loadLabel(pm).toString());
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					SharedPreferences choosehome = getSharedPreferences("choosehome", 0);
					SharedPreferences.Editor edit = choosehome.edit();
					edit.putString("packagename", ri.activityInfo.packageName);
					edit.commit();
					
					String pkg = ri.activityInfo.packageName;
					String cls = ri.activityInfo.name;
					ComponentName componet = new ComponentName(pkg, cls);
					Intent i = new Intent();
					i.setComponent(componet);
					startActivity(i);
					
					ChooseHomeActivity.this.finish();
				}
			});
			return convertView;
		}

	}

}
