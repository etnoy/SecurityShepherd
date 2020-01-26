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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "UserBuilder", buildMethodName = "build")
public class User {

	@EqualsAndHashCode.Include
	@Id
	@Builder.Default
	@NonNull
	private final String id = Hash.randomString();

	@Builder.Default
	String classId = null;

	@Builder.Default
	@NonNull
	private final String name = Hash.randomString();

	@Builder.Default
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

	/**
	 * 
	 */
	public static UserBuilder builder() {
		return new ValidatingUserBuilder();
	}

	// This class adds validation logic to the builder
	private static class ValidatingUserBuilder extends UserBuilder {

		@Override
		public UserBuilder id(String id) {
			if (id.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.id(id);
		}

		@Override
		public UserBuilder ssoId(String ssoId) {
			if (ssoId.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.ssoId(ssoId);
		}
		
		@Override
		public UserBuilder classId(String classId) {
			if (classId.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return super.classId(classId);
		}

	}

}