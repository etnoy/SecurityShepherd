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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.scoring.CorrectionRepository;
import org.owasp.securityshepherd.scoring.RankedSubmissionRepository;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionRepository;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionService unit test")
class SubmissonServiceTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private SubmissionService submissionService;

  @Mock private SubmissionRepository submissionRepository;

  @Mock private RankedSubmissionRepository rankedSubmissionRepository;

  @Mock private CorrectionRepository correctionRepository;

  @Mock private FlagHandler flagHandler;

  @Test
  void findAllByModuleName_NoSubmissionsExist_ReturnsEmpty() {
    final String mockModuleName = "id";
    when(submissionRepository.findAllByModuleName(mockModuleName)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllByModuleName(mockModuleName))
        .expectComplete()
        .verify();
    verify(submissionRepository, times(1)).findAllByModuleName(mockModuleName);
  }

  @Test
  void findAllByModuleName_SubmissionsExist_ReturnsSubmissions() {
    final String mockModuleName = "id";
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    when(submissionRepository.findAllByModuleName(mockModuleName))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllByModuleName(mockModuleName))
        .expectNext(mockSubmission1)
        .expectNext(mockSubmission2)
        .expectNext(mockSubmission3)
        .expectNext(mockSubmission4)
        .expectComplete()
        .verify();

    verify(submissionRepository, times(1)).findAllByModuleName(mockModuleName);
  }

  @Test
  void findAllValidByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidByUserId(userId))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void findAllValidByUserId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 26L;
    when(submissionRepository.findAllValidByUserId(mockUserId)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllValidByUserId(mockUserId))
        .expectComplete()
        .verify();
    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  void findAllValidByUserId_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 809L;
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    when(submissionRepository.findAllValidByUserId(mockUserId))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllValidByUserId(mockUserId))
        .expectNext(mockSubmission1)
        .expectNext(mockSubmission2)
        .expectNext(mockSubmission3)
        .expectNext(mockSubmission4)
        .expectComplete()
        .verify();

    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  void findAllValidByUserIdAndModuleName_InvalidUserId_ReturnsInvalidUserIdException() {
    final String mockModuleName = "id";
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(
              submissionService.findAllValidByUserIdAndModuleName(userId, mockModuleName))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void findAllValidByUserIdAndModuleName_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 648L;
    final String mockModuleName = "id";
    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.empty());
    StepVerifier.create(
            submissionService.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .expectComplete()
        .verify();
    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
  }

  @Test
  void findAllValidByUserIdAndModuleName_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 864L;
    final String mockModuleName = "id";
    final Submission mockSubmission = mock(Submission.class);

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.just(mockSubmission));
    StepVerifier.create(
            submissionService.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .expectNext(mockSubmission)
        .expectComplete()
        .verify();

    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
  }

  @Test
  void findAllValidIdsByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidModuleNamesByUserId(userId))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void findAllValidIdsByUserId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 508L;
    when(submissionRepository.findAllValidByUserId(mockUserId)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllValidModuleNamesByUserId(mockUserId))
        .expectNext(new ArrayList<String>())
        .expectComplete()
        .verify();
    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  void findAllValidIdsByUserId_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 237L;
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    final String moduleName1 = "id1";
    when(mockSubmission1.getModuleName()).thenReturn(moduleName1);

    final String moduleName2 = "id2";
    when(mockSubmission2.getModuleName()).thenReturn(moduleName2);

    final String moduleName3 = "id3";
    when(mockSubmission3.getModuleName()).thenReturn(moduleName3);

    final String moduleName4 = "id4";
    when(mockSubmission4.getModuleName()).thenReturn(moduleName4);

    final List<String> moduleNameList =
        Arrays.asList(new String[] {moduleName1, moduleName2, moduleName3, moduleName4});

    when(submissionRepository.findAllValidByUserId(mockUserId))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllValidModuleNamesByUserId(mockUserId))
        .expectNext(moduleNameList)
        .expectComplete()
        .verify();

    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  private void setClock(final Clock clock) {
    submissionService.setClock(clock);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    submissionService =
        new SubmissionService(submissionRepository, rankedSubmissionRepository, flagHandler);
  }

  @Test
  void submit_InvalidFlag_ReturnsInvalidSubmission() {
    final long mockUserId = 293L;
    final String mockModuleName = "id";
    final long mockSubmissionId = 353L;

    final String flag = "invalidFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleName, flag)).thenReturn(Mono.just(false));

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class)))
        .thenAnswer(
            user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleName, flag))
        .assertNext(
            submission -> {
              assertThat(submission.getId()).isEqualTo(mockSubmissionId);
              assertThat(submission.getUserId()).isEqualTo(mockUserId);
              assertThat(submission.getModuleName()).isEqualTo(mockModuleName);
              assertThat(submission.getFlag()).isEqualTo(flag);
              assertThat(submission.getTime()).isEqualTo(LocalDateTime.now(fixedClock));
              assertThat(submission.isValid()).isFalse();
            })
        .expectComplete()
        .verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleName, flag);
    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }

  @Test
  void submit_InvalidUserId_ReturnsInvalidUserIdException() {
    final String mockModuleName = "id";
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submit(userId, mockModuleName, "flag"))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void submit_ModuleAlreadySolvedByUser_ReturnsModuleAlreadySolvedException() {
    final long mockUserId = 293L;
    final String mockModuleName = "id";
    final String flag = "validFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleName, flag)).thenReturn(Mono.just(true));

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.just(mock(Submission.class)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleName, flag))
        .expectError(ModuleAlreadySolvedException.class)
        .verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleName, flag);
    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
  }

  @Test
  void submit_ValidFlag_ReturnsValidSubmission() {
    final long mockUserId = 293L;
    final String mockModuleName = "id";
    final long mockSubmissionId = 353L;

    final String flag = "validFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleName, flag)).thenReturn(Mono.just(true));

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class)))
        .thenAnswer(
            user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleName, flag))
        .assertNext(
            submission -> {
              assertThat(submission.getId()).isEqualTo(mockSubmissionId);
              assertThat(submission.getUserId()).isEqualTo(mockUserId);
              assertThat(submission.getModuleName()).isEqualTo(mockModuleName);
              assertThat(submission.getFlag()).isEqualTo(flag);
              assertThat(submission.getTime()).isEqualTo(LocalDateTime.now(fixedClock));
              assertThat(submission.isValid()).isTrue();
            })
        .expectComplete()
        .verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleName, flag);
    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }

  @Test
  void submitValid_InvalidUserId_ReturnsInvalidUserIdException() {
    final String mockModuleName = "id";
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submitValid(userId, mockModuleName))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void submitValid_ModuleAlreadySolvedByUser_ReturnsModuleAlreadySolvedException() {
    final long mockUserId = 743L;
    final String mockModuleName = "id";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.just(mock(Submission.class)));

    StepVerifier.create(submissionService.submitValid(mockUserId, mockModuleName))
        .expectError(ModuleAlreadySolvedException.class)
        .verify();

    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
  }

  @Test
  void submitValid_ModuleNotAlreadySolved_ReturnsValidSubmission() {
    final long mockUserId = 293L;
    final String mockModuleName = "id";
    final long mockSubmissionId = 353L;

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(submissionRepository.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class)))
        .thenAnswer(
            user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submitValid(mockUserId, mockModuleName))
        .assertNext(
            submission -> {
              assertThat(submission.getId()).isEqualTo(mockSubmissionId);
              assertThat(submission.getUserId()).isEqualTo(mockUserId);
              assertThat(submission.getModuleName()).isEqualTo(mockModuleName);
              assertThat(submission.getFlag()).isNull();
              assertThat(submission.getTime()).isEqualTo(LocalDateTime.now(fixedClock));
              assertThat(submission.isValid()).isTrue();
            })
        .expectComplete()
        .verify();

    verify(submissionRepository, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }
}
