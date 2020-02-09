package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
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

	public User create(String displayName) {

		log.debug("Creating user with display name " + displayName);

		UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);

		User savedUser = userRepository.save(userBuilder.build());

		log.debug("Created user with ID " + savedUser.getId());

		return savedUser;

	}

	public User createPasswordUser(String displayName, String loginName, String hashedPassword) {

		log.debug("Creating password login user with display name " + displayName);

		if (displayName == null || loginName == null || hashedPassword == null) {
			throw new NullPointerException();
		}

		if (displayName.isEmpty() || loginName.isEmpty() || hashedPassword.isEmpty()) {
			throw new IllegalArgumentException();
		}

		PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
		passwordAuthBuilder.loginName(loginName);
		passwordAuthBuilder.hashedPassword(hashedPassword);

		AuthBuilder userAuthBuilder = Auth.builder();
		userAuthBuilder.password(passwordAuthBuilder.build());

		UserBuilder userBuilder = User.builder();
		userBuilder.displayName(displayName);
		userBuilder.auth(userAuthBuilder.build());

		User savedUser = userRepository.save(userBuilder.build());

		log.debug("Created user with ID " + savedUser.getId());

		return savedUser;

	}

	public void setDisplayName(long id, String displayName) {

		User newDisplayNameUser = get(id).withDisplayName(displayName);

		userRepository.save(newDisplayNameUser);

	}

	public void setClassId(long id, long classId) {

		User newClassIdUser = get(id).withClassId(classId);

		userRepository.save(newClassIdUser);

	}

	public long count() {
		return userRepository.count();
	}

	public Optional<User> findByLoginName(String loginName) {
		return userRepository.findByLoginName(loginName);
	}

	public User get(long id) {
		Optional<User> returnedUser = userRepository.findById(id);

		if (!returnedUser.isPresent()) {
			throw new NullPointerException();
		} else {
			return returnedUser.get();
		}
	}

}