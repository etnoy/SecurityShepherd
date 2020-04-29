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

package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.security.AuthenticationManager;
import org.owasp.securityshepherd.service.WebTokenService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationManager unit test")
public class AuthenticationManagerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  AuthenticationManager authenticationManager;

  @Mock
  WebTokenService webTokenService;

  @Mock
  UserService userService;

  @Test
  public void authenticate_ValidAuthentication_ReturnsValidAuthentication() {
    final Authentication mockAuthentication = mock(Authentication.class);
    final String mockToken = "token";
    final long mockUserId = 548;
    when(mockAuthentication.getCredentials()).thenReturn(mockToken);
    when(webTokenService.validateToken(mockToken)).thenReturn(true);
    when(webTokenService.getUserIdFromToken(mockToken)).thenReturn(mockUserId);
    final SimpleGrantedAuthority mockSimpleGrantedAuthority1 = mock(SimpleGrantedAuthority.class);
    final SimpleGrantedAuthority mockSimpleGrantedAuthority2 = mock(SimpleGrantedAuthority.class);
    final SimpleGrantedAuthority mockSimpleGrantedAuthority3 = mock(SimpleGrantedAuthority.class);

    final Flux<SimpleGrantedAuthority> authorityFlux = Flux.just(mockSimpleGrantedAuthority1,
        mockSimpleGrantedAuthority2, mockSimpleGrantedAuthority3);
    final List<SimpleGrantedAuthority> authorities = Stream
        .of(mockSimpleGrantedAuthority1, mockSimpleGrantedAuthority2, mockSimpleGrantedAuthority3)
        .collect(Collectors.toList());
    when(userService.getAuthoritiesByUserId(mockUserId)).thenReturn(authorityFlux);
    StepVerifier.create(authenticationManager.authenticate(mockAuthentication)).assertNext(auth -> {
      assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
      assertThat(auth.getPrincipal()).isEqualTo(mockUserId);
      assertThat(auth.getAuthorities()).isEqualTo(authorities);
    }).expectComplete().verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    authenticationManager = new AuthenticationManager(webTokenService, userService);
  }

  @Test
  public void authenticate_InvalidAuthentication_ReturnsValidAuthentication() {
    final Authentication mockAuthentication = mock(Authentication.class);
    final String mockToken = "token";
    when(mockAuthentication.getCredentials()).thenReturn(mockToken);
    when(webTokenService.validateToken(mockToken)).thenReturn(false);

    StepVerifier.create(authenticationManager.authenticate(mockAuthentication)).expectComplete()
        .verify();
  }
}
