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

  private Integer userId;

  @NonNull
  private String loginName;

  @NonNull
  private String hashedPassword;

  @JsonProperty("isPasswordNonExpired")
  private boolean isPasswordNonExpired;
}
