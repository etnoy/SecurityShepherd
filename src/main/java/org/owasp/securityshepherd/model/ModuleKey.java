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
public final class ModuleKey {

	private final String fixedKey;

}
