package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@With
public class Auth implements Serializable {

  private static final long serialVersionUID = -1511082836956413928L;

  @EqualsAndHashCode.Include
  @Id
  private int id;

  private int user;

  private boolean isEnabled;

  private int badLoginCount;

  private boolean isAdmin;

  private Timestamp suspendedUntil;

  private String suspensionMessage;

  private Timestamp accountCreated;

  private Timestamp lastLogin;

  private final String lastLoginMethod;

  @Transient
  private final PasswordAuth password;

  @Transient
  private final SAMLAuth saml;

  @java.lang.SuppressWarnings("all")
  Auth(final int id, final int user, final boolean isEnabled, final int badLoginCount,
      final boolean isAdmin, final Timestamp suspendedUntil, final String suspensionMessage,
      final Timestamp accountCreated, final Timestamp lastLogin, final String lastLoginMethod) {
    this.id = id;
    this.user = user;
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
