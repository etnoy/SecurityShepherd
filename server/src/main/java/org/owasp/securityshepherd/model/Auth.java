package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder
@With
public final class Auth implements Serializable {
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

  @Transient
  private PasswordAuth password;

  @Transient
  private SamlAuth saml;

  @PersistenceConstructor
  public Auth(final Integer id, final int userId, final boolean isEnabled, final int badLoginCount,
      final boolean isAdmin, final LocalDateTime suspendedUntil, final String suspensionMessage,
      final LocalDateTime accountCreated, final LocalDateTime lastLogin, final String lastLoginMethod) {
    this.id = id;
    this.userId = userId;
    this.isEnabled = isEnabled;
    this.badLoginCount = badLoginCount;
    this.isAdmin = isAdmin;
    this.suspendedUntil = suspendedUntil;
    this.suspensionMessage = suspensionMessage;
    this.accountCreated = accountCreated;
    this.lastLogin = lastLogin;
    this.lastLoginMethod = lastLoginMethod;
    this.password = null;
    this.saml = null;
  }
}
