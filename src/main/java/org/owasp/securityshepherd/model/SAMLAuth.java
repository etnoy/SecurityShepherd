package org.owasp.securityshepherd.model;

import org.springframework.data.relational.core.mapping.Table;

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
