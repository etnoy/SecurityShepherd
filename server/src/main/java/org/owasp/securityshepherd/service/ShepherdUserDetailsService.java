package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.model.ShepherdUserDetails;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ShepherdUserDetailsService implements ReactiveUserDetailsService {

  @Autowired
  UserService userService;

  @Override
  public Mono<UserDetails> findByUsername(final String username) {
    return userService.findByLoginName(username).map(ShepherdUserDetails::new);
  }
}
