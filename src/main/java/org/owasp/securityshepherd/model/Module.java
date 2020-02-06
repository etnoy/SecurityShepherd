package org.owasp.securityshepherd.model;

import org.owasp.securityshepherd.service.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

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

	private boolean hasFlag;
	
	private boolean hardcodedFlag;

	private final String flag;

}