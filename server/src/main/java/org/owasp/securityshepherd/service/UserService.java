package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserRepository;
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

  private final AuthRepository authRepository;

  private final PasswordAuthRepository passwordAuthRepository;

  private final ClassService classService;

  private final KeyService keyService;

  public Mono<Long> count() {
    return userRepository.count();
  }

  public Mono<User> create(final String displayName) {
    if (displayName == null) {
      throw new NullPointerException();
    }

    if (displayName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    log.info("Creating new user with display name " + displayName);

    return Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
        .switchIfEmpty(displayNameAlreadyExists(displayName))
        .flatMap(name -> userRepository.save(User.builder().displayName(name).build()));
  }

  public Mono<User> createPasswordUser(final String displayName, final String loginName,
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
      final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
      passwordAuthBuilder.loginName(tuple.getT2());
      passwordAuthBuilder.hashedPassword(hashedPassword);
      final PasswordAuth passwordAuth = passwordAuthBuilder.build();

      final AuthBuilder userAuthBuilder = Auth.builder();
      userAuthBuilder.password(passwordAuth);
      final Auth auth = userAuthBuilder.build();

      final UserBuilder userBuilder = User.builder();
      userBuilder.displayName(tuple.getT1());
      userBuilder.auth(auth);

      return userRepository.save(userBuilder.build()).flatMap(userWithId -> {

        final PasswordAuth passwordAuthWithUser = passwordAuth.withUserId(userWithId.getId());

        final Mono<PasswordAuth> passwordAuthMono =
            passwordAuthRepository.save(passwordAuthWithUser);

        final Auth authWithUser = auth.withUserId(userWithId.getId());

        final Mono<Auth> authMono = authRepository.save(authWithUser).zipWith(passwordAuthMono)
            .map(authTuple -> authTuple.getT1().withPassword(authTuple.getT2()));

        return Mono.just(userWithId).zipWith(authMono)
            .map(userTuple -> userTuple.getT1().withAuth(userTuple.getT2()));
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
    return userRepository.findById(userId).zipWith(getAuth(userId))
        .flatMap(tuple -> userRepository.delete(tuple.getT1()));
  }

  public Mono<User> demote(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    log.info("Demoting user with id " + userId + " to user");

    return setAdminStatus(userId, false);
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

    final Mono<User> userMono = userRepository.findById(userId);

    return userMono.zipWith(getAuth(userId)).map(tuple -> tuple.getT1().withAuth(tuple.getT2()))
        .switchIfEmpty(userMono);
  }

  public Mono<User> findByLoginName(final String loginName) {
    if (loginName == null) {
      throw new NullPointerException();
    }

    if (loginName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    return passwordAuthRepository.findByLoginName(loginName).map(PasswordAuth::getUserId)
        .flatMap(this::findById);
  }

  private Mono<Auth> getAuth(final int id) {
    if (id <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    final Mono<PasswordAuth> passwordAuth = Mono.just(id).flatMap(userId -> {
      final Mono<PasswordAuth> returnedPasswordAuth = passwordAuthRepository.findByUserId(userId);
      if (returnedPasswordAuth == null) {
        return Mono.empty();
      }
      return returnedPasswordAuth;
    });

    final Mono<Auth> auth = Mono.just(id).flatMap(userId -> {
      final Mono<Auth> returnedAuth = authRepository.findByUserId(userId);
      if (returnedAuth == null) {
        return Mono.empty();
      }
      return returnedAuth;
    });

    return auth.zipWith(passwordAuth).map(tuple -> tuple.getT1().withPassword(tuple.getT2()))
        .switchIfEmpty(auth);
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

  public Mono<User> promote(final int userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    log.info("Promoting user with id " + userId + " to admin");

    return setAdminStatus(userId, true);
  }

  private Mono<User> setAdminStatus(final int userId, final boolean isAdmin) {
    final Mono<User> userMono = findById(userId);

    final Mono<Auth> authMono =
        userMono.map(user -> user.getAuth().withAdmin(isAdmin)).flatMap(authRepository::save);

    return userMono.zipWith(authMono).map(tuple -> tuple.getT1().withAuth(tuple.getT2()));
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
