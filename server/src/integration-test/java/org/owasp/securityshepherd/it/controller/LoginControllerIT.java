package org.owasp.securityshepherd.it.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.security.Role;
import org.owasp.securityshepherd.service.DatabaseService;
import org.owasp.securityshepherd.service.UserService;
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
@DisplayName("LoginController integration test")
public class LoginControllerIT {

  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;
  
  @Autowired
  DatabaseService databaseService;

  @Test
  public void login_CorrectCredentials_ReturnsJWS() {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    final User createdUser = userService.createPasswordUser("Test User", loginName, hashedPassword).block();

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

    final Role userRole = Role.valueOf(jsonBody.read("$.role"));
    final int userId = Integer.parseInt(jsonBody.read("$.sub"));
    
    assertThat(userRole, is(Role.ROLE_USER));
    assertThat(userId, is(createdUser.getId()));

  }

  @Test
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
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    databaseService.clearAll().block();
  }

}
