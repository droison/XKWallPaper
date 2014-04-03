/**
 * 监听开机启动的Receiver
 */
package com.xkwallpaper.ui;

import com.xkwallpaper.lockpaper.LockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	static final String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(action_boot)) {
			SharedPreferences lockpaper = context.getSharedPreferences("lockpaper", 0);
			if (lockpaper.getBoolean("is_create", false)) {
				LockService.startLockService(context);
			}
		}

	}

}
