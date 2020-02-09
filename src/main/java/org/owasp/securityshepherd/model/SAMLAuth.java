package org.owasp.securityshepherd.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@EqualsAndHashCode
@Builder
@With
public final class SAMLAuth {

	private final String samlId;
	
}
