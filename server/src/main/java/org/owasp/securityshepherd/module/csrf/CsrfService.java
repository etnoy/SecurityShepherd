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

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CsrfService {
  private final CsrfVoteCounterRepository csrfVoteCounterRepository;

  public Mono<Void> incrementCounter(final long userId, final long moduleId) {
    return csrfVoteCounterRepository
        .findByUserIdAndModuleId(userId, moduleId)
        .switchIfEmpty(
            Mono.just(
                CsrfVoteCounter.builder().count(0L).userId(userId).moduleId(moduleId).build()))
        .map(counter -> counter.withCount(counter.getCount() + 1))
        .flatMap(csrfVoteCounterRepository::save)
        .then(Mono.empty());
  }

  public Mono<Void> resetCounter(final long userId, final long moduleId) {
    return csrfVoteCounterRepository
        .findByUserIdAndModuleId(userId, moduleId)
        .switchIfEmpty(
            Mono.just(
                CsrfVoteCounter.builder().count(0L).userId(userId).moduleId(moduleId).build()))
        .map(counter -> counter.withCount(0L))
        .flatMap(csrfVoteCounterRepository::save)
        .then(Mono.empty());
  }

  public Mono<Boolean> isIncremented(final long userId, final long moduleId) {
    return csrfVoteCounterRepository
        .findByUserIdAndModuleId(userId, moduleId)
        .map(counter -> counter.getCount() > 0)
        .switchIfEmpty(Mono.just(false));
  }
}
