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
package org.owasp.securityshepherd.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.authentication.PasswordRegistrationDto;
import org.owasp.securityshepherd.module.ModuleController;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"application.runner.enabled=false"})
@AutoConfigureWebTestClient
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("ModuleController API integration test")
class ModuleControllerAPIIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired UserService userService;

  @Autowired private WebTestClient webTestClient;

  @Autowired TestUtils testUtils;

  @Autowired ModuleService moduleService;

  @Autowired ObjectMapper objectMapper;

  @Autowired ModuleController moduleController;

  @Test
  @Validated
  void getModuleByName_InvalidName_ReturnsError() throws Exception {

    final String loginName = "testUser";
    final String password = "paLswOrdha17£@£sh";

    webTestClient
        .post()
        .uri("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                new PasswordRegistrationDto("TestUserDisplayName", loginName, password)))
        .exchange()
        .expectStatus()
        .isCreated();

    String token =
        JsonPath.parse(
                new String(
                    webTestClient
                        .post()
                        .uri("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                            BodyInserters.fromPublisher(
                                Mono.just(
                                    "{\"userName\": \""
                                        + loginName
                                        + "\", \"password\": \""
                                        + password
                                        + "\"}"),
                                String.class))
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody()
                        .returnResult()
                        .getResponseBody()))
            .read("$.token");

    // TODO: we should return 400 bad request in the future

    for (final String invalidModuleName : TestUtils.INVALID_NAMES) {
      webTestClient
          .get()
          .uri(String.format("/api/v1/module/%s", invalidModuleName))
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .is5xxServerError();
    }
  }

  @BeforeEach
  private void setUp() {
    testUtils.deleteAll().block();
  }
}
