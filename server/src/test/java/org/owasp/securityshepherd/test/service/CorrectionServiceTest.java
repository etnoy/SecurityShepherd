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

package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.scoring.CorrectionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.scoring.Correction;
import org.owasp.securityshepherd.scoring.CorrectionRepository;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("CorrectionService unit test")
public class CorrectionServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private CorrectionService correctionService;

  @Mock
  CorrectionRepository correctionRepository;

  @Mock
  Clock clock;

  private void setClock(final Clock clock) {
    correctionService.setClock(clock);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    correctionService = new CorrectionService(correctionRepository);
  }

  @Test
  public void submit_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(correctionService.submit(userId, 500, ""))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void submit_ValidUserId_ReturnsCorrection() throws Exception {
    final long mockUserId = 609L;
    final int amount = 1000;
    final String description = "Bonus";

    when(correctionRepository.save(any(Correction.class)))
        .thenAnswer(correction -> Mono.just(correction.getArgument(0, Correction.class)));

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    StepVerifier.create(correctionService.submit(mockUserId, amount, description))
        .assertNext(correction -> {
          assertThat(correction.getUserId()).isEqualTo(mockUserId);
          assertThat(correction.getAmount()).isEqualTo(amount);
          assertThat(correction.getDescription()).isEqualTo(description);
          assertThat(correction.getTime()).isEqualTo(LocalDateTime.now(fixedClock));
        }).expectComplete().verify();
  }
}
