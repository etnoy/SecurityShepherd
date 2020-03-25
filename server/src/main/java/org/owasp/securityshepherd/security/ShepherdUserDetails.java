package org.owasp.securityshepherd.security;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.owasp.securityshepherd.persistence.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public final class ShepherdUserDetails implements UserDetails {

  /**
   * 
   */
  private static final long serialVersionUID = 9011116395514302667L;
  private final User user;

  @Override
  public String getPassword() {

    return user.getAuth().getPassword().getHashedPassword();

  }

  @Override
  public String getUsername() {

    return user.getAuth().getPassword().getLoginName();

  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    final Timestamp suspendedUntil = user.getAuth().getSuspendedUntil();

    if (suspendedUntil == null) {
      return true;
    }

    return suspendedUntil.getTime() < System.currentTimeMillis();

  }

  @Override
  public boolean isCredentialsNonExpired() {
    return user.getAuth().getPassword().isPasswordNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return user.getAuth().isEnabled();
  }


  public Role getRole() {

    if (user.getAuth().isAdmin()) {
      log.trace("User is admin");
      return Role.ROLE_ADMIN;
    } else {
      log.trace("User is regular user");
      return Role.ROLE_USER;
    }

  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    if (user.getAuth().isAdmin()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    return authorities;

  }

}