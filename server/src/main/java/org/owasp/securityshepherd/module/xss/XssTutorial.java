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
import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse.XssTutorialResponseBuilder;
import org.springframework.stereotype.Component;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

@Component
@EqualsAndHashCode(callSuper = true)
public class XssTutorial extends AbstractModule {
  private final XssService xssService;

  public XssTutorial(
      final XssService xssService,
      final ModuleService moduleService,
      final FlagHandler flagHandler) {
    super(
        "XSS Tutorial",
        "xss-tutorial",
        "Tutorial on cross site scripting (XSS)",
        moduleService,
        flagHandler);
    this.xssService = xssService;
  }

  public Mono<XssTutorialResponse> submitQuery(final long userId, final String query) {

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
          .getDynamicFlag(userId, getModuleId())
          .map(flag -> String.format("Congratulations, flag is %s", flag))
          .map(xssTutorialResponseBuilder::result)
          .map(XssTutorialResponseBuilder::build);
    }
  }
}
