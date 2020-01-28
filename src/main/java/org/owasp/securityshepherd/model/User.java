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

	@Builder.Default
	@NonNull
	private String name = RandomStringUtils.randomAlphanumeric(20);

	String classId;

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

	static final int maxIdLength = 30;
	static final int maxNameLength = 70;

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
			if (classId != null) {
				validateId(classId);
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

	public void setEmail(String email) {
		validateEmail(email);

		this.email = email;
	}

	public void setRole(String role) {
		validateRole(role);

		this.role = role;
	}

	public void setClassId(String classId) {
		validateId(classId);

		this.classId = classId;
	}

	public void setName(String name) {
		validateName(name);

		this.name = name;
	}

	public static boolean validateId(String id) {
		if (id == null) {
			throw new IllegalArgumentException("User id cannot be null");
		} else if (id.isEmpty()) {
			throw new IllegalArgumentException("User id cannot be an empty string");
		} else if (id.length() > maxIdLength) {
			throw new IllegalArgumentException("User id cannot be longer than " + Integer.toString(maxIdLength));
		}

		return true;
	}

	public static boolean validateName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("User name cannot be null");
		} else if (name.isEmpty()) {
			throw new IllegalArgumentException("User name cannot be an empty string");
		} else if (name.length() > maxNameLength) {
			throw new IllegalArgumentException("User name cannot be longer than " + Integer.toString(maxNameLength));
		}

		return true;
	}

	public static boolean validateRole(String role) {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null");
		} else if (role.equals("player") || role.equals("admin")) {
			return true;
		} else {
			throw new IllegalArgumentException("User role must be \"player\" or \"admin\"");
		}
	}

	public static boolean validateEmail(String email) {
		if (email == null) {
			throw new IllegalArgumentException("Email cannot be null");
		} else {
			return true;
		}
	}

}