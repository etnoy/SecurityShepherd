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

package org.owasp.securityshepherd.it.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.controller.ModuleController;
import org.owasp.securityshepherd.dto.PasswordRegistrationDto;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("ModuleController integration test")
public class ModuleControllerIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  TestUtils testService;

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ModuleController moduleController;

  @Test
  @DisplayName("Submitting a valid exact flag should return true")
  public void submitFlag_ValidExactFlag_Success() throws Exception {
    final String loginName = "testUser";
    final String password = "paLswOrdha17£@£sh";
    final String moduleName = "test-module";
    final String moduleShortName = "test-module";

    final String flag = "thisisaflag";

    final long moduleId = moduleService.create(moduleName, moduleShortName).block().getId();

    moduleService.setExactFlag(moduleId, flag).block();

    webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters
            .fromValue(new PasswordRegistrationDto("TestUserDisplayName", loginName, password)))
        .exchange().expectStatus().isCreated();

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    final String endpoint = String.format("/api/v1/module/submit/%d", moduleId);

    final BodyInserter<String, ReactiveHttpOutputMessage> submissionBody =
        BodyInserters.fromValue(flag);

    final Flux<Submission> moduleSubmissionFlux = webTestClient.post().uri(endpoint)
        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON).body(submissionBody).exchange().expectStatus()
        .isOk().returnResult(Submission.class).getResponseBody();

    StepVerifier.create(moduleSubmissionFlux.map(Submission::isValid))
        // We expect the submission to be valid
        .expectNext(true)
        // We're done
        .expectComplete().verify();
  }

  @Test
  @DisplayName("Submitting an invalid exact flag should return false")
  public void submitFlag_InvalidExactFlag_Success() throws Exception {
    final String loginName = "testUser";
    final String password = "paLswOrdha17£@£sh";
    final String moduleName = "test-module";
    final String moduleShortName = "test-module";

    final String flag = "thisisaflag";

    final long moduleId = moduleService.create(moduleName, moduleShortName).block().getId();

    moduleService.setExactFlag(moduleId, flag).block();

    webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters
            .fromValue(new PasswordRegistrationDto("TestUserDisplayName", loginName, password)))
        .exchange().expectStatus().isCreated();

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    StepVerifier
        .create(webTestClient.post().uri(String.format("/api/v1/module/submit/%d", moduleId))
            .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(flag + "invalid"))
            .exchange().expectStatus().isOk().returnResult(Submission.class).getResponseBody()
            .map(Submission::isValid))
        .expectNext(false).expectComplete().verify();
  }

  @BeforeEach
  private void setUp() {
    testService.deleteAll().block();
  }
}
