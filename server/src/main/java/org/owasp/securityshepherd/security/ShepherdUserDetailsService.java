package org.owasp.securityshepherd.security;

import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShepherdUserDetailsService implements ReactiveUserDetailsService  {

  @Autowired
  UserService userService;
  
  @Override
  public Mono<UserDetails> findByUsername(final String username) {
    log.debug("Hello");
    return userService.getByLoginName(username).map(user -> (UserDetails)user);
  }
}
