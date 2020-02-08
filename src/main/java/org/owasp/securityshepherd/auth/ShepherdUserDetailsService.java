package org.owasp.securityshepherd.auth;

import java.util.Optional;

import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;

import org.owasp.securityshepherd.model.User;

import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShepherdUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String name) {
		if (userRepository.count() == 0) {

			PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();

			passwordAuthBuilder.loginName("test");
			passwordAuthBuilder.hashedPassword("$2y$12$4b4Cn4Pux0vk30aNrB6le.dDlrVBHqWfh98ZNrVhLIB0CkLG0WVWe");
			passwordAuthBuilder.passwordExpired(false);

			AuthBuilder userAuthBuilder = Auth.builder();

			userAuthBuilder.password(passwordAuthBuilder.build());
			userAuthBuilder.isEnabled(true);

			UserBuilder userBuilder = User.builder();

			userBuilder.name("TestUser");
			userBuilder.auth(userAuthBuilder.build());

			userRepository.save(userBuilder.build());

		}

		log.trace("Looking for username " + name);
		Optional<User> user = userRepository.findByLoginName(name);
		if (!user.isPresent()) {
			throw new RuntimeException(name);
		}
		return new PasswordUserDetails(user.get());
	}
}