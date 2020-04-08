package org.owasp.securityshepherd.it.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("LoginController integration test")
public class LoginControllerIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }
  
  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;
  
  @Autowired
  TestUtils testService;

  @Test
  @DisplayName("Logging in with correct credentials should return a valid token")
  public void login_CorrectCredentials_ReturnsJWS() {
    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    final long createdUserId = userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    final String jws = JsonPath.parse(
        new String(webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"test\"}"),
                String.class))
            .exchange().expectStatus().isOk().expectBody(String.class).returnResult()
            .getResponseBody()))
        .read("$.token");

    final int signatureDotPosition = jws.lastIndexOf('.');

    final String jwt = jws.substring(0, signatureDotPosition);

    final int bodyDotPosition = jwt.lastIndexOf('.');

    final DocumentContext jsonBody =
        JsonPath.parse(new String(Base64.getDecoder().decode(jwt.substring(bodyDotPosition + 1))));

    final long userId = Long.parseLong(jsonBody.read("$.sub"));
    
    assertThat(userId, is(createdUserId));
  }

  @Test
  @DisplayName("Logging in with an incorrect password should return HTTP Unauthorized")
  public void login_WrongPassword_ReturnsUnauthorized() {
    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"wrong\"}"),
            String.class))
        .exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("Logging in with an incorrect username should return HTTP Unauthorized")
  public void login_WrongUserName_ReturnsUnauthorized() {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"doesnotexist\", \"password\": \"wrong\"}"), String.class))
        .exchange().expectStatus().isUnauthorized();
  }

  @BeforeEach
  private void setUp() {
    testService.deleteAll().block();
  }
}
