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

}