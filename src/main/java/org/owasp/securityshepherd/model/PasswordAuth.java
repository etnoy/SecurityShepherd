package org.owasp.securityshepherd.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class PasswordAuth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 32553442956391684L;

	@NonNull
	private final String loginName;

	private final String hashedPassword;

	@Builder.Default
	private final boolean isPasswordNonExpired = false;

	PasswordAuth(final String loginName, final String hashedPassword, final boolean isPasswordNonExpired) {
		this.loginName = loginName;
		this.hashedPassword = hashedPassword;
		this.isPasswordNonExpired = isPasswordNonExpired;
	}

}
