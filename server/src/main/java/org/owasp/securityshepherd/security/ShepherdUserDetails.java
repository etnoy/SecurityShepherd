package org.owasp.securityshepherd.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.UserAuth;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Value;

@Value
public final class ShepherdUserDetails implements UserDetails {
  private static final long serialVersionUID = 9011116395514302667L;

  private UserAuth userAuth;

  private PasswordAuth passwordAuth;

  public long getUserId() {
    return userAuth.getUserId();
  }

  @Override
  public String getPassword() {
    return passwordAuth.getHashedPassword();
  }

  @Override
  public String getUsername() {
    return passwordAuth.getLoginName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    final LocalDateTime suspendedUntil = userAuth.getSuspendedUntil();

    if (suspendedUntil == null) {
      return true;
    }

    return suspendedUntil.isBefore(LocalDateTime.now());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return passwordAuth.isPasswordNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return userAuth.isEnabled();
  }

  public Role getRole() {
    if (userAuth.isAdmin()) {
      return Role.ROLE_ADMIN;
    } else {
      return Role.ROLE_USER;
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    if (userAuth.isAdmin()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    return authorities;
  }
}
