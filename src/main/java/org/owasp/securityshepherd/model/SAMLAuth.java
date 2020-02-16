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
public final class SAMLAuth {

	@NonNull
	private final String samlId;

	SAMLAuth(final String samlId) {
		this.samlId = samlId;
	}

}