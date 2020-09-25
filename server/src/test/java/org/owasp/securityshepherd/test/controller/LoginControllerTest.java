/*
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
package org.owasp.securityshepherd.test.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.AuthResponse;
import org.owasp.securityshepherd.authentication.LoginController;
import org.owasp.securityshepherd.authentication.PasswordLoginDto;
import org.owasp.securityshepherd.authentication.PasswordRegistrationDto;
import org.owasp.securityshepherd.authentication.WebTokenService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginController unit test")
class LoginControllerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  LoginController loginController;

  @Mock UserService userService;

  @Mock WebTokenService webTokenService;

  @Mock PasswordEncoder passwordEncoder;

  @Test
  void login_InvalidCredentials_ReturnsJWT() {
    final String userName = "user";
    final String password = "password";
    final PasswordLoginDto passwordLoginDto = new PasswordLoginDto(userName, password);
    final long mockUserId = 122L;
    when(userService.findUserIdByLoginName(userName)).thenReturn(Mono.just(mockUserId));
    when(userService.authenticate(userName, password)).thenReturn(Mono.just(false));
    StepVerifier.create(loginController.login(passwordLoginDto))
        .expectNext(new ResponseEntity<>(HttpStatus.UNAUTHORIZED))
        .expectComplete()
        .verify();
  }

  @Test
  void login_ValidCredentials_ReturnsJWT() {
    final String userName = "user";
    final String password = "password";
    final PasswordLoginDto passwordLoginDto = new PasswordLoginDto(userName, password);
    final String mockJwt = "token";
    final long mockUserId = 122L;
    final AuthResponse mockAuthResponse = new AuthResponse(mockJwt, userName);
    when(userService.findUserIdByLoginName(userName)).thenReturn(Mono.just(mockUserId));
    when(userService.authenticate(userName, password)).thenReturn(Mono.just(true));
    when(webTokenService.generateToken(mockUserId)).thenReturn(mockJwt);
    StepVerifier.create(loginController.login(passwordLoginDto))
        .expectNext(new ResponseEntity<>(mockAuthResponse, HttpStatus.OK))
        .expectComplete()
        .verify();
  }

  @Test
  void register_ValidData_ReturnsUserid() {
    final String displayName = "displayName";
    final String userName = "user";
    final String password = "password";
    final String encodedPassword = "encoded";
    final PasswordRegistrationDto passwordRegistrationDto =
        new PasswordRegistrationDto(displayName, userName, password);
    final long mockUserId = 255L;
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
    when(userService.createPasswordUser(displayName, userName, encodedPassword))
        .thenReturn(Mono.just(mockUserId));
    StepVerifier.create(loginController.register(passwordRegistrationDto))
        .expectNext(mockUserId)
        .expectComplete()
        .verify();
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    loginController = new LoginController(userService, webTokenService, passwordEncoder);
  }
}
