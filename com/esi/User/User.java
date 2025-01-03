package com.esi.User;


public class User {
	private String Username;
	private String Password;
	
	public User(String Username,String Password) {
		this.setPassword(Password);
		this.setUsername(Username);
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

}
