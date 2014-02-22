package com.xkwallpaper.db;

import java.io.Serializable;

public class DbSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9037134976784830854L;
	private int id;
	private String key;
	private int type;
	private long time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
