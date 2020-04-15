package org.owasp.securityshepherd.module.xss;

import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.service.ModuleService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class XssTutorial { // extends SubmittableModule {

  private final ModuleService moduleService;

  public Long moduleId;

  public static final String MODULE_IDENTIFIER = "xss-tutorial";
  
  @PostConstruct
  public Mono<Void> initialize() {
    log.info("Creating xss tutorial module");
    final Mono<Module> moduleMono =
        moduleService.create("XSS Tutorial", MODULE_IDENTIFIER);

    return moduleMono.flatMap(module -> {
      this.moduleId = module.getId();
      return Mono.when(moduleService.setDynamicFlag(moduleId));
    });
  }
}
