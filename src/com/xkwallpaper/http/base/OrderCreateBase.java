package com.xkwallpaper.http.base;

import java.io.Serializable;

public class OrderCreateBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private String private_token;
	private Temp order;

	public OrderCreateBase(String private_token,int paper_id) {
		this.order = new Temp(paper_id);
		this.private_token = private_token;
	}
	public Temp getOrder() {
		return order;
	}

	public void setOrder(Temp order) {
		this.order = order;
	}

	public String getPrivate_token() {
		return private_token;
	}

	public void setPrivate_token(String private_token) {
		this.private_token = private_token;
	}

	class Temp {
		public Temp(int paper_id) {
			this.paper_id = paper_id;
		}
		private int paper_id;

		public int getPaper_id() {
			return paper_id;
		}

		public void setPaper_id(int paper_id) {
			this.paper_id = paper_id;
		}

	}

}
