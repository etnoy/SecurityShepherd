package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.owasp.securityshepherd.utils.Hash;
import org.springframework.data.annotation.Id;

@Data
@Builder(builderClassName = "UserBuilder")
public class User {

	@EqualsAndHashCode.Include
	@Id
	private final String Id;

	@Builder.Default
	String classId = null;

	private final String name;

	private final String password = null;

	@Builder.Default
	@NonNull
	String role = "player";
	@Builder.Default
	String ssoId = null;

	@Builder.Default
	int badLoginCount = 0;

	@Builder.Default
	@NonNull
	Timestamp suspendedUntil = new Timestamp(0);

	@Builder.Default
	String email = null;
	@Builder.Default
	@NonNull
	String loginType = "login";
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

	public static class UserBuilder {
		public UserBuilder id(String id) {
			if (id == null) {
				id = Hash.randomString();
			}

			if (id.isEmpty()) {
				throw new IllegalArgumentException("User Id cannot be empty");
			}

			if (!StringUtils.isAlphanumeric(id)) {
				throw new IllegalArgumentException("User Id must be alphanumeric");
			}

			return this;
		}

		public UserBuilder name(String name) {
			if (name == null) {
				name = Hash.randomString();
			}

			if (name.isEmpty()) {
				throw new IllegalArgumentException("User name cannot be empty");
			}

			return this;
		}
		
		public UserBuilder password(String password) {

			if (password.isEmpty()) {
				throw new IllegalArgumentException("Password cannot be empty");
			}

			return this;
		}

	}

}