package com.xkwallpaper.http.base;

import java.io.Serializable;

public class SocialBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private int type;// （社交平台类型，1代表微博，2代表qq）
	private String uid;// （用户id）；
	private String face;// （头像）；
	private String username;// （用户名）

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
