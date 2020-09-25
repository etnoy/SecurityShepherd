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
package org.owasp.securityshepherd.it.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.runner.enabled=false"})
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("User integration test")
class UserIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired UserService userService;

  @Autowired TestUtils testService;

  @Test
  void createPasswordUser_ValidData_ReturnsCorrectUser() {
    final long userId =
        userService
            .createPasswordUser(
                "Test User",
                "user_login_name",
                "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW")
            .block();

    StepVerifier.create(userService.findById(userId).map(User::getId))
        .expectNext(userId)
        .expectComplete()
        .verify();
  }

  @Test
  void createUser_NaughtyUsernames_RepositoryFindsCorrectUser() {
    for (final String displayName : TestUtils.STRINGS) {
      if (!displayName.isEmpty()) {
        StepVerifier.create(
                userService
                    .create(displayName)
                    .flatMap(userService::findById)
                    .map(User::getDisplayName))
            .expectNext(displayName)
            .expectComplete()
            .verify();
        testService.deleteAll().block();
      }
    }
  }

  @BeforeEach
  private void setUp() {
    testService.deleteAll().block();
  }
}
