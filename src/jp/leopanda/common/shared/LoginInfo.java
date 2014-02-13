package jp.leopanda.common.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean loggedIn = false;

	private String loginUrl;

	private String logoutUrl;

	private String emailAddress;

	private String nickname;

	private String pictureUrl;

	private String userID;
	

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(final boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(final String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(final String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getName() {
		return nickname;
	}

	public void setName(final String nickname) {
		this.nickname = nickname;
	}

	public void setPictureUrl(final String pictureUrl) {
		this.pictureUrl = pictureUrl;

	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(final String userID) {
		this.userID = userID;
	}



}
