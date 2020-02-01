package org.owasp.securityshepherd.model;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@EqualsAndHashCode
@Table("auth_data_password")
@Builder(builderClassName = "UserAuthPasswordBuilder")
@With
public final class PasswordData {

	private final String hashedPassword;

	private final boolean passwordExpired = false;

}
