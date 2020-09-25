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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserController;
import org.owasp.securityshepherd.user.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController unit test")
class UserControllerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private UserController userController;

  @Mock private UserService userService;

  @Test
  void deleteById_ValidId_CallsUserService() {
    final long mockUserId = 317L;
    when(userService.deleteById(mockUserId)).thenReturn(Mono.empty());
    StepVerifier.create(userController.deleteById(mockUserId)).expectComplete().verify();
    verify(userService, times(1)).deleteById(mockUserId);
  }

  @Test
  void findAll_NoUsersExist_ReturnsEmpty() {
    when(userService.findAll()).thenReturn(Flux.empty());
    StepVerifier.create(userController.findAll()).expectComplete().verify();
    verify(userService, times(1)).findAll();
  }

  @Test
  void findById_UserIdDoesNotExist_ReturnsUser() {
    final long mockUserId = 559L;
    when(userService.findById(mockUserId)).thenReturn(Mono.empty());
    StepVerifier.create(userController.findById(mockUserId)).expectComplete().verify();
    verify(userService, times(1)).findById(mockUserId);
  }

  @Test
  void findById_UserIdExists_ReturnsUser() {
    final long mockUserId = 380L;
    final User user = mock(User.class);

    when(userService.findById(mockUserId)).thenReturn(Mono.just(user));
    StepVerifier.create(userController.findById(mockUserId))
        .expectNext(user)
        .expectComplete()
        .verify();
    verify(userService, times(1)).findById(mockUserId);
  }

  @Test
  void findAll_UsersExist_ReturnsUsers() {
    final User user1 = mock(User.class);
    final User user2 = mock(User.class);
    final User user3 = mock(User.class);
    final User user4 = mock(User.class);

    when(userService.findAll()).thenReturn(Flux.just(user1, user2, user3, user4));
    StepVerifier.create(userController.findAll())
        .expectNext(user1)
        .expectNext(user2)
        .expectNext(user3)
        .expectNext(user4)
        .expectComplete()
        .verify();
    verify(userService, times(1)).findAll();
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    userController = new UserController(userService);
  }
}
