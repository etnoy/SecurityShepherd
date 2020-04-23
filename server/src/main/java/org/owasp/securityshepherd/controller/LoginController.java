/**
 * This file is part of Security Shepherd.
 *
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.owasp.securityshepherd.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.dto.PasswordLoginDto;
import org.owasp.securityshepherd.dto.PasswordRegistrationDto;
import org.owasp.securityshepherd.security.AuthResponse;
import org.owasp.securityshepherd.service.WebTokenService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LoginController {

  private final UserService userService;

  private final WebTokenService webTokenService;

  private final PasswordEncoder passwordEncoder;

  @PostMapping(value = "/login")
  public Mono<ResponseEntity<AuthResponse>> login(@RequestBody @Valid PasswordLoginDto loginDto) {
    return userService.findUserIdByLoginName(loginDto.getUserName())
        .filterWhen(
            userId -> userService.authenticate(loginDto.getUserName(), loginDto.getPassword()))
        .map(webTokenService::generateToken).map(token -> new AuthResponse(token, loginDto.getUserName()))
        .map(authResponse -> new ResponseEntity<>(authResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
  }

  @PostMapping(path = "/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Long> register(@Valid @RequestBody final PasswordRegistrationDto registerDto) {
    return userService.createPasswordUser(registerDto.getDisplayName(), registerDto.getUserName(),
        passwordEncoder.encode(registerDto.getPassword()));
  }
}
