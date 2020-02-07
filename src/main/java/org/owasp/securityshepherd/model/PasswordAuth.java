package org.owasp.securityshepherd.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
@EqualsAndHashCode
@Builder
@With
public final class PasswordAuth {

	@NonNull
	private final String loginName;
	
	private final String hashedPassword;

	@Builder.Default
	private final boolean passwordExpired = true;

}
