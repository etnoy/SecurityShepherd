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

package org.owasp.securityshepherd.module.xss;

import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.SubmittableModule;
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

  public static final String SHORT_NAME = "xss-tutorial";

  private final ObjectMapper objectMapper;
  
  private final FlagHandler flagComponent;

  @PostConstruct
  public Mono<Long> initialize() {
    log.info("Creating xss tutorial module");
    final Mono<Module> moduleMono = moduleService.create("XSS Tutorial", SHORT_NAME,
        "Tutorial for making cross site scripting");
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
    if (this.moduleId == null) {
      return Mono.error(new RuntimeException("Must initialize module before submitting to it"));
    }
    final String htmlTarget = String.format(
        "<html><head><title>Alert</title></head><body><p>Result: %s</p></body></html>", query);

    final String alert = xssService.doXss(htmlTarget);

    ObjectNode rootNode = objectMapper.createObjectNode();

    rootNode.put("result", String.format("Sorry, found no result for %s", query));

    if (alert != null) {
      rootNode.put("alert", alert);

      return flagComponent.getDynamicFlag(userId, this.moduleId)
          .map(flag -> String.format("Congratulations, flag is %s", flag))
          .map(result -> rootNode.put("flag", result)).map(ObjectNode::toString);
    }
    return Mono.just(rootNode.toString());
  }
}
