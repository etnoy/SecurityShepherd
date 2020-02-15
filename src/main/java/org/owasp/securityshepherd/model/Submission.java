package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class Submission {

	@EqualsAndHashCode.Include
	@Id
	private final int id;

	@NonNull
	@Builder.Default
	private final Integer userId = null;

	@NonNull
	@Builder.Default
	private final Integer moduleId = null;

	@NonNull
	@Builder.Default
	private final Timestamp time = new Timestamp(System.currentTimeMillis());

	@Builder.Default
	private final boolean isValid = false;

	@NonNull
	private final String submittedFlag;

}