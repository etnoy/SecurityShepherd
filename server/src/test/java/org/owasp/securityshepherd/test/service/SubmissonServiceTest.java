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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Clock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.PasswordAuth;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserAuth;
import org.owasp.securityshepherd.user.UserAuthRepository;
import org.owasp.securityshepherd.user.UserRepository;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

  @Mock
  private Clock clock;

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

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    submissionService =
        new SubmissionService(submissionRepository, correctionRepository, flagHandler, clock);
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
}
