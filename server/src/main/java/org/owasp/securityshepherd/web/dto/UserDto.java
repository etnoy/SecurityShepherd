package org.owasp.securityshepherd.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.owasp.securityshepherd.validation.PasswordMatches;
import org.owasp.securityshepherd.validation.ValidEmail;
import org.owasp.securityshepherd.validation.ValidPassword;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@PasswordMatches
public class UserDto {
	
	@NotNull
	@Size(min = 1, message = "{Size.userDto.firstName}")
	private String displayName;

	@NotNull
	@Size(min = 1, message = "{Size.userDto.firstName}")
	private String loginName;
	
	@ValidPassword
	@NotNull
	private String password;

	@NotNull
	@Size(min = 1)
	private String matchingPassword;

	@ValidEmail
	@NotNull
	@Size(min = 1, message = "{Size.userDto.email}")
	private String email;

}