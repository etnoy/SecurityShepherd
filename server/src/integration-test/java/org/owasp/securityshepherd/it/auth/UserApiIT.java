package org.owasp.securityshepherd.it.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.dto.PasswordUserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
public class UserApiIT {

  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void apiGetUserResource_AuthenticatedUser_Success() throws Exception {

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

  }

  @Test
  public void apiGetUserResource_AuthenticatedAdmin_Success() throws Exception {

    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    final int userId =
        userService.createPasswordUser("Test User", loginName, hashedPassword).block().getId();

    userService.promote(userId).block();

    String token = JsonPath.parse(
        new String(webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                Mono.just("{\"username\": \"" + loginName + "\", \"password\": \"test\"}"),
                String.class))
            .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

  }

  @Test
  public void apiListUsers_UsersExist_ReturnsUserList() throws Exception {

    final String loginName = "test";
    final String password = "paLswOrdha17£@£sh";

    HashSet<Integer> userIdSet = new HashSet<Integer>();

    final int userId = webTestClient.post().uri("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters
            .fromValue(new PasswordUserRegistrationDto("TestUserDisplayName", loginName, password)))
        .exchange().expectStatus().isCreated().expectBody(Integer.class).returnResult()
        .getResponseBody();

    // Promote this user to admin
    userService.promote(userId).block();

    userIdSet.add(userId);

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"username\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    userIdSet
        .add(webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                new PasswordUserRegistrationDto("TestUser2", "loginName2", "paLswOrdha17£@£sh")))
            .exchange().expectStatus().isCreated().expectBody(Integer.class).returnResult()
            .getResponseBody());

    userIdSet
        .add(webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                new PasswordUserRegistrationDto("TestUser3", "loginName3", "paLswOrdha17£@£sh")))
            .exchange().expectStatus().isCreated().expectBody(Integer.class).returnResult()
            .getResponseBody());

    StepVerifier
        .create(webTestClient.get().uri("/api/v1/users").header("Authorization", "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
            .contentType(MediaType.APPLICATION_JSON).returnResult(User.class).getResponseBody()
            .map(User::getId))
        .recordWith(HashSet::new).thenConsumeWhile(x -> true)
        .expectRecordedMatches(x -> x.equals(userIdSet)).expectComplete().verify();

  }

  @Test
  public void apiUserCreate_ValidData_ReturnsValidUser() throws Exception {

    final String loginName = "test";
    final String password = "paLswOrdha17£@£sh";

    final int userId = webTestClient.post().uri("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(Mono.just("{\"displayName\": \"" + loginName
            + "\", \"loginName\": \"" + loginName + "\",  \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isCreated().expectBody(Integer.class).returnResult()
        .getResponseBody();
    
    // Promote this user to admin
    userService.promote(userId).block();

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"username\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    FluxExchangeResult<User> getResult = webTestClient.get()
        .uri("/api/v1/user/" + Integer.toString(userId)).header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(User.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(userService.getById(userId).block()));

    }).expectComplete().verify();

  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    // Clear all users from repository before every test
    userService.deleteAll().block();
  }

}
