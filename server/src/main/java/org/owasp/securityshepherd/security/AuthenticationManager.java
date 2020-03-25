package org.owasp.securityshepherd.security;

import java.util.Collection;
import org.owasp.securityshepherd.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();

    if (jwtUtil.validateToken(authToken)) {

      final Mono<User> userMono = jwtUtil.getUserFromToken(authToken);

      final Mono<String> loginNameMono =
          userMono.map(user -> user.getAuth().getPassword().getLoginName());

      final Mono<Collection<? extends GrantedAuthority>> authoritiesMono =
          userMono.map(ShepherdUserDetails::new).map(ShepherdUserDetails::getAuthorities);

      return loginNameMono.zipWith(authoritiesMono).map(
          tuple -> new UsernamePasswordAuthenticationToken(tuple.getT1(), null, tuple.getT2()));

    } else {
      return Mono.empty();
    }
  }
}
