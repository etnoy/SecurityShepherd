package org.owasp.securityshepherd.it.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.auth.PasswordUserDetails;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PasswordUserDetailsIT {

	@Autowired
	private UserRepository userRepository;

	@Test
	@Disabled
	public void userAuth_HashedPassword_ReturnsHash() {

		String userName = "authorizedUser";

		// "Password" hashed with low-round bcrypt for testing
		String hashedPassword = "$2y$06$dPAxpb.cmwryGD1PcW6Kh.Fpq0xy9wN8aq6EV2DqRX/Y7LFPL1noa";

		PasswordAuthBuilder passwordBuilder = PasswordAuth.builder();

		passwordBuilder.hashedPassword(hashedPassword);
		passwordBuilder.passwordExpired(false);

		AuthBuilder authDataBuilder = Auth.builder();

		authDataBuilder.password(passwordBuilder.build());

		UserBuilder authorizedUserBuilder = User.builder();

		authorizedUserBuilder.displayName(userName);

		authorizedUserBuilder.auth(authDataBuilder.build());

		User authorizedUser = userRepository.save(authorizedUserBuilder.build());

		UserDetails userDetails = new PasswordUserDetails(authorizedUser);

		assertEquals(hashedPassword, userDetails.getPassword());
		assertEquals(userName, userDetails.getUsername());

		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("player")));

	}

}