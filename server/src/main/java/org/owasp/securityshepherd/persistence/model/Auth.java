package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public final class Auth implements Serializable {

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

}
