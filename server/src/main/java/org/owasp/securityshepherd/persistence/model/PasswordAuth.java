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
public final class PasswordAuth implements Serializable {

	private static final long serialVersionUID = 32553442956391684L;

	@EqualsAndHashCode.Include
	@Id
	private final int id;
	
	private final int user;

	@NonNull
	private final String loginName;

	private final String hashedPassword;

	@Builder.Default
	private final boolean isPasswordNonExpired = false;

}
