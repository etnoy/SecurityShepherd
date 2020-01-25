package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.owasp.securityshepherd.utils.Hash;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserEntity {

	@Builder.Default
	@EqualsAndHashCode.Include
	private @Id String userId = Hash.randomString();
	@Builder.Default
	private String classId = null;

	@Builder.Default
	private @NonNull String userName = Hash.randomString();

	@Builder.Default
	private String userPass = null;

	@Builder.Default
	private @NonNull String userRole = "player";
	@Builder.Default
	private String ssoName = null;

	@Builder.Default
	private int badLoginCount = 0;

	@Builder.Default
	private @NonNull Timestamp suspendedUntil = new Timestamp(0);

	@Builder.Default
	private String userAddress = null;
	@Builder.Default
	private @NonNull String loginType = "login";
	@Builder.Default
	private boolean tempPassword = false;
	@Builder.Default
	private boolean tempUsername = false;
	@Builder.Default
	private int userScore = 0;
	@Builder.Default
	private int goldMedalCount = 0;
	@Builder.Default
	private int silverMedalCount = 0;
	@Builder.Default
	private int bronzeMedalCount = 0;
	@Builder.Default
	private int badSubmissionCount = 0;

}