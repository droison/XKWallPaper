package com.xkwallpaper.http.base;

import java.io.Serializable;

public class PostPraiseBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private int paper_id;
	private String private_token;

	public String getPrivate_token() {
		return private_token;
	}

	public void setPrivate_token(String private_token) {
		this.private_token = private_token;
	}

	public int getPaper_id() {
		return paper_id;
	}

	public void setPaper_id(int paper_id) {
		this.paper_id = paper_id;
	}

}
