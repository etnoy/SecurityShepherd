/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.module.csrf;

import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialIncrementResult.CsrfTutorialIncrementResultBuilder;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialResult.CsrfTutorialResultBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class CsrfTutorial extends AbstractModule {
  private final CsrfService csrfService;

  private final ModuleService moduleService;

  private final FlagHandler flagHandler;

  @Override
  public String getDescription() {
    return "Tutorial on cross-site request forgery (CSRF)";
  }

  @Override
  public String getName() {
    return "CSRF Tutorial";
  }

  @Override
  public String getShortName() {
    return "csrf-tutorial";
  }

  public Mono<Long> initialize() {
    log.info("Creating csrf tutorial module");
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        module -> {
          this.moduleId = module.getId();
          return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
        });
  }

  public Mono<CsrfTutorialResult> getTutorial(final long userId) {
    if (this.moduleId == null) {
      return Mono.error(new RuntimeException("Must initialize module first"));
    }

    final CsrfTutorialResultBuilder csrfTutorialResultBuilder =
        CsrfTutorialResult.builder().parameter(String.valueOf(userId));

    return csrfService
        .validate(userId, this.moduleId)
        .filterWhen(isIncremented -> Mono.just(true))
        .flatMap(
            isIncremented ->
                flagHandler
                    .getDynamicFlag(userId, this.moduleId)
                    .map(csrfTutorialResultBuilder::flag))
        .defaultIfEmpty(csrfTutorialResultBuilder)
        .map(builder -> builder.build());
  }

  public Mono<CsrfTutorialIncrementResult> increment(final long userId, final String targetUserId) {
    if (this.moduleId == null) {
      return Mono.error(new RuntimeException("Must initialize module first"));
    }

    CsrfTutorialIncrementResultBuilder csrfTutorialIncrementResultBuilder =
        CsrfTutorialIncrementResult.builder();

    if (String.valueOf(userId) == targetUserId) {
      return Mono.just(
          csrfTutorialIncrementResultBuilder
              .error("You cannot increment your own counter")
              .build());
    } else {
      return csrfService
          .incrementCounter(targetUserId, this.moduleId)
          .then(
              Mono.just(
                  csrfTutorialIncrementResultBuilder.message("Thank you for voting").build()));
    }
  }
}
