package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@With
public class User implements Serializable {

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

  @PersistenceConstructor
  public User(final int id, @NonNull final String displayName, final Integer classId,
      final String email, final byte[] key) {
    if (displayName == null) {
      throw new java.lang.NullPointerException("displayName is marked non-null but is null");
    }
    this.id = id;
    this.displayName = displayName;
    this.classId = classId;
    this.email = email;
    this.key = key;
    this.auth = null;
  }


}
