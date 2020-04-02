package org.owasp.securityshepherd.test.util;

import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.ScoreService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public final class TestService {

  private final UserService userService;

  private final ClassService classService;

  private final ModuleService moduleService;

  private final SubmissionService submissionService;

  private final ScoreService scoreService;

  public Mono<Void> deleteAll() {
    // Clear all users and modules from repository before every test
    return
    // Delete all scores
    scoreService.deleteAll()
        // Delete all submissions
        .then(submissionService.deleteAll())
        // Delete all classes
        .then(classService.deleteAll())
        // Delete all modules
        .then(moduleService.deleteAll())
        // Delete all users
        .then(userService.deleteAll());
  }
}
