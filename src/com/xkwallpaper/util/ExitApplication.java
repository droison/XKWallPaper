package com.xkwallpaper.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ExitApplication extends Application {

	private List<Activity> registerList = new LinkedList<Activity>();
	private static ExitApplication instance;

	private ExitApplication() {

	}

	public static ExitApplication getInstance() {
		if (null == instance) {
			instance = new ExitApplication();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		registerList.add(activity);
	}

	public void exit() {
		for (Activity activity : registerList) {
			activity.finish();
		}
	}
	
}