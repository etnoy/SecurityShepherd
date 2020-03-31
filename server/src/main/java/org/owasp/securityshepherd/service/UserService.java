package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.model.UserAuth;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserDatabaseClient;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.security.ShepherdUserDetails;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class UserService {

  private final UserRepository userRepository;

  private final UserDatabaseClient userDatabaseClient;

  private final AuthRepository authRepository;

  private final PasswordAuthRepository passwordAuthRepository;

  private final ClassService classService;

  private final KeyService keyService;

  public Mono<Long> count() {
    return userRepository.count();
  }

  public Mono<ShepherdUserDetails> findUserDetailsByUserId(final int userId) {

    final Mono<UserAuth> userAuthMono = Mono.just(userId).flatMap(this::findAuthByUserId);
    final Mono<PasswordAuth> passwordAuthMono =
        Mono.just(userId).flatMap(this::findPasswordAuthByUserId);

    return Mono.zip(userAuthMono, passwordAuthMono, ShepherdUserDetails::new);
  }

  public Mono<ShepherdUserDetails> findUserDetailsByLoginName(final String loginName) {
    return findUserIdByLoginName(loginName).flatMap(this::findUserDetailsByUserId);
  }

  public Mono<Integer> create(final String displayName) {
    if (displayName == null) {
      throw new NullPointerException();
    }

    if (displayName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    log.info("Creating new user with display name " + displayName);

    return Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
        .switchIfEmpty(displayNameAlreadyExists(displayName))
        .flatMap(name -> userRepository.save(User.builder().displayName(name).build()))
        .map(User::getId);
  }

  public Mono<Integer> createPasswordUser(final String displayName, final String loginName,
      final String hashedPassword) {
    if (displayName == null) {
      throw new NullPointerException("Display name cannot be null");
    }

    if (loginName == null) {
      throw new NullPointerException("Login name cannot be null");
    }

    if (hashedPassword == null) {
      throw new NullPointerException("Password hash cannot be null");
    }

    if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
      throw new IllegalArgumentException();
    }

    log.info("Creating new password login user with display name " + displayName
        + " and login name " + loginName);

    final Mono<String> loginNameMono = Mono.just(loginName)
        .filterWhen(this::doesNotExistByLoginName).switchIfEmpty(loginNameAlreadyExists(loginName));

    final Mono<String> displayNameMono =
        Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
            .switchIfEmpty(displayNameAlreadyExists(displayName));

    return Mono.zip(displayNameMono, loginNameMono).flatMap(tuple -> {

      final UserBuilder userBuilder = User.builder();
      userBuilder.displayName(tuple.getT1());

      final Mono<Integer> userIdMono = userRepository.save(userBuilder.build()).map(User::getId);

      final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
      passwordAuthBuilder.loginName(tuple.getT2());
      passwordAuthBuilder.hashedPassword(hashedPassword);

      return userIdMono.delayUntil(userId -> {
        Mono<UserAuth> userAuthMono =
            authRepository.save(UserAuth.builder().userId(userId).build());

        Mono<PasswordAuth> passwordAuthMono =
            passwordAuthRepository.save(passwordAuthBuilder.userId(userId).build());

        return Mono.when(userAuthMono, passwordAuthMono);
      });
    });
  }

  public Mono<String> findDisplayNameById(final int userId) {
    return userRepository.findById(userId).map(User::getDisplayName);
  }

  public Mono<Void> deleteAll() {
    return passwordAuthRepository.deleteAll().then(authRepository.deleteAll())
        .then(userRepository.deleteAll());
  }

  public Mono<Void> deleteById(final int userId) {
    return userRepository.findById(userId).zipWith(findAuthByUserId(userId))
        .flatMap(tuple -> userRepository.delete(tuple.getT1()));
  }

  public Mono<Void> demote(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    log.info("Demoting user with id " + userId + " to user");

    return findAuthByUserId(userId).map(userAuth -> userAuth.withAdmin(false))
        .flatMap(authRepository::save).then();
  }

  private Mono<String> displayNameAlreadyExists(final String displayName) {
    return Mono.error(
        new DuplicateUserDisplayNameException("Display name " + displayName + " already exists"));
  }

  private Mono<Boolean> doesNotExistByDisplayName(final String displayName) {
    return userRepository.findByDisplayName(displayName).map(u -> false).defaultIfEmpty(true);
  }

  private Mono<Boolean> doesNotExistByLoginName(final String loginName) {
    return passwordAuthRepository.findByLoginName(loginName).map(u -> false).defaultIfEmpty(true);
  }

  public Flux<User> findAll() {
    return userRepository.findAll().flatMap(user -> findById(user.getId()));
  }

  public Mono<User> findById(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    return userRepository.findById(userId);
  }

  public Mono<Integer> findUserIdByLoginName(final String loginName) {
    if (loginName == null) {
      throw new NullPointerException();
    }

    if (loginName.isEmpty()) {
      // TODO: custom exception message
      throw new IllegalArgumentException();
    }
    return userDatabaseClient.findUserIdByLoginName(loginName);
  }

  public Mono<UserAuth> findAuthByUserId(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    return authRepository.findByUserId(userId);
  }

  public Mono<PasswordAuth> findPasswordAuthByUserId(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    return passwordAuthRepository.findByUserId(userId);
  }

  public Mono<byte[]> getKeyById(final int id) {
    if (id <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    return Mono.just(id).filterWhen(userRepository::existsById)
        .switchIfEmpty(Mono.error(new UserIdNotFoundException())).flatMap(userRepository::findById)
        .flatMap(user -> {
          final byte[] key = user.getKey();
          if (key == null) {
            return keyService.generateRandomBytes(16).map(user::withKey)
                .flatMap(userRepository::save).map(User::getKey);
          }
          return Mono.just(key);
        });
  }

  private Mono<String> loginNameAlreadyExists(final String loginName) {
    return Mono
        .error(new DuplicateClassNameException("Login name " + loginName + " already exists"));
  }

  public Mono<Void> promote(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    log.info("Promoting user with id " + userId + " to admin");

    return findAuthByUserId(userId).map(userAuth -> userAuth.withAdmin(true))
        .flatMap(authRepository::save).then();
  }

  public Mono<User> setClassId(final int userId, final int classId)
      throws InvalidUserIdException, InvalidClassIdException {

    if (userId <= 0) {
      throw new InvalidUserIdException();
    }

    if (classId <= 0) {
      throw new InvalidClassIdException();
    }

    final Mono<Integer> classIdMono = Mono.just(classId).filterWhen(classService::existsById)
        .switchIfEmpty(Mono.error(new ClassIdNotFoundException()));

    return Mono.just(userId).flatMap(this::findById).zipWith(classIdMono)
        .map(tuple -> tuple.getT1().withClassId(tuple.getT2())).flatMap(userRepository::save);
  }

  public Mono<User> setDisplayName(final int userId, final String displayName)
      throws InvalidUserIdException {

    if (userId <= 0) {
      throw new InvalidUserIdException();
    }

    if (displayName == null) {
      throw new NullPointerException();
    }

    if (displayName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    log.info("Setting display name of user id " + userId + " to " + displayName);

    final Mono<String> displayNameMono =
        Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
            .switchIfEmpty(displayNameAlreadyExists(displayName));

    return Mono.just(userId).filterWhen(userRepository::existsById)
        .switchIfEmpty(Mono.error(new UserIdNotFoundException())).flatMap(this::findById)
        .zipWith(displayNameMono).map(tuple -> tuple.getT1().withDisplayName(tuple.getT2()))
        .flatMap(userRepository::save);
  }
}
