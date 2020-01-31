package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@Table("users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "UserAuthBuilder")
@With
public final class UserAuth {

	@Builder.Default
	private final boolean isEnabled = false;
	
	@Builder.Default
	private final Timestamp suspendedUntil = new Timestamp(0);	
	
}