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

import java.time.LocalDateTime;

import org.owasp.securityshepherd.module.FlagHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CsrfService {
	private final CsrfAttackRepository csrfAttackRepository;

	private final FlagHandler flagHandler;

	public Mono<Void> attack(final String pseudonym, final long moduleId) {
		return csrfAttackRepository.findByPseudonymAndModuleId(pseudonym, moduleId)
				.map(attack -> attack.withFinished(LocalDateTime.now())).flatMap(csrfAttackRepository::save)
				.then(Mono.empty());
	}

	public Mono<String> getPseudonym(final long userId, final long moduleId) {
		return flagHandler.getSaltedHmac(userId, moduleId, "csrfPseudonym", "HmacSHA256");
	}

	public Mono<Boolean> validatePseudonym(final String pseudonym, final long moduleId) {
		return csrfAttackRepository.countByPseudonymAndModuleId(pseudonym, moduleId).map(count -> count > 0);
	}

	public Mono<Boolean> validate(final String pseudonym, final long moduleId) {
		return csrfAttackRepository.findByPseudonymAndModuleId(pseudonym, moduleId)
				.map(attack -> attack.getFinished() != null)
				.switchIfEmpty(csrfAttackRepository.save(CsrfAttack.builder().pseudonym(pseudonym)
						.started(LocalDateTime.now()).moduleId(moduleId).build()).then(Mono.just(false)));
	}
}
