package org.owasp.securityshepherd.model;

import org.owasp.securityshepherd.service.FlagHandlingService;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
import lombok.AccessLevel;

@Data
@Table("modules")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class Module {

	@EqualsAndHashCode.Include
	@Id
	private final long id;

	@NonNull
	private final String name;

	private final String description;

	private final String shortName;

	@NonNull
	private final byte[] solutionKey = FlagHandlingService.generateRandomBytes(16);

	private final boolean fixedSolutionKey = false;


}