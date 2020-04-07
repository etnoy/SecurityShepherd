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

  public static final long[] INVALID_IDS = {-1, -1000, 0, -1234567, -500};

  public static final boolean[] BOOLEANS = {false, true};

  public static final Long INITIAL_LONG = 0L;
  
  public static final Long[] LONGS = {0L, 1L, -1L, 100L, -100L, 1000L, -1000L,
      Long.valueOf(Integer.MAX_VALUE) + 1, Long.valueOf(Integer.MIN_VALUE - 1), 123456789L,
      -12346789L, Long.MAX_VALUE, Long.MIN_VALUE};

  public static final Long[] LONGS_WITH_NULL = {null, 0L, 1L, -1L, 100L, -100L, 1000L, -1000L,
      Long.valueOf(Integer.MAX_VALUE) + 1, Long.valueOf(Integer.MIN_VALUE - 1), 123456789L,
      -12346789L, Long.MAX_VALUE, Long.MIN_VALUE};

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
