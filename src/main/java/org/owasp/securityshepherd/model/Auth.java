package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class Auth {

	@Builder.Default
	private final boolean isEnabled = false;

	@Builder.Default
	private final int badLoginCount = 0;

	@Builder.Default
	private final boolean isAdmin = false;

	@Builder.Default
	private final Timestamp suspendedUntil = null;
	
	private final String suspensionMessage;

	@Builder.Default
	private final Timestamp accountCreated = null;

	@Builder.Default
	private final Timestamp lastLogin = null;
	
	private final String lastLoginMethod;

	private final PasswordAuth password;

	private final SAMLAuth saml;
	
	public boolean isAccountSuspended() {
		if(suspendedUntil == null) {
			return false;
		} else if (suspendedUntil.getTime() <= System.currentTimeMillis()) {
			return false;
		} else {
			return true;
		}
		
	}

}