package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class Submission {

	@EqualsAndHashCode.Include
	@Id
	private final int id;

	private final int userId;

	private final int moduleId;

	@NonNull
	private final Timestamp time;

	@Builder.Default
	private final boolean isValid = false;

	private final String flag;

}