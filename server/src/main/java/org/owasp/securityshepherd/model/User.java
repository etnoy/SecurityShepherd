package org.owasp.securityshepherd.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
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
  private Long id;

  @NonNull
  private String displayName;

  private Long classId;

  private String email;

  @Column("user_key")
  private byte[] key;
}
