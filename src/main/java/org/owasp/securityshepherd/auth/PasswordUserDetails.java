package org.owasp.securityshepherd.auth;

import java.util.Collection;
import java.util.Collections;

import org.owasp.securityshepherd.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PasswordUserDetails implements UserDetails {

	private static final long serialVersionUID = -3677404075923734838L;
	
	private User user;

	public PasswordUserDetails(User user) {
    	log.trace("Creating password details for user " + user);

		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		String role;

		if (user.getAuth().isAdmin()) {
			role = "admin";
		} else {
			role = "player";
		}

		return Collections.singletonList(new SimpleGrantedAuthority(role));

	}

	@Override
	public String getPassword() {
		log.trace("Found password hash ", user.getAuth().getPassword().getHashedPassword());

		return user.getAuth().getPassword().getHashedPassword();
	}

	@Override
	public String getUsername() {
		log.trace("Found username " + user.getAuth().getPassword().getLoginName());
		return user.getDisplayName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.getAuth().isAccountSuspended();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !user.getAuth().getPassword().isPasswordExpired();
	}

	@Override
	public boolean isEnabled() {
		return user.getAuth().isEnabled();
	}

}