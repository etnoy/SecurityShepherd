package org.owasp.securityshepherd.module;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public abstract class SubmittableModule {
  public Long moduleId = null;
  public static final String MODULE_IDENTIFIER = null;
  public Mono<Void> initialize() {
    return null;
  }
}
