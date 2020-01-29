package org.owasp.securityshepherd;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userDao;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userDao.getByName(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return new MyUserPrincipal(user);
	}
}