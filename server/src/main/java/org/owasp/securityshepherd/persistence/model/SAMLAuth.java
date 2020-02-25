package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class SAMLAuth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 211951930649985921L;
	
	@EqualsAndHashCode.Include
	@Id
	private final int id;
	
	@NonNull
	private final String samlId;

}