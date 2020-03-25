package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder
@With
public final class User implements Serializable {

  private static final long serialVersionUID = 3097353498257801154L;

  @Id
  private Integer id;

  @NonNull
  private String displayName;

  private Integer classId;

  private String email;

  @Column("user_key")
  private byte[] key;

  @Transient
  private Auth auth;

  @PersistenceConstructor
  public User(final Integer id, @NonNull final String displayName, final Integer classId,
      final String email, final byte[] key) {
    this.id = id;
    this.displayName = displayName;
    this.classId = classId;
    this.email = email;
    this.key = key;
    this.auth = null;
  }

}
