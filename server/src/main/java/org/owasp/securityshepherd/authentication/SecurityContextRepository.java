/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {
  private final AuthenticationManager authenticationManager;

  @Override
  public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
    String authorizationHeader =
        serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return Mono.just(authorizationHeader.substring(7))
          .map(token -> new UsernamePasswordAuthenticationToken(token, token))
          .flatMap(authenticationManager::authenticate)
          .map(SecurityContextImpl::new);
    } else {
      return Mono.empty();
    }
  }

  @Override
  public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
    return Mono.error(new UnsupportedOperationException("Not supported yet."));
  }
}
