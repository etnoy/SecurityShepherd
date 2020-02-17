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
public final class SAMLAuth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 211951930649985921L;
	@NonNull
	private final String samlId;

	SAMLAuth(final String samlId) {
		this.samlId = samlId;
	}

}