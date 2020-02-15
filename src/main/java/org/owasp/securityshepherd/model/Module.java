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
public final class Module {

	@EqualsAndHashCode.Include
	@Id
	private final int id;

	@NonNull
	private final String name;

	private final String description;

	@Builder.Default
	private final boolean flagEnabled = false;

	@Builder.Default
	private final boolean exactFlag = false;

	private final String flag;
	
	@Builder.Default
	private final boolean isOpen = false;

}