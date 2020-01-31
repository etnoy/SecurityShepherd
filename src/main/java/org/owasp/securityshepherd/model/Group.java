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
@Table("groups")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "GroupBuilder")
@With
public final class Group {

	@EqualsAndHashCode.Include
	@Id
	private final long id;

	@Builder.Default
	@NonNull
	private final String name = RandomStringUtils.randomAlphanumeric(20);

}