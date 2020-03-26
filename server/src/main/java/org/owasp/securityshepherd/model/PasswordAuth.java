package org.owasp.securityshepherd.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
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

  @JsonProperty("isPasswordNonExpired")
  private boolean isPasswordNonExpired;
  
  @java.beans.ConstructorProperties({"id", "user", "loginName", "hashedPassword", "isPasswordNonExpired"})
  public PasswordAuth(final Integer id, final int user, @NonNull final String loginName, @NonNull final String hashedPassword, @JsonProperty("isPasswordNonExpired") final boolean isPasswordNonExpired) {
    this.id = id;
    this.user = user;
    this.loginName = loginName;
    this.hashedPassword = hashedPassword;
    this.isPasswordNonExpired = isPasswordNonExpired;
  }


}
