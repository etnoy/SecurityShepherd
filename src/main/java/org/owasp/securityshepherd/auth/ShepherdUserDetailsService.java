package org.owasp.securityshepherd.auth;

import java.util.Optional;

import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ShepherdUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String loginName) {
		if (userService.count() == 0) {

			try {
				userService.createPasswordUser("TestUser", "test",
						"$2y$04$U3oKNt0WbZXLvt.LTHyd4Oy4OMweBu3oU7unxvtCcijW7AxVREbZK");
			} catch (DuplicateUserLoginNameException | DuplicateUserDisplayNameException e) {
				// Ignore as this is a test
			}

		}

		log.trace("Looking for username " + loginName);
		Optional<User> user = userService.findByLoginName(loginName);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException(loginName);
		}
		
		log.trace("Found username, creating user details");
		return new PasswordUserDetails(user.get());
	}
}