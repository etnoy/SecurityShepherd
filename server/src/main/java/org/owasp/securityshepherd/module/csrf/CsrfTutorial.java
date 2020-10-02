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
package org.owasp.securityshepherd.module.csrf;

import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialResult.CsrfTutorialResultBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CsrfTutorial extends AbstractModule {
  private final CsrfService csrfService;

  public CsrfTutorial(
      final CsrfService csrfService,
      final ModuleService moduleService,
      final FlagHandler flagHandler) {
    super(
        "CSRF Tutorial",
        "csrf-tutorial",
        "Tutorial on cross-site request forgery (CSRF)",
        moduleService,
        flagHandler);
    this.csrfService = csrfService;
  }

  public Mono<CsrfTutorialResult> getTutorial(final long userId) {

    final Mono<String> pseudonym = csrfService.getPseudonym(userId, getModuleId());

    final Mono<CsrfTutorialResultBuilder> resultWithoutFlag =
        pseudonym.map(p -> CsrfTutorialResult.builder().pseudonym(p));

    final Mono<CsrfTutorialResultBuilder> resultWithFlag =
        resultWithoutFlag
            .zipWith(flagHandler.getDynamicFlag(userId, getModuleId()))
            .map(tuple -> tuple.getT1().flag(tuple.getT2()));

    return pseudonym
        .flatMap(p -> csrfService.validate(p, getModuleId()))
        .filter(isActive -> isActive)
        .flatMap(isActive -> resultWithFlag)
        .switchIfEmpty(resultWithoutFlag)
        .map(CsrfTutorialResultBuilder::build);
  }

  public Mono<CsrfTutorialResult> attack(final long userId, final String target) {

    CsrfTutorialResultBuilder csrfTutorialResultBuilder = CsrfTutorialResult.builder();

    log.debug(String.format("User %d is attacking csrf target %s", userId, target));

    return csrfService
        .validatePseudonym(target, getModuleId())
        .flatMap(
            valid -> {
              if (Boolean.TRUE.equals(valid)) {
                return csrfService
                    .getPseudonym(userId, getModuleId())
                    .flatMap(
                        p -> {
                          if (p.equals(target)) {
                            return Mono.just(
                                csrfTutorialResultBuilder
                                    .error("You cannot activate yourself")
                                    .build());
                          } else {
                            return csrfService
                                .attack(target, getModuleId())
                                .then(
                                    Mono.just(
                                        csrfTutorialResultBuilder
                                            .message("Thank you for voting")
                                            .build()));
                          }
                        });

              } else {
                return Mono.just(csrfTutorialResultBuilder.error("Unknown target ID").build());
              }
            });
  }
}
