package org.owasp.securityshepherd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Table("class")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class ClassEntity {
	
	@EqualsAndHashCode.Include
	@Id
	private final int id;

	@NonNull
	private final String name;
	
	ClassEntity(final int id, final String name) {
		this.id=id;
		this.name=name;
	}

}