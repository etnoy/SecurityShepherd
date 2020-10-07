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

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.module.BaseModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialResult.CsrfTutorialResultBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class CsrfTutorial extends BaseModule {
  private final CsrfService csrfService;

  private static final String MODULE_ID = "csrf-tutorial";

  public CsrfTutorial(
      final CsrfService csrfService,
      final ModuleService moduleService,
      final FlagHandler flagHandler) {
    super(MODULE_ID, moduleService, flagHandler, null);
    this.csrfService = csrfService;
  }

  public Mono<CsrfTutorialResult> getTutorial(final long userId) {

    final Mono<String> pseudonym = csrfService.getPseudonym(userId, MODULE_ID);

    final Mono<CsrfTutorialResultBuilder> resultWithoutFlag =
        pseudonym.map(p -> CsrfTutorialResult.builder().pseudonym(p));

    final Mono<CsrfTutorialResultBuilder> resultWithFlag =
        resultWithoutFlag.zipWith(getFlag(userId)).map(tuple -> tuple.getT1().flag(tuple.getT2()));

    return pseudonym
        .flatMap(pseudo -> csrfService.validate(pseudo, MODULE_ID))
        .filter(isActive -> isActive)
        .flatMap(isActive -> resultWithFlag)
        .switchIfEmpty(resultWithoutFlag)
        .map(CsrfTutorialResultBuilder::build);
  }

  public Mono<CsrfTutorialResult> attack(final long userId, final String target) {

    CsrfTutorialResultBuilder csrfTutorialResultBuilder = CsrfTutorialResult.builder();

    log.debug(String.format("User %d is attacking csrf target %s", userId, target));

    return module
        .map(m -> m.getId())
        .flatMap(moduleId -> csrfService.validatePseudonym(target, moduleId))
        .flatMap(
            valid -> {
              if (Boolean.TRUE.equals(valid)) {
                return csrfService
                    .getPseudonym(userId, moduleId)
                    .flatMap(
                        pseudonym -> {
                          if (pseudonym.equals(target)) {
                            return Mono.just(
                                csrfTutorialResultBuilder
                                    .error("You cannot activate yourself")
                                    .build());
                          } else {
                            return csrfService
                                .attack(target, moduleId)
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
