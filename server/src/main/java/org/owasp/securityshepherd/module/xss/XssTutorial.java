package org.owasp.securityshepherd.module.xss;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.module.SubmittableModule;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.XssService;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class XssTutorial implements SubmittableModule {

  private final XssService xssService;

  private final ModuleService moduleService;

  private Long moduleId;

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

  public Mono<String> submitQuery(final long userId, final String query) {
    String alert = null;
    final String htmlTarget = String.format(
        "<html><head><title>Alert</title></head><body><p>Result: %s</p></body></html>", query);

    try {
      alert = xssService.doXss(htmlTarget);
    } catch (IOException e) {
      return Mono.error(e);
    }

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    rootNode.put("result", String.format("Sorry, found no result for %s", query));

    if (alert != null) {
      rootNode.put("alert", alert);

      return moduleService.getDynamicFlag(userId, this.moduleId)
          .map(flag -> String.format("Congratulations, flag is %s", flag))
          .map(result -> rootNode.put("flag", result)).map(node -> node.toString());
    }
    return Mono.just(rootNode.toString());
  }
}
