package org.owasp.securityshepherd.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShepherdUserDetailsService implements UserDetailsService {


  @Override
  public UserDetails loadUserByUsername(String loginName) {
    return null;


  }
}
