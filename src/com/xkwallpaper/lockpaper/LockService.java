package com.xkwallpaper.lockpaper;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockService extends Service {

	private static String TAG = "LockService";
	private static Intent lockIntent;
	private static Intent serviceIntentInstance;
	private static boolean isServiceStart = false;
	private KeyguardManager mKeyguardManager = null;
	private KeyguardManager.KeyguardLock mKeyguardLock = null;
	private TelephonyManager phoneManager ;
private boolean phoneInUse = false;
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

		phoneManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        // 手动注册对PhoneStateListener中的listen_call_state状态进行监听
		phoneManager.listen(new PhoneStateListener(){
			 @Override
		        public void onCallStateChanged(int state, String incomingNumber) {
		            switch (state) {
		            case TelephonyManager.CALL_STATE_IDLE:
		            	phoneInUse = false;
		                break;
		            case TelephonyManager.CALL_STATE_RINGING:
		            	phoneInUse = true;
		                break;
		            case TelephonyManager.CALL_STATE_OFFHOOK:
		            	phoneInUse = false;
		            default:
		                break;
		            }
		            super.onCallStateChanged(state, incomingNumber);
		        }
		}, PhoneStateListener.LISTEN_CALL_STATE);
        
		
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
		LockService.this.unregisterReceiver(mScreenOffReceiver);
		if(isServiceStart){
			startService(serviceIntentInstance);
		}
	}

	private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.SCREEN_ON")&&!phoneInUse) {
				mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("LockIntent");
				mKeyguardLock.disableKeyguard();
				startActivity(lockIntent);
			}
		}

	};

	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals("android.intent.action.SCREEN_OFF")&&!phoneInUse) {
				mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("LockIntent");
				mKeyguardLock.disableKeyguard();
				startActivity(lockIntent);
			}
		}

	};
}
