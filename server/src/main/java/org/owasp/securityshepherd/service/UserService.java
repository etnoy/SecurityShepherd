package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.persistence.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.persistence.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.persistence.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@RequiredArgsConstructor
@Service
public final class UserService {

	private final UserRepository userRepository;

	private final AuthRepository authRepository;

	private final PasswordAuthRepository passwordAuthRepository;

	private final KeyService keyService;

	public Mono<User> create(final String displayName) {

		if (displayName == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty()) {
			throw new IllegalArgumentException();
		}

		return Mono.just(displayName).filterWhen(name -> doesNotExistByDisplayName(name))
				.switchIfEmpty(Mono.error(new DuplicateUserDisplayNameException("Display name already exists")))
				.flatMap(name -> userRepository.save(User.builder().displayName(name).build()));

	}

	private Mono<Boolean> doesNotExistByDisplayName(final String displayName) {
		return userRepository.findByDisplayName(displayName).map(u -> false).defaultIfEmpty(true);
	}

	private Mono<Boolean> doesNotExistByLoginName(final String loginName) {
		return passwordAuthRepository.findByLoginName(loginName).map(u -> false).defaultIfEmpty(true);
	}

	public Mono<User> createPasswordUser(final String displayName, final String loginName, final String hashedPassword) {

		if (displayName == null || loginName == null || hashedPassword == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
			throw new IllegalArgumentException();
		}

		final Mono<String> loginNameMono = Mono.just(loginName).filterWhen(this::doesNotExistByLoginName)
				.switchIfEmpty(Mono.error(new DuplicateUserLoginNameException("Login name already exists")));

		final Mono<String> displayNameMono = Mono.just(displayName).filterWhen(this::doesNotExistByDisplayName)
				.switchIfEmpty(Mono.error(new DuplicateUserDisplayNameException("Display name already exists")));

		final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
		passwordAuthBuilder.loginName(loginName);
		passwordAuthBuilder.hashedPassword(hashedPassword);
		final PasswordAuth passwordAuth = passwordAuthBuilder.build();

		final AuthBuilder userAuthBuilder = Auth.builder();
		userAuthBuilder.password(passwordAuth);
		final Auth auth = userAuthBuilder.build();

		final UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);
		
		return Mono.zip(displayNameMono, loginNameMono).flatMap(dto -> {

			final Mono<User> user = userRepository.save(userBuilder.build()).flatMap(savedUser -> {

				final PasswordAuth passwordAuthWithUser = passwordAuth.withUser(savedUser.getId());
				final Auth authWithUser = auth.withUser(savedUser.getId());

				passwordAuthRepository.save(passwordAuthWithUser);
				authRepository.save(authWithUser);

				return Mono.just(savedUser);

			});

			return user;

		});

	}

	public void setDisplayName(final int userId, final String displayName) {

		Mono<String> displayMono = Mono.just(displayName).filterWhen(name -> doesNotExistByDisplayName(name))
				.switchIfEmpty(Mono.error(new DuplicateUserDisplayNameException("Display name already exists")));

		Mono.just(userId).flatMap(id -> {
			try {
				return get(id);
			} catch (InvalidUserIdException e) {
				return Mono.error(e);
			}
		}).zipWith(displayMono).map(tuple -> tuple.getT1().withDisplayName(tuple.getT2()))
				.flatMap(userRepository::save);

	}

	public void setClassId(final int id, final int classId)
			throws ClassIdNotFoundException, InvalidUserIdException, UserIdNotFoundException, InvalidClassIdException {

		// TODO put this check back in reactive form
//		if (!classService.existsById(classId)) {
//
//			throw new ClassIdNotFoundException();
//
//		}

//		final Mono<Tuple2<User, Integer>> idDisplayNameMono = idMono.flatMap(userId -> {
//			try {
//				return get(userId);
//			} catch (InvalidUserIdException e) {
//				return Mono.error(e);
//			}
//		})
//				.switchIfEmpty(Mono.error(new UserIdNotFoundException())).zipWith(classIdMono);
//
//		idDisplayNameMono.flatMap(pair -> userRepository.save(pair.getT1().withClassId(pair.getT2())));

	}

	public Mono<Long> count() {

		return userRepository.count();

	}

	public Mono<byte[]> getKey(final int userId) throws UserIdNotFoundException, InvalidUserIdException {
		return null;

//		final Mono<User> returnedUser = idMono.flatMap(this::get)
//				.switchIfEmpty(Mono.error(new UserIdNotFoundException()));
//
//		returnedUser.filter(user -> user.getKey() == null).map(user -> user.withKey(keyService.generateRandomBytes(16)))
//				.map(user -> userRepository.save(user));
//
//		return returnedUser.map(user -> user.getKey());

	}

	public Mono<User> findByLoginName(final String loginName) {

		return passwordAuthRepository.findByLoginName(loginName).map(pwAuth -> pwAuth.getUser()).flatMap(userId -> {
			try {
				return this.get(userId);
			} catch (InvalidUserIdException e) {
				return Mono.error(e);
			}
		});

	}

	public Mono<User> get(final int id) throws InvalidUserIdException {

		if (id <= 0) {
			throw new InvalidUserIdException();
		}

		Mono<User> user = userRepository.findById(id);
		Mono<PasswordAuth> passwordAuth = passwordAuthRepository.findByUserId(id);
		Mono<Auth> auth = authRepository.findByUserId(id);

		return user.zipWith(auth.zipWith(passwordAuth).map(tuple -> tuple.getT1().withPassword(tuple.getT2())))
				.map(tuple -> tuple.getT1().withAuth(tuple.getT2()));

	}

}