package org.owasp.securityshepherd.security;

import java.util.Collection;
import org.owasp.securityshepherd.persistence.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PasswordUserDetails implements UserDetails {

  private static final long serialVersionUID = -3677404075923734838L;

  private User user;

  public PasswordUserDetails(User user) {
    log.trace("Creating password details for user " + user);

    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;

    // String role;
    //
    // if (user.getAuth().isAdmin()) {
    // role = "admin";
    // } else {
    // role = "player";
    // }
    //
    // return Collections.singletonList(new SimpleGrantedAuthority(role));

  }

  @Override
  public String getPassword() {
    return null;
    // log.trace("Found password hash ", user.getAuth().getPassword().getHashedPassword());
    //
    // return user.getAuth().getPassword().getHashedPassword();
  }

  @Override
  public String getUsername() {
    return null;
    // log.trace("Found username " + user.getAuth().getPassword().getLoginName());
    // return user.getDisplayName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;

    // final Timestamp suspendedUntil = user.getAuth().getSuspendedUntil();
    //
    // if (suspendedUntil == null) {
    // return true;
    // }
    //
    // return suspendedUntil.getTime() < System.currentTimeMillis();

  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
    // return user.getAuth().getPassword().isPasswordNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return false;
    // return user.getAuth().isEnabled();
  }

}
