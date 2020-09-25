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
package org.owasp.securityshepherd.module.xss;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse.XssTutorialResponseBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class XssTutorial extends AbstractModule {
  private final XssService xssService;

  private final ModuleService moduleService;

  private final FlagHandler flagHandler;

  @Override
  public String getDescription() {
    return "Tutorial on cross site scripting (XSS)";
  }

  @Override
  public String getName() {
    return "XSS Tutorial";
  }

  @Override
  public String getShortName() {
    return "xss-tutorial";
  }

  public Mono<Long> initialize() {
    log.info("Creating xss tutorial module");
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        module -> {
          this.moduleId = module.getId();
          return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
        });
  }

  public Mono<XssTutorialResponse> submitQuery(final long userId, final String query) {
    if (this.moduleId == null) {
      return Mono.error(new RuntimeException("Must initialize module before submitting to it"));
    }
    final String htmlTarget =
        String.format(
            "<html><head><title>Alert</title></head><body><p>Result: %s</p></body></html>", query);

    final List<String> alerts = xssService.doXss(htmlTarget);

    final XssTutorialResponseBuilder xssTutorialResponseBuilder = XssTutorialResponse.builder();

    if (alerts.isEmpty()) {
      xssTutorialResponseBuilder.result(String.format("Sorry, found no result for %s", query));
      return Mono.just(xssTutorialResponseBuilder.build());
    } else {
      xssTutorialResponseBuilder.alert(alerts.get(0));

      return flagHandler
          .getDynamicFlag(userId, this.moduleId)
          .map(flag -> String.format("Congratulations, flag is %s", flag))
          .map(xssTutorialResponseBuilder::result)
          .map(XssTutorialResponseBuilder::build);
    }
  }
}
