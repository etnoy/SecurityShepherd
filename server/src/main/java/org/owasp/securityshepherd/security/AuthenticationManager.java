package org.owasp.securityshepherd.security;

import java.util.List;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.service.WebTokenService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

  private final WebTokenService webTokenService;

  private final UserService userService;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();

    if (webTokenService.validateToken(authToken)) {

      final long userId = webTokenService.getUserIdFromToken(authToken);

      final Mono<List<SimpleGrantedAuthority>> authoritiesMono =
          userService.getAuthoritiesById(userId);

      return authoritiesMono
          .map(authorities -> new UsernamePasswordAuthenticationToken(userId, null, authorities));
    } else {
      return Mono.empty();
    }
  }
}
