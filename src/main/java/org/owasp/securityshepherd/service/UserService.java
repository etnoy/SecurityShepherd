package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	KeyService rngService;

	public User create(final String displayName) {

		if (displayName == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating user with display name " + displayName);

		final UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);

		final User savedUser = userRepository.save(userBuilder.build());

		log.debug("Created user with ID " + savedUser.getId());

		return savedUser;

	}

	public User createPasswordUser(final String displayName, final String loginName, final String hashedPassword) {

		if (displayName == null || loginName == null || hashedPassword == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
			throw new IllegalArgumentException();
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

	public void setDisplayName(final int id, final String displayName) {

		User newDisplayNameUser = get(id).get().withDisplayName(displayName);

		userRepository.save(newDisplayNameUser);

	}

	public void setClassId(final int id, final int classId) {

		User newClassIdUser = get(id).get().withClassId(classId);

		userRepository.save(newClassIdUser);

	}

	public long count() {

		return userRepository.count();

	}

	public byte[] getKey(final int id) throws UserIdNotFoundException {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}
		
		final Optional<User> returnedUser = get(id);

		if (!returnedUser.isPresent()) {

			throw new UserIdNotFoundException();

		}

		User getKeyUser = get(id).get();

		byte[] currentKey = getKeyUser.getKey();

		if (currentKey == null) {

			currentKey = rngService.generateRandomBytes(16);

			getKeyUser = getKeyUser.withKey(currentKey);

			userRepository.save(getKeyUser);

		}

		return currentKey;

	}

	public Optional<User> findByLoginName(final String loginName) {

		return userRepository.findByLoginName(loginName);

	}

	public Optional<User> get(final int id) {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}

		return userRepository.findById(id);

	}

}