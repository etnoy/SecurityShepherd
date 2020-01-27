package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "UserBuilder", buildMethodName = "build")
public class User {

	@EqualsAndHashCode.Include
	@Id
	@Builder.Default
	@NonNull
	@With
	private final String id = RandomStringUtils.randomAlphanumeric(20);

	String classId;

	@Builder.Default
	@NonNull
	private final String name = RandomStringUtils.randomAlphanumeric(20);

	private final String password;

	@Builder.Default
	@NonNull
	String role = "player";

	// TODO: ssoId should be deprecated and merged into id
	String ssoId;

	@Builder.Default
	int badLoginCount = 0;

	@Builder.Default
	Timestamp suspendedUntil = null;

	String email;

	@Builder.Default
	@NonNull
	final String loginType = "login";

	@Builder.Default
	boolean temporaryPassword = false;
	@Builder.Default
	boolean temporaryUsername = false;
	@Builder.Default
	int score = 0;
	@Builder.Default
	int goldMedalCount = 0;
	@Builder.Default
	int silverMedalCount = 0;
	@Builder.Default
	int bronzeMedalCount = 0;
	@Builder.Default
	int badSubmissionCount = 0;

	public static UserBuilder builder() {
		return new ValidatingUserBuilder();
	}

	// This class adds validation logic to the builder
	private static class ValidatingUserBuilder extends UserBuilder {

		@Override
		public UserBuilder id(String id) {
			if (id == null) {
				id = RandomStringUtils.randomAlphanumeric(20);
			} else {
				validateId(id);
			}
			return super.id(id);
		}

		@Override
		public UserBuilder name(String name) {
			if (name == null) {
				name = RandomStringUtils.randomAlphanumeric(20);
			} else {
				validateName(name);
			}
			return super.name(name);
		}

		@Override
		public UserBuilder ssoId(String ssoId) {
			if (ssoId != null && ssoId.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.ssoId(ssoId);
		}

		@Override
		public UserBuilder classId(String classId) {

			if (classId != null && classId.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.classId(classId);
		}

		@Override
		public UserBuilder goldMedalCount(int goldMedalCount) {

			if (goldMedalCount < 0) {
				throw new IllegalArgumentException("Gold medal count must be a positive integer");
			}
			return super.goldMedalCount(goldMedalCount);
		}

		@Override
		public UserBuilder silverMedalCount(int silverMedalCount) {

			if (silverMedalCount < 0) {
				throw new IllegalArgumentException("Silver medal count must be a positive integer");
			}
			return super.silverMedalCount(silverMedalCount);
		}

		@Override
		public UserBuilder bronzeMedalCount(int bronzeMedalCount) {

			if (bronzeMedalCount < 0) {
				throw new IllegalArgumentException("Bronze medal count must be a positive integer");
			}
			return super.bronzeMedalCount(bronzeMedalCount);
		}
		
		@Override
		public UserBuilder badSubmissionCount(int badSubmissionCount) {

			if (badSubmissionCount < 0) {
				throw new IllegalArgumentException("Bad submission count must be a positive integer");
			}
			return super.badSubmissionCount(badSubmissionCount);
		}
		
		@Override
		public UserBuilder badLoginCount(int badLoginCount) {

			if (badLoginCount < 0) {
				throw new IllegalArgumentException("Bad login count must be a positive integer");
			}
			return super.badLoginCount(badLoginCount);
		}

	}

	public void setGoldMedalCount(int goldMedalCount) {
		if (goldMedalCount < 0) {
			throw new IllegalArgumentException("Gold medal count must be a positive integer");
		}

		this.goldMedalCount = goldMedalCount;
	}

	public void setSilverMedalCount(int silverMedalCount) {
		if (silverMedalCount < 0) {
			throw new IllegalArgumentException("Silver medal count must be a positive integer");
		}

		this.silverMedalCount = silverMedalCount;
	}

	public void setBronzeMedalCount(int bronzeMedalCount) {
		if (bronzeMedalCount < 0) {
			throw new IllegalArgumentException("Bronze medal count must be a positive integer");
		}

		this.bronzeMedalCount = bronzeMedalCount;
	}
	
	public void setBadSubmissionCount(int badSubmissionCount) {
		if (badSubmissionCount < 0) {
			throw new IllegalArgumentException("Bad submission count must be a positive integer");
		}

		this.badSubmissionCount = badSubmissionCount;
	}
	
	public void setBadLoginCount(int badLoginCount) {
		if (badLoginCount < 0) {
			throw new IllegalArgumentException("Bad login count must be a positive integer");
		}

		this.badLoginCount = badLoginCount;
	}
	
	public static boolean validateId(String id) {
		if (id.isEmpty()) {
			throw new IllegalArgumentException("User id cannot be an empty string");
		}

		return true;
	}

	public static boolean validateName(String name) {
		if (name.isEmpty()) {
			throw new IllegalArgumentException("User name cannot be an empty string");
		}

		return true;
	}

}