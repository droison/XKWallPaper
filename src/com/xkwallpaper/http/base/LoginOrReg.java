package com.xkwallpaper.http.base;

public class LoginOrReg {
	public static class Reg {
		private Temp user;

		public Temp getUser() {
			return user;
		}

		public void setUser(Temp user) {
			this.user = user;
		}

		public Reg(String username, String password, String password_confirmation, String phone, String verfy) {
			user = new Temp(username, password, password_confirmation, phone, verfy);
		}

		class Temp {
			public Temp(String username, String password, String password_confirmation, String phone, String verfy) {
				this.username = username;
				this.password = password;
				this.password_confirmation = password_confirmation;
				this.phone = phone;
				this.verfy = verfy;
			}

			private String username;
			private String password;
			private String password_confirmation;
			private String phone;
			private String verfy;

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

			public String getPassword_confirmation() {
				return password_confirmation;
			}

			public void setPassword_confirmation(String password_confirmation) {
				this.password_confirmation = password_confirmation;
			}

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}

			public String getVerfy() {
				return verfy;
			}

			public void setVerfy(String verfy) {
				this.verfy = verfy;
			}
		}

	}

	public static class PhoneVerify {
		private Temp user;

		public Temp getUser() {
			return user;
		}

		public void setUser(Temp user) {
			this.user = user;
		}

		public PhoneVerify(String phone) {
			user = new Temp();
			user.setPhone(phone);
		}

		class Temp {

			private String phone;

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}
		}
	}

	public static class Login {
		private Temp user;

		public Temp getUser() {
			return user;
		}

		public void setUser(Temp user) {
			this.user = user;
		}
		
		public Login(String phone, String password) {
			this.user = new Temp(phone, password);
		}

		class Temp {
			private String phone;
			private String password;

			public String getPhone() {
				return phone;
			}

			public void setPhone(String phone) {
				this.phone = phone;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}
			
			public Temp(String phone, String password) {
				this.password =password;
				this.phone = phone;
			}
		}

	}
}
