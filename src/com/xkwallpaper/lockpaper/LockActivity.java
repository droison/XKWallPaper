package com.xkwallpaper.lockpaper;

import cn.sharesdk.framework.ShareSDK;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.db.AccountDAO;
import com.xkwallpaper.db.DbAccount;
import com.xkwallpaper.db.PraiseDAO;
import com.xkwallpaper.http.PostData;
import com.xkwallpaper.http.ShareTask;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.http.base.PostPraiseBase;
import com.xkwallpaper.http.base.PraiseResult;
import com.xkwallpaper.lockpaper.SlideBar.OnTriggerListener;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.ui.R;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockActivity extends BaiduMTJActivity implements OnTriggerListener ,OnClickListener{
	private FrameLayout lock_linearlayout;
	float temp = 0;

	private static final boolean DBG = true;
	private static final String TAG = "LockActivity";
	public static StatusViewManager mStatusViewManager;

	private ImageView lock_praise, lock_share;
	private TextView lock_praisePlus;
	private AccountDAO accountDAO;
	private DbAccount account;
	private PraiseDAO praiseDAO;
	private Animation appearAnimation;
	private Paper paper = new Paper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		setContentView(R.layout.activity_lockpaper);

		mStatusViewManager = new StatusViewManager(this, this.getApplicationContext());

		lock_linearlayout = (FrameLayout) this.findViewById(R.id.lock_linearlayout);
		lock_praise = (ImageView) this.findViewById(R.id.lock_praise);
		lock_share = (ImageView) this.findViewById(R.id.lock_share);
		lock_praisePlus = (TextView) this.findViewById(R.id.lock_praisePlus);
		
		accountDAO = new AccountDAO(this);
		praiseDAO = new PraiseDAO(this);
		appearAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);

		SlideBar slideToUnLock = (SlideBar) findViewById(R.id.slideBar);
		slideToUnLock.setOnTriggerListener(this);

		SharedPreferences lockpaper = getSharedPreferences("lockpaper", 0);
		String paperPath = lockpaper.getString("paper_path", "");
		int paper_Id = lockpaper.getInt("paper_id", 0);
		String paper_thumb = lockpaper.getString("paper_thumb", "");
		Drawable dw = Drawable.createFromPath(paperPath);
		if (dw != null && paper_Id != 0 && !paperPath.equals("") && lockpaper.getBoolean("is_create", false)) {
			paper.setId(paper_Id);
			paper.setMphoto(paper_thumb);
			lock_linearlayout.setBackgroundDrawable(dw);
			
			if (praiseDAO.isExist(paper_Id + "")) {
				lock_praise.setTag("已赞");
			} else {
				lock_praise.setTag("未赞");
			}
		} else {
			finish();
		}

		TelephonyManager phoneManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		// 手动注册对PhoneStateListener中的listen_call_state状态进行监听
		phoneManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					finish();
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					finish();
				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
		
		lock_praise.setOnClickListener(this);
		lock_share.setOnClickListener(this);
		ShareSDK.initSDK(this);
	}

	Handler praiseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AppConstants.HANDLER_MESSAGE_NORMAL:

				PraiseResult pr = (PraiseResult) msg.obj;
				if (pr.isResult()) {
					lock_praise.setTag("已赞");
					paper.setPraise(pr.getPraise_num());
					praiseDAO.save(paper, "lock");
					lock_praisePlus.startAnimation(appearAnimation);
					Toast.makeText(LockActivity.this, "成功", Toast.LENGTH_SHORT).show();
				} else {
					lock_praise.setTag("未赞");
				}
				break;
			case AppConstants.HANDLER_MESSAGE_NONETWORK:
				lock_praise.setTag("未赞");
				break;
			case AppConstants.HANDLER_HTTPSTATUS_ERROR:
				lock_praise.setTag("未赞");
				break;
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return disableKeycode(keyCode, event);
	}

	private boolean disableKeycode(int keyCode, KeyEvent event) {
		int key = event.getKeyCode();
		switch (key) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ShareSDK.stopSDK(this);
		if (DBG)
			Log.d(TAG, "onDestroy()");

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DBG)
			Log.d(TAG, "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DBG)
			Log.d(TAG, "onResume()");
	}

	@Override
	public void onTrigger() {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock_praise:
			if (v.getTag().equals("已赞")) {
				Toast.makeText(this, "您已经赞过了", Toast.LENGTH_SHORT).show();
				return;
			}
			account = accountDAO.getAccount();
			if (account == null) {
				Toast.makeText(this, "请先进入应用登录", Toast.LENGTH_SHORT).show();
			} else {
				lock_praise.setTag("已赞");
				PostPraiseBase praiseBase = new PostPraiseBase();
				praiseBase.setPaper_id(paper.getId());
				praiseBase.setPrivate_token(account.getToken());
				ThreadExecutor.execute(new PostData(this, praiseHandler, praiseBase, 1));
			}
			break;

		case R.id.lock_share:
			ShareTask shareTask = new ShareTask(this, paper, "lock");
			shareTask.execute();
			break;
		}
	}

}
