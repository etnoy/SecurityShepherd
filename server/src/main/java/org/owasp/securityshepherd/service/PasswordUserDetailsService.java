package org.owasp.securityshepherd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PasswordUserDetailsService implements ReactiveUserDetailsService {
  @Autowired
  UserService userService;

  @Override
  public Mono<UserDetails> findByUsername(final String loginName) {
    return userService.findUserDetailsByLoginName(loginName).cast(UserDetails.class);
  }
}
