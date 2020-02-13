package org.owasp.securityshepherd.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
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

}