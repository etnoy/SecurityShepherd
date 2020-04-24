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

package org.owasp.securityshepherd.service;

import java.time.Clock;
import java.time.LocalDateTime;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.model.Correction;
import org.owasp.securityshepherd.model.Correction.CorrectionBuilder;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class CorrectionService {

  private final CorrectionRepository correctionRepository;

  private final Clock clock;

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
