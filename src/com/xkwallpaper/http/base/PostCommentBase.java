package com.xkwallpaper.http.base;

import java.io.Serializable;

public class PostCommentBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private int paper_id;
	private int parent_id;
	private String content;
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

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
