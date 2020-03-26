package org.owasp.securityshepherd.it.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.service.UserService;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LoginControllerIT {

  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void login_CorrectCredentials_ReturnsJWS() {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    String token = JsonPath.parse(
        new String(webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                Mono.just("{\"username\": \"" + loginName + "\", \"password\": \"test\"}"),
                String.class))
            .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    assertThat(token, is(notNullValue()));

  }

  @Test
  public void login_WrongPassword_ReturnsUnauthorized() {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"username\": \"" + loginName + "\", \"password\": \"wrong\"}"),
            String.class))
        .exchange().expectStatus().isUnauthorized();

  }
  
  @Test
  public void login_WrongUserName_ReturnsUnauthorized() {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"username\": \"doesnotexist\", \"password\": \"wrong\"}"),
            String.class))
        .exchange().expectStatus().isUnauthorized();

  }



  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    // Clear all users from repository before every test
    userService.deleteAll().block();
  }

}
