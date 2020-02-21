package org.owasp.securityshepherd.security;

import java.util.Optional;

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

		log.trace("Looking for username " + loginName);
		Optional<User> user = userService.findByLoginName(loginName);
		
		if (!user.isPresent()) {
			throw new UsernameNotFoundException(loginName);
		}
		
		log.trace("Found username, creating user details");
		return new PasswordUserDetails(user.get());
	}
}