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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidRankException;
import org.owasp.securityshepherd.module.ModulePointRepository;
import org.owasp.securityshepherd.scoring.ModulePoint;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.ScoreboardEntry;
import org.owasp.securityshepherd.scoring.ScoreboardRepository;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoreService unit test")
class ScoreServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ScoreService scoreService;

  @Mock ModulePointRepository modulePointRepository;

  @Mock ScoreboardRepository scoreboardRepository;

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    scoreService = new ScoreService(modulePointRepository, scoreboardRepository);
  }

  @Test
  void setModuleScore_ValidModuleIdAndRank_ReturnsScore() throws Exception {
    final long mockModuleId = 884L;
    final int rank = 3;
    final int points = 1000;

    when(modulePointRepository.save(any(ModulePoint.class)))
        .thenAnswer(args -> Mono.just(args.getArgument(0, ModulePoint.class)));

    StepVerifier.create(scoreService.setModuleScore(mockModuleId, rank, points))
        .assertNext(
            modulePoint -> {
              assertThat(modulePoint.getModuleId()).isEqualTo(mockModuleId);
              assertThat(modulePoint.getRank()).isEqualTo(rank);
              assertThat(modulePoint.getPoints()).isEqualTo(points);
            })
        .expectComplete()
        .verify();
  }

  @Test
  void submit_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(scoreService.setModuleScore(moduleId, 1, 1))
          .expectError(InvalidModuleIdException.class)
          .verify();
    }
  }

  @Test
  void submit_InvalidRank_ReturnsRankException() {
    for (final int rank : new int[] {-1, -123, -999999}) {
      StepVerifier.create(scoreService.setModuleScore(1, rank, 1))
          .expectError(InvalidRankException.class)
          .verify();
    }
  }

  @Test
  void getScoreboard_NoArguments_CallsRepository() throws Exception {
    final ScoreboardEntry mockScoreboardEntry1 = mock(ScoreboardEntry.class);
    final ScoreboardEntry mockScoreboardEntry2 = mock(ScoreboardEntry.class);
    final ScoreboardEntry mockScoreboardEntry3 = mock(ScoreboardEntry.class);

    when(scoreboardRepository.findAll())
        .thenReturn(Flux.just(mockScoreboardEntry1, mockScoreboardEntry2, mockScoreboardEntry3));

    StepVerifier.create(scoreService.getScoreboard())
        .expectNext(mockScoreboardEntry1)
        .expectNext(mockScoreboardEntry2)
        .expectNext(mockScoreboardEntry3)
        .expectComplete()
        .verify();
  }
}
