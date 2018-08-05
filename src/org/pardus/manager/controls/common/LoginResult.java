package org.pardus.manager.controls.common;

public class LoginResult {
	boolean modalResult;
	String userName;
	String password;

	public LoginResult(String userName, String password) {
		this.modalResult = true;
		this.userName = userName;
		this.password = password;
	}

	public LoginResult(boolean modalResult) {
		this.modalResult = modalResult;
	}

	public boolean isModalResult() {
		return modalResult;
	}

	public void setModalResult(boolean modalResult) {
		this.modalResult = modalResult;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginResult [modalResult=" + modalResult + ", userName=" + userName + ", password=" + password + "]";
	}
	
	

}
