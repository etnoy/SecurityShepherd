package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.proxy.UserRepositoryProxy;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public final class UserService {

	private final UserRepositoryProxy userRepository;

	private final ClassService classService;

	private final KeyService keyService;

	public User create(final String displayName) throws DuplicateUserDisplayNameException {

		if (displayName == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		// Check if display name exists
		if(userRepository.existsByDisplayName(displayName)) {
			throw new DuplicateUserDisplayNameException();
		}

		log.debug("Creating user with display name " + displayName);

		final UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);

		final User savedUser = userRepository.save(userBuilder.build());

		log.debug("Created user with ID " + savedUser.getId());

		return savedUser;

	}

	public User createPasswordUser(final String displayName, final String loginName, final String hashedPassword) throws DuplicateUserLoginNameException, DuplicateUserDisplayNameException {

		// Validate arguments
		if (displayName == null || loginName == null || hashedPassword == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
			throw new IllegalArgumentException();
		}

		// Check if display name exists
		if(userRepository.existsByDisplayName(displayName)) {
			throw new DuplicateUserDisplayNameException();
		}
		
		// Check if login name exists
		if(userRepository.existsByLoginName(loginName)) {
			throw new DuplicateUserLoginNameException();
		}
		
		log.debug("Creating password login user with display name " + displayName);

		final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
		passwordAuthBuilder.loginName(loginName);
		passwordAuthBuilder.hashedPassword(hashedPassword);

		final AuthBuilder userAuthBuilder = Auth.builder();
		userAuthBuilder.password(passwordAuthBuilder.build());

		final UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);
		userBuilder.auth(userAuthBuilder.build());

		final User savedUser = userRepository.save(userBuilder.build());

		log.debug("Created user with ID " + savedUser.getId());

		return savedUser;

	}

	public void setDisplayName(final int id, final String displayName)
			throws UserIdNotFoundException, InvalidUserIdException {

		final Optional<User> returnedUser = get(id);

		if (!returnedUser.isPresent()) {

			throw new UserIdNotFoundException();

		}

		userRepository.save(returnedUser.get().withDisplayName(displayName));

	}

	public void setClassId(final int id, final int classId)
			throws ClassIdNotFoundException, InvalidUserIdException, UserIdNotFoundException, InvalidClassIdException {

		final Optional<User> returnedUser = get(id);

		if (!returnedUser.isPresent()) {

			throw new UserIdNotFoundException();

		}

		if (!classService.existsById(classId)) {

			throw new ClassIdNotFoundException();

		}

		userRepository.save(returnedUser.get().withClassId(classId));

	}

	public long count() {

		return userRepository.count();

	}

	public byte[] getKey(final int id) throws UserIdNotFoundException, InvalidUserIdException {

		final Optional<User> returnedUser = get(id);

		if (!returnedUser.isPresent()) {

			throw new UserIdNotFoundException();

		}

		User getKeyUser = returnedUser.get();

		byte[] currentKey = getKeyUser.getKey();

		if (currentKey == null) {

			currentKey = keyService.generateRandomBytes(16);
			getKeyUser = getKeyUser.withKey(currentKey);
			userRepository.save(getKeyUser);

		}

		return currentKey;

	}

	public Optional<User> findByLoginName(final String loginName) {

		return userRepository.findByLoginName(loginName);

	}

	public Optional<User> get(final int id) throws InvalidUserIdException {

		if (id <= 0) {
			throw new InvalidUserIdException();
		}

		return userRepository.findById(id);

	}

}