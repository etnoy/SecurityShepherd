/**
 * This file is part of Security Shepherd.
 *
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.owasp.securityshepherd.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
    ServerHttpRequest request = serverWebExchange.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return Mono.just(authHeader.substring(7))
          .map(token -> new UsernamePasswordAuthenticationToken(token, token))
          .flatMap(authenticationManager::authenticate).map(SecurityContextImpl::new);
    } else {
      return Mono.empty();
    }
  }
}
