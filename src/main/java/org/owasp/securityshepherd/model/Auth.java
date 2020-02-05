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
public final class Auth {

	@Builder.Default
	private final boolean isEnabled = false;

	private final int badLoginCount = 0;

	private final boolean isAdmin = false;

	private final Timestamp suspendedUntil;

	private final String suspensionMessage = null;

	private final Timestamp lastLogin;

	private final PasswordData password;

	private final SAMLData saml;
	
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