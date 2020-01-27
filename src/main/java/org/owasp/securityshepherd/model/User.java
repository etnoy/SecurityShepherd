package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "UserBuilder")
public class User {

	@EqualsAndHashCode.Include
	@Id
	@Builder.Default
	@NonNull
	private final String id = RandomStringUtils.randomAlphanumeric(20);

	String classId;

	@Builder.Default
	@NonNull
	private final String name = RandomStringUtils.randomAlphanumeric(20);

	@Builder.Default
	private final String password = null;

	@Builder.Default
	@NonNull
	private String role = "player";

	@Builder.Default
	Timestamp suspendedUntil = null;

	@Builder.Default
	String email = null;

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
	int goldMedals = 0;
	@Builder.Default
	int silverMedals = 0;
	@Builder.Default
	int bronzeMedals = 0;

	@Builder.Default
	int badSubmissionCount = 0;
	@Builder.Default
	int badLoginCount = 0;

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
		public UserBuilder classId(String classId) {

			if (classId != null && classId.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.classId(classId);
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
		public UserBuilder role(String role) {

			validateRole(role);

			return super.role(role);
		}

		@Override
		public UserBuilder goldMedals(int goldMedals) {

			if (goldMedals < 0) {
				throw new IllegalArgumentException("Gold medal count must be a positive integer");
			}
			return super.goldMedals(goldMedals);
		}

		@Override
		public UserBuilder silverMedals(int silverMedals) {

			if (silverMedals < 0) {
				throw new IllegalArgumentException("Silver medal count must be a positive integer");
			}
			return super.silverMedals(silverMedals);
		}

		@Override
		public UserBuilder bronzeMedals(int bronzeMedals) {

			if (bronzeMedals < 0) {
				throw new IllegalArgumentException("Bronze medal count must be a positive integer");
			}
			return super.bronzeMedals(bronzeMedals);
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

	public void setGoldMedals(int goldMedals) {
		if (goldMedals < 0) {
			throw new IllegalArgumentException("Gold medal count must be a positive integer");
		}

		this.goldMedals = goldMedals;
	}

	public void setSilverMedals(int silverMedals) {
		if (silverMedals < 0) {
			throw new IllegalArgumentException("Silver medal count must be a positive integer");
		}

		this.silverMedals = silverMedals;
	}

	public void setBronzeMedals(int bronzeMedals) {
		if (bronzeMedals < 0) {
			throw new IllegalArgumentException("Bronze medal count must be a positive integer");
		}

		this.bronzeMedals = bronzeMedals;
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

	public void setRole(String role) {
		validateRole(role);

		this.role = role;
	}

	public void setClassId(String classId) {
		validateId(classId);

		this.classId = classId;
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

	public static boolean validateRole(String role) {
		if (role.equals("player") || role.equals("admin")) {
			return true;
		} else {
			throw new IllegalArgumentException("User role must be \"player\" or \"admin\"");
		}

	}

}