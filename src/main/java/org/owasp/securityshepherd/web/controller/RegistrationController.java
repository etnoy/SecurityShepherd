package org.owasp.securityshepherd.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.DuplicateUserLoginNameException;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.GenericResponse;
import org.owasp.securityshepherd.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RegistrationController {

	@Autowired
	private UserService userService;

	@PostMapping(path = "/api/v1/user/registration")
	public GenericResponse registerUserAccount(@RequestBody @Valid final UserDto userDto, final HttpServletRequest request) {
		log.debug("Registering user account with information: {}", userDto);

		log.debug(userDto.toString());
		
			try {
				final User registered = userService.createPasswordUser(userDto);
			} catch (DuplicateUserLoginNameException e) {
		        return new GenericResponse("Login name already exists");

			} catch (DuplicateUserDisplayNameException e) {
		        return new GenericResponse("Display name already exists");

			}
		
        return new GenericResponse("success");


	}

}