package org.owasp.securityshepherd.model;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Data
@Table("submissions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class Submission {

	@EqualsAndHashCode.Include
	@Id
	private final long id;

	@NonNull
	@Builder.Default
	private final Long userId = null;

	@NonNull
	@Builder.Default
	private final Long moduleId = null;

	@NonNull
	@Builder.Default
	private final Timestamp time = new Timestamp(System.currentTimeMillis());

	@Builder.Default
	private final boolean valid = false;

	@NonNull
	private final String submittedFlag;

}