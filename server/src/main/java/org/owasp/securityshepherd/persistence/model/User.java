package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import org.owasp.securityshepherd.security.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Value
@AllArgsConstructor
@Builder
@Slf4j
@With
public final class User implements Serializable, UserDetails {

  private static final long serialVersionUID = 3097353498257801154L;

  @Id
  private final Integer id;

  @NonNull
  private final String displayName;

  private final Integer classId;

  private final String email;

  @Column("user_key")
  private final byte[] key;

  @Transient
  private final Auth auth;

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

  @Override
  public String getPassword() {

    return this.getAuth().getPassword().getHashedPassword();

  }

  @Override
  public String getUsername() {

    return auth.getPassword().getLoginName();

  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    final Timestamp suspendedUntil = auth.getSuspendedUntil();

    if (suspendedUntil == null) {
      return true;
    }

    return suspendedUntil.getTime() < System.currentTimeMillis();

  }

  @Override
  public boolean isCredentialsNonExpired() {
    return auth.getPassword().isPasswordNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return auth.isEnabled();
  }


  public Role getRole() {

    if (auth.isAdmin()) {
      return Role.ROLE_ADMIN;
    } else {
      return Role.ROLE_USER;
    }

  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return Collections.singletonList(new SimpleGrantedAuthority(getRole().name()));

  }



}
