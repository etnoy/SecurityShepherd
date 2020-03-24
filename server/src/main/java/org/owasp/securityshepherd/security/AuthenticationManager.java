package org.owasp.securityshepherd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

  @Autowired
  private JWTUtil jwtUtil;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();

    String username = jwtUtil.getUsernameFromToken(authToken);

    if (username != null && jwtUtil.validateToken(authToken)) {
      log.debug(authToken);
      Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
      Role role = Role.valueOf(claims.get("role", String.class));

      log.debug("Role: " + role);
      
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username,
          new SimpleGrantedAuthority(role.name()));
      return Mono.just(auth);
    } else {
      return Mono.empty();
    }
  }
}
