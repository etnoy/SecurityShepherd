package org.owasp.securityshepherd.model;

import java.io.Serializable;
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
public final class Submission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5485881248601955741L;

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