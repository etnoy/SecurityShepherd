package org.owasp.securityshepherd.module;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface SubmittableModule {
  public Long getModuleId();
  public Mono<Long> initialize();
}
