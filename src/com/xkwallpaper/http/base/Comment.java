package com.xkwallpaper.http.base;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private int id;
	private String content;
	private String face;
	private String username;
	private int parent_id;
	private Date time;
	private String parent_username;
	private String sphoto;

	public String getSphoto() {
		return sphoto;
	}

	public void setSphoto(String sphoto) {
		this.sphoto = sphoto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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

	public String getParent_username() {
		return parent_username;
	}

	public void setParent_username(String parent_username) {
		this.parent_username = parent_username;
	}

}
