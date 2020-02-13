package org.owasp.securityshepherd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class User {

	@EqualsAndHashCode.Include
	@Id
	private final int id;

	@NonNull
	private final String displayName;
	
	@Builder.Default
	private final Integer classId = null;

	private final String email;

	@Column("user_key")
	private final byte[] key;
	
	private final Auth auth;

}