package org.owasp.securityshepherd.module.sqlinjection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/module/" + SqlInjectionTutorial.MODULE_URL)
public class SqlInjectionTutorialController {
  private final SqlInjectionTutorial sqlInjectionTutorial;

  private final ObjectMapper objectMapper;

  @PostMapping(path = "search")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<SqlInjectionTutorialRow> search(@RequestBody final String request) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class).flatMapMany(userId -> {
          try {
            return this.sqlInjectionTutorial.submitQuery(userId,
                objectMapper.readTree(request).get("query").asText());
          } catch (JsonProcessingException e) {
            return Mono.error(e);
          }
        });
  }
}
