package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
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

  @Id
  private Integer id;

  private int user;

  @NonNull
  private String loginName;

  @NonNull
  private String hashedPassword;

  private boolean isPasswordNonExpired;

  @PersistenceConstructor
  public PasswordAuth(final Integer id, final int user, @NonNull final String loginName,
      @NonNull final String hashedPassword, final boolean isPasswordNonExpired) {
    this.id = id;
    this.user = user;
    this.loginName = loginName;
    this.hashedPassword = hashedPassword;
    this.isPasswordNonExpired = isPasswordNonExpired;
  }

}
