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
package org.owasp.securityshepherd.test.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.AuthenticationManager;
import org.owasp.securityshepherd.authentication.SecurityContextRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityContextRepository unit test")
class SecurityContextRepositoryTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private SecurityContextRepository securityContextRepository;

  @Mock AuthenticationManager authenticationManager;

  @Test
  void load_InvalidHeader_ReturnsSecurityContext() throws Exception {
    final ServerWebExchange mockServerWebExchange = mock(ServerWebExchange.class);
    final String token = "authToken";
    final ServerHttpRequest mockServerHttpRequest = mock(ServerHttpRequest.class);
    when(mockServerWebExchange.getRequest()).thenReturn(mockServerHttpRequest);
    final HttpHeaders mockHttpHeaders = mock(HttpHeaders.class);
    when(mockServerHttpRequest.getHeaders()).thenReturn(mockHttpHeaders);
    final String mockAuthorizationHeader = "Hello World" + token;
    when(mockHttpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(mockAuthorizationHeader);
    StepVerifier.create(
            securityContextRepository
                .load(mockServerWebExchange)
                .map(SecurityContext::getAuthentication))
        .expectComplete()
        .verify();
  }

  @Test
  void load_NullHeader_ReturnsSecurityContext() throws Exception {
    final ServerWebExchange mockServerWebExchange = mock(ServerWebExchange.class);

    final ServerHttpRequest mockServerHttpRequest = mock(ServerHttpRequest.class);
    when(mockServerWebExchange.getRequest()).thenReturn(mockServerHttpRequest);
    final HttpHeaders mockHttpHeaders = mock(HttpHeaders.class);
    when(mockServerHttpRequest.getHeaders()).thenReturn(mockHttpHeaders);
    when(mockHttpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    StepVerifier.create(
            securityContextRepository
                .load(mockServerWebExchange)
                .map(SecurityContext::getAuthentication))
        .expectComplete()
        .verify();
  }

  @Test
  void load_ValidHeader_ReturnsSecurityContext() throws Exception {
    final Long mockUserId = 581L;
    final ServerWebExchange mockServerWebExchange = mock(ServerWebExchange.class);

    final String token = "authToken";

    final ServerHttpRequest mockServerHttpRequest = mock(ServerHttpRequest.class);
    when(mockServerWebExchange.getRequest()).thenReturn(mockServerHttpRequest);
    final HttpHeaders mockHttpHeaders = mock(HttpHeaders.class);
    when(mockServerHttpRequest.getHeaders()).thenReturn(mockHttpHeaders);
    final String mockAuthorizationHeader = "Bearer " + token;
    when(mockHttpHeaders.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(mockAuthorizationHeader);

    final List<SimpleGrantedAuthority> mockAuthorities =
        Arrays.asList(new SimpleGrantedAuthority[] {new SimpleGrantedAuthority("ROLE_USER")});

    final Authentication mockAuthentication =
        new UsernamePasswordAuthenticationToken(mockUserId, token, mockAuthorities);

    when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(token, token)))
        .thenReturn(Mono.just(mockAuthentication));

    StepVerifier.create(
            securityContextRepository
                .load(mockServerWebExchange)
                .map(SecurityContext::getAuthentication))
        .expectNext(mockAuthentication)
        .expectComplete()
        .verify();
  }

  @Test
  void save_NotImplemented() throws Exception {
    StepVerifier.create(securityContextRepository.save(null, null))
        .expectError(UnsupportedOperationException.class)
        .verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    securityContextRepository = new SecurityContextRepository(authenticationManager);
  }
}
