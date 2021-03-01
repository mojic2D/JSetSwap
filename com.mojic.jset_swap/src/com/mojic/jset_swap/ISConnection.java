package com.mojic.jset_swap; 

public class ISConnection {
	private String connection;
	private String username;
	private String password;
	private String jdbc;

	ISConnection(String jdbc,String connection, String username, String password) {
		this.connection = connection;
		this.username = username;
		this.password = password;
		this.jdbc=jdbc;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
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

	public String getJdbc() {
		return jdbc;
	}

	public void setJdbc(String jdbc) {
		this.jdbc = jdbc;
	}

	
	
}
