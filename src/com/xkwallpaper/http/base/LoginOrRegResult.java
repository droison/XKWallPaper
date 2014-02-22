package com.xkwallpaper.http.base;

public class LoginOrRegResult {
	public static class Login {
		private boolean result;
		private String private_token;
		private String message;
		private String face;
		private String username;
		private String phone;
		private boolean weibo;
		private boolean qq;

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

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public boolean isWeibo() {
			return weibo;
		}

		public void setWeibo(boolean weibo) {
			this.weibo = weibo;
		}

		public boolean isQq() {
			return qq;
		}

		public void setQq(boolean qq) {
			this.qq = qq;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

		public String getPrivate_token() {
			return private_token;
		}

		public void setPrivate_token(String private_token) {
			this.private_token = private_token;
		}

	}

	public static class PhoneVerify {

		private boolean result;
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

	}

	public static class Reg {
		private boolean result;
		private String private_token;
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

		public String getPrivate_token() {
			return private_token;
		}

		public void setPrivate_token(String private_token) {
			this.private_token = private_token;
		}

	}
}
