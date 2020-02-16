package org.owasp.securityshepherd.model;

import lombok.Builder;
import lombok.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class PasswordAuth {

	@NonNull
	private final String loginName;
	
	private final String hashedPassword;

	@Builder.Default
	private final boolean isPasswordExpired = true;

}
