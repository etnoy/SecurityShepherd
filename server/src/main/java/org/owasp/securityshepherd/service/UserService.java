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

	private final KeyService keyService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Mono<User> create(final String displayName) throws DuplicateUserDisplayNameException {

		if (displayName == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Starting up");

		log.debug("Exists equals " + doesNotExistByDisplayName("test12345").block());
		log.debug("Exists equals " + doesNotExistByDisplayName("test").block());

		final UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);
		final User newUser = userBuilder.build();

		return Mono.just(newUser).filterWhen(user -> doesNotExistByDisplayName(user.getDisplayName()))
				.switchIfEmpty(Mono.error(new DuplicateUserDisplayNameException("Display name already exists")))
				.flatMap(userRepository::save);

	}

	public Mono<Boolean> doesNotExistByDisplayName(final String displayName) {
		return userRepository.findByDisplayName(displayName).map(u -> false).defaultIfEmpty(true);
	}

	public Mono<User> createPasswordUser(final UserDto userDto)
			throws DuplicateUserLoginNameException, DuplicateUserDisplayNameException {
		return null;
//
//		// Check if display name exists
//		if (userRepository.existsByDisplayName(userDto.getDisplayName())) {
//			throw new DuplicateUserDisplayNameException();
//		}
//
//		// Check if login name exists
//		if (userRepository.existsByLoginName(userDto.getLoginName())) {
//			throw new DuplicateUserLoginNameException();
//		}
//
//		log.debug("Creating password login user with display name " + userDto.getDisplayName());
//
//		final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
//		passwordAuthBuilder.loginName(userDto.getLoginName());
//		passwordAuthBuilder.hashedPassword(passwordEncoder.encode(userDto.getPassword()));
//
//		final AuthBuilder userAuthBuilder = Auth.builder();
//		// userAuthBuilder.password(passwordAuthBuilder.build());
//
//		final UserBuilder userBuilder = User.builder();
//		userBuilder.displayName(userDto.getDisplayName());
//		userBuilder.email(userDto.getEmail());
//		// userBuilder.auth(userAuthBuilder.build());
//
//		final Mono<User> savedUser = userRepository.save(userBuilder.build());
//
//		// log.debug("Created user with ID " + savedUser.getId());
//
//		return savedUser;

	}

	public void setDisplayName(final Mono<Integer> idMono, final Mono<String> displayNameMono)
			throws UserIdNotFoundException, InvalidUserIdException {

		final Mono<Tuple2<User, String>> idDisplayNameMono = idMono.flatMap(this::get)
				.switchIfEmpty(Mono.error(new UserIdNotFoundException())).zipWith(displayNameMono);

		idDisplayNameMono.flatMap(pair -> userRepository.save(pair.getT1().withDisplayName(pair.getT2())));

	}

	public void setClassId(final Mono<Integer> idMono, final Mono<Integer> classIdMono)
			throws ClassIdNotFoundException, InvalidUserIdException, UserIdNotFoundException, InvalidClassIdException {

		// TODO put this check back in reactive form
//		if (!classService.existsById(classId)) {
//
//			throw new ClassIdNotFoundException();
//
//		}

		final Mono<Tuple2<User, Integer>> idDisplayNameMono = idMono.flatMap(this::get)
				.switchIfEmpty(Mono.error(new UserIdNotFoundException())).zipWith(classIdMono);

		idDisplayNameMono.flatMap(pair -> userRepository.save(pair.getT1().withClassId(pair.getT2())));

	}

	public Mono<Long> count() {

		return userRepository.count();

	}

	public Mono<byte[]> getKey(final Mono<Integer> idMono) throws UserIdNotFoundException, InvalidUserIdException {

		final Mono<User> returnedUser = idMono.flatMap(this::get)
				.switchIfEmpty(Mono.error(new UserIdNotFoundException()));

		returnedUser.filter(user -> user.getKey() == null).map(user -> user.withKey(keyService.generateRandomBytes(16)))
				.map(user -> userRepository.save(user));

		return returnedUser.map(user -> user.getKey());

	}

	public Mono<User> findByLoginName(final String loginName) {
		return null;

		// return userRepository.findByLoginName(loginName);

	}

	public Mono<User> get(final int id) {

		return userRepository.findById(id);

	}

	public Mono<User> createPasswordUser(final String displayName, final String loginName, final String hashedPassword)
			throws DuplicateUserLoginNameException, DuplicateUserDisplayNameException {
		return null;
//
//		// Validate arguments
//		if (displayName == null || loginName == null || hashedPassword == null) {
//			throw new NullPointerException();
//		}
//
//		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
//			throw new IllegalArgumentException();
//		}
//
//		// Check if display name exists
//		if (userRepository.existsByDisplayName(displayName)) {
//			throw new DuplicateUserDisplayNameException();
//		}
//
//		// Check if login name exists
//		if (userRepository.existsByLoginName(loginName)) {
//			throw new DuplicateUserLoginNameException();
//		}
//
//		log.debug("Creating password login user with display name " + displayName);
//
//		final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
//		passwordAuthBuilder.loginName(loginName);
//		passwordAuthBuilder.hashedPassword(hashedPassword);
//
//		final AuthBuilder userAuthBuilder = Auth.builder();
//		// userAuthBuilder.password(passwordAuthBuilder.build());
//
//		final UserBuilder userBuilder = User.builder();
//		userBuilder.displayName(displayName);
//		// userBuilder.auth(userAuthBuilder.build());
//
//		final Mono<User> savedUser = userRepository.save(userBuilder.build());
//
//		// log.debug("Created user with ID " + savedUser.getId());
//
//		return savedUser;

	}

}