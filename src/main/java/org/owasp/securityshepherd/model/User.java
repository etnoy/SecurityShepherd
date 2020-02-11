package org.owasp.securityshepherd.model;

import org.springframework.data.annotation.Id;
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

	@Builder.Default
	private final Integer classId = null;

	@NonNull
	private final String displayName;

	private final String email;

	private final Auth auth;

}