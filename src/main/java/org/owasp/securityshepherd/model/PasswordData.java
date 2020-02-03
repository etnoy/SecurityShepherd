package org.owasp.securityshepherd.model;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@EqualsAndHashCode
@Table("auth_data_password")
@Builder
@With
public final class PasswordData {

	private final String hashedPassword;

	@Builder.Default
	private final boolean passwordExpired = true;

}
