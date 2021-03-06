/*
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

import org.owasp.securityshepherd.exception.NotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ControllerAuthentication {
  public Mono<Long> getUserId() {
    return
    // Get the security context
    ReactiveSecurityContextHolder.getContext()
        // Get the authentication from the context
        .map(SecurityContext::getAuthentication)
        // If the principal is null, filter it out and return exceptio
        .filter(auth -> auth.getPrincipal() != null)
        .switchIfEmpty(Mono.error(new NotAuthenticatedException()))
        // If the principal isn't null, cast it to Long
        .map(Authentication::getPrincipal)
        .cast(Long.class);
  }
}
