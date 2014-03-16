package com.xkwallpaper.lockpaper;

import java.util.List;

import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.util.ExitApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockService extends Service {

	private static String TAG = "LockService";
	private static Intent lockIntent;
	private static Intent serviceIntentInstance;
	private static boolean isServiceStart = false;
	private boolean phoneInUse = false;
	private KeyguardManager mKeyguardManager;
	private KeyguardLock mKeyguardLock;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void startLockService(Context mContext) {
		if (serviceIntentInstance == null) {
			serviceIntentInstance = new Intent(mContext, LockService.class);
			serviceIntentInstance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			lockIntent = new Intent(mContext, LockActivity.class);
			lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			mContext.startService(serviceIntentInstance);
		}
		isServiceStart = true;
	}

	public static void stopLockService(Context mContext) {
		isServiceStart = false;
		if (serviceIntentInstance != null) {
			mContext.stopService(serviceIntentInstance);
			serviceIntentInstance = null;
		}

	}

	public static boolean isServiceRunning() {
		return isServiceStart;
	}

	public void onCreate() {
		super.onCreate();

		IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
		LockService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);

		IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		LockService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_REDELIVER_INTENT; // 表示在被异常KILL后，重传Intent并重启Service

	}

	public void onDestroy() {
		super.onDestroy();
		LockService.this.unregisterReceiver(mScreenOnReceiver);
//		LockService.this.unregisterReceiver(mScreenOffReceiver);
		if (isServiceStart) {
			startService(serviceIntentInstance);
		}
	}

	private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences lockpaper = getSharedPreferences("lockpaper", 0);
			String paperPath = lockpaper.getString("paper_path", "");
			int paper_Id = lockpaper.getInt("paper_id", 0);
			Drawable dw = Drawable.createFromPath(paperPath);
			if (dw != null && paper_Id != 0 && !paperPath.equals("") && lockpaper.getBoolean("is_create", false)) {

				if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
					// mKeyguardManager = (KeyguardManager)
					// context.getSystemService(Context.KEYGUARD_SERVICE);
					// mKeyguardLock =
					// mKeyguardManager.newKeyguardLock("LockIntent");
					// mKeyguardLock.disableKeyguard();
					if (lockIntent == null) {
						lockIntent = new Intent(context, LockActivity.class);
						lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(lockIntent);
					}
				}

			}
		}

	};

	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences lockpaper = getSharedPreferences("lockpaper", 0);
			String paperPath = lockpaper.getString("paper_path", "");
			int paper_Id = lockpaper.getInt("paper_id", 0);
			Drawable dw = Drawable.createFromPath(paperPath);
			if (dw != null && paper_Id != 0 && !paperPath.equals("") && lockpaper.getBoolean("is_create", false)) {
				String action = intent.getAction();

				if (action.equals("android.intent.action.SCREEN_OFF")) {
//					mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//					mKeyguardLock = mKeyguardManager.newKeyguardLock("LockIntent");
//					mKeyguardLock.disableKeyguard();
					if (lockIntent == null) {
						lockIntent = new Intent(context, LockActivity.class);
						lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					if (!isTopActivity()) {
						ExitApplication.getInstance().exit();
					}
					startActivity(lockIntent);
				}
			}

		}

	};

	private boolean isTopActivity() {
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if ("com.xkwallpaper.ui".equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
