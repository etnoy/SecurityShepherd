package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class User implements Serializable {

  private static final long serialVersionUID = 3097353498257801154L;

  @Id
  private final int id;

  @NonNull
  private final String displayName;

  private final Integer classId;

  private final String email;

  @Column("user_key")
  private final byte[] key;

  @Transient
  private final Auth auth;

}
