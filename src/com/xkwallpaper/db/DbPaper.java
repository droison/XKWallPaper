package com.xkwallpaper.db;

import java.io.Serializable;

import com.xkwallpaper.http.base.Paper;

public class DbPaper extends Paper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9037134976784830854L;
	private String dir;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}
