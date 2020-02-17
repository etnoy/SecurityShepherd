package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3877275355721047824L;

	@Id
	private final int id;

	@NonNull
	@Column("config_key")
	private final String key;

	@NonNull
	private final String value;

	Configuration(final int id, final String key, final String value) {
		this.id = id;
		this.key = key;
		this.value = value;
	}

}