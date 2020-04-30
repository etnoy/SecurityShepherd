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
        .map(Authentication::getPrincipal).cast(Long.class);
  }
}
