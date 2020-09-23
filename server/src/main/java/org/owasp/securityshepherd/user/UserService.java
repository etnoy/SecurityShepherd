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
package org.owasp.securityshepherd.user;

import java.time.LocalDateTime;

import org.owasp.securityshepherd.authentication.PasswordAuth;
import org.owasp.securityshepherd.authentication.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.authentication.PasswordAuthRepository;
import org.owasp.securityshepherd.authentication.UserAuth;
import org.owasp.securityshepherd.authentication.UserAuthRepository;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.user.User.UserBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

	private final UserAuthRepository userAuthRepository;

	private final PasswordAuthRepository passwordAuthRepository;

	private final ClassService classService;

	private final KeyService keyService;

	public Mono<Long> count() {
		return userRepository.count();
	}

	public Mono<Boolean> authenticate(final String username, final String password) {
		if (username == null) {
			return Mono.error(new NullPointerException());
		}
		if (password == null) {
			return Mono.error(new NullPointerException());
		}
		if (username.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}
		if (password.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}
		// Initialize the encoder
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

		return
		// Find the password auth
		findPasswordAuthByLoginName(username)
				// Extract the password hash
				.map(PasswordAuth::getHashedPassword)
				// Check if hash matches
				.map(hashedPassword -> encoder.matches(password, hashedPassword)).defaultIfEmpty(false);
	}

	public Flux<SimpleGrantedAuthority> getAuthoritiesByUserId(final long userId) {
		if (userId <= 0) {
			return Flux.error(new InvalidUserIdException());
		}
		return findUserAuthByUserId(userId).filter(UserAuth::isAdmin)
				.map(userAuth -> new SimpleGrantedAuthority("ROLE_ADMIN")).flux()
				.concatWithValues(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public Mono<Long> create(final String displayName) {
		if (displayName == null) {
			return Mono.error(new NullPointerException());
		}

		if (displayName.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}

		log.info("Creating new user with display name " + displayName);

		return Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
				.switchIfEmpty(displayNameAlreadyExists(displayName))
				.flatMap(name -> userRepository.save(User.builder().displayName(name)
						.key(keyService.generateRandomBytes(16)).accountCreated(LocalDateTime.now()).build()))
				.map(User::getId);
	}

	public Mono<Long> createPasswordUser(final String displayName, final String loginName,
			final String hashedPassword) {
		if (displayName == null) {
			return Mono.error(new NullPointerException("Display name cannot be null"));
		}

		if (loginName == null) {
			return Mono.error(new NullPointerException("Login name cannot be null"));
		}

		if (hashedPassword == null) {
			return Mono.error(new NullPointerException("Password hash cannot be null"));
		}

		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}

		log.info("Creating new password login user with display name " + displayName + " and login name " + loginName);

		final Mono<String> loginNameMono = Mono.just(loginName).filterWhen(this::doesNotExistByLoginName)
				.switchIfEmpty(loginNameAlreadyExists(loginName));

		final Mono<String> displayNameMono = Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
				.switchIfEmpty(displayNameAlreadyExists(displayName));

		return Mono.zip(displayNameMono, loginNameMono).flatMap(tuple -> {
			final UserBuilder userBuilder = User.builder();
			userBuilder.displayName(tuple.getT1()).key(keyService.generateRandomBytes(16))
					.accountCreated(LocalDateTime.now());

			final Mono<Long> userIdMono = userRepository.save(userBuilder.build()).map(User::getId);

			final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
			passwordAuthBuilder.loginName(tuple.getT2());
			passwordAuthBuilder.hashedPassword(hashedPassword);

			return userIdMono.delayUntil(userId -> {
				Mono<UserAuth> userAuthMono = userAuthRepository.save(UserAuth.builder().userId(userId).build());

				Mono<PasswordAuth> passwordAuthMono = passwordAuthRepository
						.save(passwordAuthBuilder.userId(userId).build());

				return Mono.when(userAuthMono, passwordAuthMono);
			});
		});
	}

	public Mono<Void> deleteById(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}
		return passwordAuthRepository.deleteByUserId(userId).then(userAuthRepository.deleteByUserId(userId))
				.then(userRepository.deleteById(userId));
	}

	public Mono<Void> demote(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		log.info("Demoting user with id " + userId + " to user");

		return findUserAuthByUserId(userId).map(userAuth -> userAuth.withAdmin(false)).flatMap(userAuthRepository::save)
				.then();
	}

	private Mono<String> displayNameAlreadyExists(final String displayName) {
		return Mono.error(new DuplicateUserDisplayNameException("Display name " + displayName + " already exists"));
	}

	private Mono<Boolean> doesNotExistByDisplayName(final String displayName) {
		return userRepository.findByDisplayName(displayName).map(u -> false).defaultIfEmpty(true);
	}

	private Mono<Boolean> doesNotExistByLoginName(final String loginName) {
		return passwordAuthRepository.findByLoginName(loginName).map(u -> false).defaultIfEmpty(true);
	}

	public Flux<User> findAll() {
		return userRepository.findAll();
	}

	public Mono<User> findById(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		return userRepository.findById(userId);
	}

	public Mono<String> findDisplayNameById(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}
		return userRepository.findById(userId).map(User::getDisplayName);
	}

	public Mono<byte[]> findKeyById(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		return Mono.just(userId).filterWhen(userRepository::existsById)
				.switchIfEmpty(Mono.error(new UserIdNotFoundException())).flatMap(userRepository::findById)
				.flatMap(user -> {
					final byte[] key = user.getKey();
					if (key == null) {
						return userRepository.save(user.withKey(keyService.generateRandomBytes(16))).map(User::getKey);
					}
					return Mono.just(key);
				});
	}

	public Mono<PasswordAuth> findPasswordAuthByLoginName(final String loginName) {
		if (loginName == null) {
			return Mono.error(new NullPointerException());
		}
		if (loginName.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}
		return passwordAuthRepository.findByLoginName(loginName);
	}

	public Mono<PasswordAuth> findPasswordAuthByUserId(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		return passwordAuthRepository.findByUserId(userId);
	}

	public Mono<UserAuth> findUserAuthByUserId(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		return userAuthRepository.findByUserId(userId);
	}

	public Mono<Long> findUserIdByLoginName(final String loginName) {
		if (loginName == null) {
			return Mono.error(new NullPointerException());
		}
		if (loginName.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}

		return passwordAuthRepository.findByLoginName(loginName).map(PasswordAuth::getUserId);
	}

	private Mono<String> loginNameAlreadyExists(final String loginName) {
		return Mono.error(new DuplicateUserLoginNameException("Login name " + loginName + " already exists"));
	}

	public Mono<Void> promote(final long userId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		log.info("Promoting user with id " + userId + " to admin");

		return findUserAuthByUserId(userId).map(userAuth -> userAuth.withAdmin(true)).flatMap(userAuthRepository::save)
				.then();
	}

	public Mono<User> setClassId(final long userId, final long classId) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		if (classId <= 0) {
			return Mono.error(new InvalidClassIdException());
		}

		final Mono<Long> classIdMono = Mono.just(classId).filterWhen(classService::existsById)
				.switchIfEmpty(Mono.error(new ClassIdNotFoundException()));

		return Mono.just(userId).flatMap(this::findById).zipWith(classIdMono)
				.map(tuple -> tuple.getT1().withClassId(tuple.getT2())).flatMap(userRepository::save);
	}

	public Mono<User> setDisplayName(final long userId, final String displayName) {
		if (userId <= 0) {
			return Mono.error(new InvalidUserIdException());
		}

		if (displayName == null) {
			return Mono.error(new NullPointerException());
		}

		if (displayName.isEmpty()) {
			return Mono.error(new IllegalArgumentException());
		}

		log.info("Setting display name of user id " + userId + " to " + displayName);

		final Mono<String> displayNameMono = Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
				.switchIfEmpty(displayNameAlreadyExists(displayName));

		return Mono.just(userId).filterWhen(userRepository::existsById)
				.switchIfEmpty(Mono.error(new UserIdNotFoundException())).flatMap(this::findById)
				.zipWith(displayNameMono).map(tuple -> tuple.getT1().withDisplayName(tuple.getT2()))
				.flatMap(userRepository::save);
	}
}
