package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@EqualsAndHashCode
@Builder
@With
public final class AuthData {

	@Builder.Default
	private final boolean isEnabled = false;

	private final int badLoginCount;

	private final Timestamp suspendedUntil;

	private final Timestamp lastLogin;

	private final PasswordData password;

	private final SAMLData saml;

}