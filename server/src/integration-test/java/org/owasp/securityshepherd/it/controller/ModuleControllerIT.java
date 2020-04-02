package org.owasp.securityshepherd.it.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.dto.PasswordRegistrationDto;
import org.owasp.securityshepherd.dto.SubmissionDto;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.jayway.jsonpath.JsonPath;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
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
  TestService testService;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void submitModule_ValidExactFlag_Success() throws Exception {
    final String loginName = "testUser";
    final String password = "paLswOrdha17£@£sh";

    final String flag = "thisisaflag";

    final int moduleId = moduleService.create("Test Module").block().getId();

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
        .create(webTestClient.post().uri("/api/v1/module/submit")
            .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new SubmissionDto(moduleId, flag))).exchange()
            .expectStatus().isOk().returnResult(Boolean.class).getResponseBody())
        .assertNext(correctFlag -> {
          assertThat(correctFlag, is(true));
        }).expectComplete().verify();
  }

  @Test
  public void submitModule_InvalidExactFlag_Success() throws Exception {
    final String loginName = "testUser";
    final String password = "paLswOrdha17£@£sh";

    final String flag = "thisisaflag";

    final int moduleId = moduleService.create("Test Module").block().getId();

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
        .create(webTestClient.post().uri("/api/v1/module/submit")
            .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new SubmissionDto(moduleId, flag + "invalid"))).exchange()
            .expectStatus().isOk().returnResult(Boolean.class).getResponseBody())
        .assertNext(correctFlag -> {
          assertThat(correctFlag, is(false));
        }).expectComplete().verify();
  }

  @BeforeEach
  private void setUp() {
    testService.deleteAll().block();
  }
}
