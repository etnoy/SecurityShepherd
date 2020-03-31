package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder
@With
public final class UserAuth implements Serializable {
  private static final long serialVersionUID = -1511082836956413928L;

  @Id
  private Integer id;

  private Integer userId;

  @JsonProperty("isEnabled")
  private boolean isEnabled;

  private int badLoginCount;

  @JsonProperty("isAdmin")
  private boolean isAdmin;

  private LocalDateTime suspendedUntil;

  private String suspensionMessage;

  private LocalDateTime accountCreated;

  private LocalDateTime lastLogin;

  private String lastLoginMethod;
}
