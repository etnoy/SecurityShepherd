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
	
	private final User user;
	
	private final Module module;

	@NonNull
	private final Timestamp time;
	
	private final boolean valid;
	
	@NonNull
	private final String submittedFlag;

}