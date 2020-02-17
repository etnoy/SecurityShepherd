package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@With
public final class Module implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6391362512222766270L;

	@EqualsAndHashCode.Include
	@Id
	private final int id;

	@NonNull
	private final String name;

	private final String description;

	@Builder.Default
	private final boolean isFlagEnabled = false;

	@Builder.Default
	private final boolean isExactFlag = false;

	private final String flag;

	@Builder.Default
	private final boolean isOpen = false;

}