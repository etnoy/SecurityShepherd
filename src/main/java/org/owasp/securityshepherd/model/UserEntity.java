package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

public class UserEntity {

	private String userId;
	private String classId;
	private String userName;
	private String userPass;
	private String userRole;
	private String ssoName;
	private int badLoginCount;
	private Timestamp suspendedUntil;
	private String userAddress;
	private String loginType;
	private boolean tempPassword;
	private boolean tempUsername;
	private int userScore;
	private int goldMedalCount;
	private int silverMedalCount;
	private int bronzeMedalCount;
	private int badSubmissionCount;

	public UserEntity(String userId, String classId, String userName, String userPass, String userRole, String ssoName,
			int badLoginCount, Timestamp suspendedUntil, String userAddress, String loginType, boolean tempPassword,
			boolean tempUsername, int userScore, int goldMedalCount, int silverMedalCount, int bronzeMedalCount,
			int badSubmissionCount) {

		setUserId(userId);
		this.classId = classId;
		this.userName = userName;
		this.userPass = userPass;
		this.userRole = userRole;
		this.ssoName = ssoName;
		this.badLoginCount = badLoginCount;
		this.suspendedUntil = suspendedUntil;
		this.userAddress = userAddress;
		this.loginType = loginType;
		this.tempPassword = tempPassword;
		this.tempUsername = tempUsername;
		this.userScore = userScore;
		this.goldMedalCount = goldMedalCount;
		this.silverMedalCount = silverMedalCount;
		this.bronzeMedalCount = bronzeMedalCount;
		this.badSubmissionCount = badSubmissionCount;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		UserEntity user = (UserEntity) obj;

		return (user.getUserId() == getUserId());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		if (userId.isEmpty()) {
			throw new IllegalArgumentException("userId cannot be empty");
		}
		this.userId = userId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getSsoName() {
		return ssoName;
	}

	public void setSsoName(String ssoName) {
		this.ssoName = ssoName;
	}

	public int getBadLoginCount() {
		return badLoginCount;
	}

	public void setBadLoginCount(int badLoginCount) {
		this.badLoginCount = badLoginCount;
	}

	public Timestamp getSuspendedUntil() {
		return suspendedUntil;
	}

	public void setSuspendedUntil(Timestamp suspendedUntil) {
		this.suspendedUntil = suspendedUntil;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public boolean isTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(boolean tempPassword) {
		this.tempPassword = tempPassword;
	}

	public boolean isTempUsername() {
		return tempUsername;
	}

	public void setTempUsername(boolean tempUsername) {
		this.tempUsername = tempUsername;
	}

	public int getUserScore() {
		return userScore;
	}

	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public int getGoldMedalCount() {
		return goldMedalCount;
	}

	public void setGoldMedalCount(int goldMedalCount) {
		this.goldMedalCount = goldMedalCount;
	}

	public int getSilverMedalCount() {
		return silverMedalCount;
	}

	public void setSilverMedalCount(int silverMedalCount) {
		this.silverMedalCount = silverMedalCount;
	}

	public int getBronzeMedalCount() {
		return bronzeMedalCount;
	}

	public void setBronzeMedalCount(int bronzeMedalCount) {
		this.bronzeMedalCount = bronzeMedalCount;
	}

	public int getBadSubmissionCount() {
		return badSubmissionCount;
	}

	public void setBadSubmissionCount(int badSubmissionCount) {
		this.badSubmissionCount = badSubmissionCount;
	}

}