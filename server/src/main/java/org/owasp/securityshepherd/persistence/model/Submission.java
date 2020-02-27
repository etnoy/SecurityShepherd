package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
public final class Submission implements Serializable {

	private static final long serialVersionUID = -5485881248601955741L;

	@Id
	private int id;

	private int userId;

	private int moduleId;

	@NonNull
	private Timestamp time;

	private boolean isValid;

	private String flag;

}