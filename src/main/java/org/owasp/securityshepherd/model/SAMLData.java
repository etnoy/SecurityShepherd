package org.owasp.securityshepherd.model;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@EqualsAndHashCode
@Table("auth_data_saml")
@Builder(builderClassName = "UserAuthSAMLBuilder")
@With
public final class SAMLData {

	private final String samlId;
	
}
