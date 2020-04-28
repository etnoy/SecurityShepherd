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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionService unit test")
public class SubmissonServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private SubmissionService submissionService;

  @Mock
  private SubmissionRepository submissionRepository;

  @Mock
  private CorrectionRepository correctionRepository;

  @Mock
  private FlagHandler flagHandler;

  @Test
  public void findAllByModuleId_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllByModuleId(moduleId))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void findAllByModuleId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockModuleId = 614L;
    when(submissionRepository.findAllByModuleId(mockModuleId)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllByModuleId(mockModuleId)).expectComplete()
        .verify();
    verify(submissionRepository, times(1)).findAllByModuleId(mockModuleId);
  }

  @Test
  public void findAllByModuleId_SubmissionsExist_ReturnsSubmissions() {
    final long mockModuleId = 628L;
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    when(submissionRepository.findAllByModuleId(mockModuleId))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllByModuleId(mockModuleId))
        .expectNext(mockSubmission1).expectNext(mockSubmission2).expectNext(mockSubmission3)
        .expectNext(mockSubmission4).expectComplete().verify();

    verify(submissionRepository, times(1)).findAllByModuleId(mockModuleId);
  }

  @Test
  public void findAllValidByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidByUserId(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findAllValidByUserId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 26L;
    when(submissionRepository.findAllValidByUserId(mockUserId)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllValidByUserId(mockUserId)).expectComplete()
        .verify();
    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  public void findAllValidByUserId_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 809L;
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    when(submissionRepository.findAllValidByUserId(mockUserId))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllValidByUserId(mockUserId))
        .expectNext(mockSubmission1).expectNext(mockSubmission2).expectNext(mockSubmission3)
        .expectNext(mockSubmission4).expectComplete().verify();

    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  public void findAllValidByUserIdAndModuleId_InvalidModuleId_ReturnsInvalidUserIdException() {
    final long mockUserId = 671L;
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidByUserIdAndModuleId(mockUserId, moduleId))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void findAllValidByUserIdAndModuleId_InvalidUserId_ReturnsInvalidUserIdException() {
    final long mockModuleId = 366L;
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidByUserIdAndModuleId(userId, mockModuleId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findAllValidByUserIdAndModuleId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 648L;
    final long mockModuleId = 283L;
    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.empty());
    StepVerifier.create(submissionService.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .expectComplete().verify();
    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
  }

  @Test
  public void findAllValidByUserIdAndModuleId_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 864L;
    final long mockModuleId = 36L;
    final Submission mockSubmission = mock(Submission.class);

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.just(mockSubmission));
    StepVerifier.create(submissionService.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .expectNext(mockSubmission).expectComplete().verify();

    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
  }

  @Test
  public void findAllValidIdsByUserId_InvalidUserId_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.findAllValidIdsByUserId(userId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void findAllValidIdsByUserId_NoSubmissionsExist_ReturnsEmpty() {
    final long mockUserId = 508L;
    when(submissionRepository.findAllValidByUserId(mockUserId)).thenReturn(Flux.empty());
    StepVerifier.create(submissionService.findAllValidIdsByUserId(mockUserId))
        .expectNext(new ArrayList<Long>()).expectComplete().verify();
    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  @Test
  public void findAllValidIdsByUserId_SubmissionsExist_ReturnsSubmissions() {
    final long mockUserId = 237L;
    final Submission mockSubmission1 = mock(Submission.class);
    final Submission mockSubmission2 = mock(Submission.class);
    final Submission mockSubmission3 = mock(Submission.class);
    final Submission mockSubmission4 = mock(Submission.class);

    final long moduleId1 = 164L;
    when(mockSubmission1.getModuleId()).thenReturn(moduleId1);

    final long moduleId2 = 310L;
    when(mockSubmission2.getModuleId()).thenReturn(moduleId2);

    final long moduleId3 = 783;
    when(mockSubmission3.getModuleId()).thenReturn(moduleId3);

    final long moduleId4 = 499L;
    when(mockSubmission4.getModuleId()).thenReturn(moduleId4);

    final List<Long> moduleIdList =
        Arrays.asList(new Long[] {moduleId1, moduleId2, moduleId3, moduleId4});

    when(submissionRepository.findAllValidByUserId(mockUserId))
        .thenReturn(Flux.just(mockSubmission1, mockSubmission2, mockSubmission3, mockSubmission4));
    StepVerifier.create(submissionService.findAllValidIdsByUserId(mockUserId))
        .expectNext(moduleIdList).expectComplete().verify();

    verify(submissionRepository, times(1)).findAllValidByUserId(mockUserId);
  }

  private void setClock(final Clock clock) {
    // Set up the system under test
    submissionService.setClock(clock);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    submissionService = new SubmissionService(submissionRepository, flagHandler);
  }

  @Test
  public void submit_InvalidFlag_ReturnsInvalidSubmission() {
    final long mockUserId = 293L;
    final long mockModuleId = 800L;
    final long mockSubmissionId = 353L;

    final String flag = "invalidFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleId, flag)).thenReturn(Mono.just(false));

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class))).thenAnswer(
        user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleId, flag))
        .assertNext(submission -> {
          assertThat(submission.getId(), is(mockSubmissionId));
          assertThat(submission.getUserId(), is(mockUserId));
          assertThat(submission.getModuleId(), is(mockModuleId));
          assertThat(submission.getFlag(), is(flag));
          assertThat(submission.getTime(), is(LocalDateTime.now(fixedClock)));
          assertThat(submission.isValid(), is(false));
        }).expectComplete().verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleId, flag);
    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }

  @Test
  public void submit_InvalidModuleId_ReturnsInvalidUserIdException() {
    final long mockUserId = 934L;
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submit(mockUserId, moduleId, "flag"))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void submit_InvalidUserId_ReturnsInvalidUserIdException() {
    final long mockModuleId = 201L;
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submit(userId, mockModuleId, "flag"))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void submit_ModuleAlreadySolvedByUser_ReturnsModuleAlreadySolvedException() {
    final long mockUserId = 293L;
    final long mockModuleId = 800L;
    final String flag = "validFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleId, flag)).thenReturn(Mono.just(true));

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.just(mock(Submission.class)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleId, flag))
        .expectError(ModuleAlreadySolvedException.class).verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleId, flag);
    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
  }

  @Test
  public void submit_ValidFlag_ReturnsValidSubmission() {
    final long mockUserId = 293L;
    final long mockModuleId = 800L;
    final long mockSubmissionId = 353L;

    final String flag = "validFlag";

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(flagHandler.verifyFlag(mockUserId, mockModuleId, flag)).thenReturn(Mono.just(true));

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class))).thenAnswer(
        user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submit(mockUserId, mockModuleId, flag))
        .assertNext(submission -> {
          assertThat(submission.getId(), is(mockSubmissionId));
          assertThat(submission.getUserId(), is(mockUserId));
          assertThat(submission.getModuleId(), is(mockModuleId));
          assertThat(submission.getFlag(), is(flag));
          assertThat(submission.getTime(), is(LocalDateTime.now(fixedClock)));
          assertThat(submission.isValid(), is(true));
        }).expectComplete().verify();

    verify(flagHandler, times(1)).verifyFlag(mockUserId, mockModuleId, flag);
    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }

  @Test
  public void submitValid_InvalidModuleId_ReturnsInvalidUserIdException() {
    final long mockUserId = 348L;
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submitValid(mockUserId, moduleId))
          .expectError(InvalidModuleIdException.class).verify();
    }
  }

  @Test
  public void submitValid_InvalidUserId_ReturnsInvalidUserIdException() {
    final long mockModuleId = 160L;
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(submissionService.submitValid(userId, mockModuleId))
          .expectError(InvalidUserIdException.class).verify();
    }
  }

  @Test
  public void submitValid_ModuleAlreadySolvedByUser_ReturnsModuleAlreadySolvedException() {
    final long mockUserId = 743L;
    final long mockModuleId = 276L;

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.just(mock(Submission.class)));

    StepVerifier.create(submissionService.submitValid(mockUserId, mockModuleId))
        .expectError(ModuleAlreadySolvedException.class).verify();

    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
  }

  @Test
  public void submitValid_ModuleNotAlreadySolved_ReturnsValidSubmission() {
    final long mockUserId = 293L;
    final long mockModuleId = 800L;
    final long mockSubmissionId = 353L;

    final Clock fixedClock = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    setClock(fixedClock);

    when(submissionRepository.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.empty());

    when(submissionRepository.save(any(Submission.class))).thenAnswer(
        user -> Mono.just(user.getArgument(0, Submission.class).withId(mockSubmissionId)));

    StepVerifier.create(submissionService.submitValid(mockUserId, mockModuleId))
        .assertNext(submission -> {
          assertThat(submission.getId(), is(mockSubmissionId));
          assertThat(submission.getUserId(), is(mockUserId));
          assertThat(submission.getModuleId(), is(mockModuleId));
          assertThat(submission.getFlag(), is(nullValue()));
          assertThat(submission.getTime(), is(LocalDateTime.now(fixedClock)));
          assertThat(submission.isValid(), is(true));
        }).expectComplete().verify();

    verify(submissionRepository, times(1)).findAllValidByUserIdAndModuleId(mockUserId,
        mockModuleId);
    verify(submissionRepository, times(1)).save(any(Submission.class));
  }
}
