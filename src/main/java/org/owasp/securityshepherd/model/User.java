package org.owasp.securityshepherd.model;

import org.owasp.securityshepherd.service.FlagService;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
@Table("users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class User {

	@EqualsAndHashCode.Include
	@Id
	private final long id;

	@NonNull
	private final String name;

	private final String classId;

	private final String email;

	private final Auth auth;

	@NonNull
	@Builder.Default
	private final byte[] flagKey = FlagService.generateFlagKey();

}