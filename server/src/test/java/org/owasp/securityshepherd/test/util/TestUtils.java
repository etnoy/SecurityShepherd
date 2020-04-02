package org.owasp.securityshepherd.test.util;

import org.owasp.securityshepherd.repository.ClassRepository;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserAuthRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public final class TestUtils {

  public static final int[] INVALID_IDS = {-1, -1000, 0, -1234567, -500};

  public static final boolean[] BOOLEANS = {false, true};

  private final UserRepository userRepository;

  private final PasswordAuthRepository passwordAuthRepository;

  private final ConfigurationRepository configurationRepository;

  private final ClassRepository classRepository;

  private final ModuleRepository moduleRepository;

  private final SubmissionRepository submissionRepository;

  private final CorrectionRepository correctionRepository;

  private final ModulePointRepository modulePointRepository;

  private final UserAuthRepository userAuthRepository;

  public Mono<Void> deleteAll() {
    // Deleting data must be done in the right order due to db constraints
    return
    // Delete all score corrections
    correctionRepository.deleteAll()
        // Delete all module scoring rules
        .then(modulePointRepository.deleteAll())
        // Delete all submissions
        .then(submissionRepository.deleteAll())
        // Delete all classes
        .then(classRepository.deleteAll())
        // Delete all modules
        .then(moduleRepository.deleteAll())
        // Delete all configuration
        .then(configurationRepository.deleteAll())
        // Delete all password auth data
        .then(passwordAuthRepository.deleteAll())
        // Delete all user auth data
        .then(userAuthRepository.deleteAll())
        // Delete all users
        .then(userRepository.deleteAll());
  }
}
