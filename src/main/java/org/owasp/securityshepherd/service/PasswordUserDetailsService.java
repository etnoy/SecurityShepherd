package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.auth.UserAuthDetails;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PasswordUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<User> user = userRepository.findByName(username);

		if (!user.isPresent()) {
			throw new UsernameNotFoundException(username);
		}

		return new UserAuthDetails(user.get());
	}
}