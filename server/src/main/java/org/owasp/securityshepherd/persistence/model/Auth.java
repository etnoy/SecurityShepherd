package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class Auth implements Serializable {

	private static final long serialVersionUID = -1511082836956413928L;

	@EqualsAndHashCode.Include
	@Id
	private final int id;

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

}