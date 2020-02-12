package org.owasp.securityshepherd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
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
	private boolean flagEnabled = false;

	@Builder.Default
	private boolean exactFlag = false;

	private final String flag;

	@Column("module_key")
	private final byte[] key;
	
	@Builder.Default
	private final boolean isOpen = false;

}