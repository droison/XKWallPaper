package com.xkwallpaper.receiver;

import com.xkwallpaper.lockpaper.LockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		SharedPreferences lockpaper = arg0.getSharedPreferences("lockpaper", 0);
		if (lockpaper.getBoolean("is_create", false)) {
			LockService.startLockService(arg0);
		}
	}

}
