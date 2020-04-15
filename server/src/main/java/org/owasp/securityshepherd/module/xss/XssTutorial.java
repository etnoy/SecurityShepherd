package org.owasp.securityshepherd.module.xss;

import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.module.SubmittableModule;
import org.owasp.securityshepherd.service.ModuleService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class XssTutorial implements SubmittableModule {

  private final ModuleService moduleService;

  public Long moduleId;

  public static final String MODULE_URL = "xss-tutorial";

  @PostConstruct
  public Mono<Long> initialize() {
    log.info("Creating xss tutorial module");
    final Mono<Module> moduleMono = moduleService.create("XSS Tutorial", MODULE_URL);

    return moduleMono.flatMap(module -> {
      this.moduleId = module.getId();
      return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
    });
  }
  
  @Override
  public Long getModuleId() {
    return this.moduleId;
  }
}
