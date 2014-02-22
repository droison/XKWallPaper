package com.xkwallpaper.http.base;

public class InfoUpdateBase {
	private Temp user;
	private String private_token;

	public Temp getUser() {
		return user;
	}

	public void setUser(Temp user) {
		this.user = user;
	}

	public String getPrivate_token() {
		return private_token;
	}

	public void setPrivate_token(String private_token) {
		this.private_token = private_token;
	}

	public InfoUpdateBase(String username, String password, String phone) {
		user = new Temp(username, password, phone);
	}

	class Temp {
		private String username;
		private String password;
		private String phone;

		public Temp(String username, String password, String phone) {
			this.username = username;
			this.password = password;
			this.phone = phone;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

	}
}
