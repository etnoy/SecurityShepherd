package org.owasp.securityshepherd.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.owasp.securityshepherd.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthDetails implements UserDetails {

	private static final long serialVersionUID = -3677404075923734838L;
	private User user;

	public UserAuthDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<>();

		String role;

		if (user.getAuth().isAdmin()) {
			role = "admin";
		} else {
			role = "player";
		}

		authorities.add(new SimpleGrantedAuthority(role));

		return authorities;

	}

	@Override
	public String getPassword() {
		return user.getAuth().getPassword().getHashedPassword();
	}

	@Override
	public String getUsername() {
		return user.getName();
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
		return !user.getAuth().isEnabled();
	}

}