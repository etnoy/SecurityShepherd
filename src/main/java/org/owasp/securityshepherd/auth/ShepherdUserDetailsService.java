package org.owasp.securityshepherd.auth;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShepherdUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String loginName) {
		if (userService.count() == 0) {

			userService.createPasswordUser("TestUser", "test",
					"$2y$04$U3oKNt0WbZXLvt.LTHyd4Oy4OMweBu3oU7unxvtCcijW7AxVREbZK");

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