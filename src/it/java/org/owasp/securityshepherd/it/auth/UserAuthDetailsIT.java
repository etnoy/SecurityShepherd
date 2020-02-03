package org.owasp.securityshepherd.it.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.auth.UserAuthDetails;
import org.owasp.securityshepherd.model.AuthData;
import org.owasp.securityshepherd.model.AuthData.AuthDataBuilder;
import org.owasp.securityshepherd.model.PasswordData;
import org.owasp.securityshepherd.model.PasswordData.PasswordDataBuilder;
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
public class UserAuthDetailsIT {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void userAuth_HashedPassword_ReturnsHash() {

		String userName = "authorizedUser";

		// "Password" hashed with low-round bcrypt for testing
		String hashedPassword = "$2y$06$dPAxpb.cmwryGD1PcW6Kh.Fpq0xy9wN8aq6EV2DqRX/Y7LFPL1noa";

		PasswordDataBuilder passwordBuilder = PasswordData.builder();

		passwordBuilder.hashedPassword(hashedPassword);
		passwordBuilder.passwordExpired(false);

		AuthDataBuilder authDataBuilder = AuthData.builder();

		authDataBuilder.password(passwordBuilder.build());

		UserBuilder authorizedUserBuilder = User.builder();

		authorizedUserBuilder.name(userName);

		authorizedUserBuilder.auth_data(authDataBuilder.build());

		User authorizedUser = userRepository.save(authorizedUserBuilder.build());

		UserDetails userDetails = new UserAuthDetails(authorizedUser);

		assertEquals(hashedPassword, userDetails.getPassword());
		assertEquals(userName, userDetails.getUsername());

		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("player")));

	}

}