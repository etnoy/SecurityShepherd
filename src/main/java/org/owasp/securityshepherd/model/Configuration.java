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
public final class Configuration {

	@EqualsAndHashCode.Include
	@Id
	private final int id;
	
	@NonNull
	@Column("config_key")
	private final String key;
	
	@NonNull
	private final String value;

}