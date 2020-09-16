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
package org.owasp.securityshepherd.scoring;

import java.time.Clock;
import java.time.LocalDateTime;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.scoring.Correction.CorrectionBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class CorrectionService {
  private final CorrectionRepository correctionRepository;

  private Clock clock;

  public CorrectionService(final CorrectionRepository correctionRepository) {
    this.correctionRepository = correctionRepository;
    resetClock();
  }

  public void resetClock() {
    this.clock = Clock.systemDefaultZone();
  }

  public void setClock(Clock clock) {
    this.clock = clock;
  }

  public Mono<Correction> submit(final Long userId, final long amount, final String description) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    final CorrectionBuilder correctionBuilder = Correction.builder();

    correctionBuilder.userId(userId);
    correctionBuilder.amount(amount);
    correctionBuilder.description(description);
    correctionBuilder.time(LocalDateTime.now(clock));

    return correctionRepository.save(correctionBuilder.build());
  }
}
