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

import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialActivationResult.CsrfTutorialActivationResultBuilder;
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
		return moduleMono.flatMap(module -> {
			this.moduleId = module.getId();
			return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
		});
	}

	public Mono<CsrfTutorialResult> getTutorial(final long userId) {
		if (this.moduleId == null) {
			return Mono.error(new ModuleNotInitializedException("Must initialize module first"));
		}

		final Mono<String> pseudonym = csrfService.getPseudonym(userId, this.moduleId);

		final Mono<CsrfTutorialResultBuilder> resultWithoutFlag = pseudonym
				.map(p -> CsrfTutorialResult.builder().pseudonym(p));

		final Mono<CsrfTutorialResultBuilder> resultWithFlag = resultWithoutFlag
				.zipWith(flagHandler.getDynamicFlag(userId, this.moduleId))
				.map(tuple -> tuple.getT1().flag(tuple.getT2()));

		return pseudonym.flatMap(p -> csrfService.validate(p, this.moduleId)).filter(isActive -> isActive)
				.flatMap(isActive -> resultWithFlag).switchIfEmpty(resultWithoutFlag)
				.map(CsrfTutorialResultBuilder::build);
	}

	public Mono<CsrfTutorialActivationResult> attack(final long userId, final String target) {
		if (this.moduleId == null) {
			return Mono.error(new ModuleNotInitializedException("Must initialize module first"));
		}

		CsrfTutorialActivationResultBuilder csrfTutorialActivationResultBuilder = CsrfTutorialActivationResult
				.builder();

		log.debug(String.format("User %d is attacking csrf target %s", userId, target));

		return csrfService.validatePseudonym(target, this.moduleId).flatMap(valid -> {
			if (!valid) {
				return Mono.just(csrfTutorialActivationResultBuilder.error("Unknown target ID").build());
			} else {
				return csrfService.getPseudonym(userId, this.moduleId).flatMap(p -> {
					if (p.equals(target)) {
						return Mono.just(
								csrfTutorialActivationResultBuilder.error("You cannot activate yourself").build());
					} else {
						return csrfService.attack(target, this.moduleId).then(
								Mono.just(csrfTutorialActivationResultBuilder.message("Thank you for voting").build()));
					}
				});
			}
		});

	}
}
