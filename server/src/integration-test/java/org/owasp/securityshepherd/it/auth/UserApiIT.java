package org.owasp.securityshepherd.it.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserApiIT {

  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void apiGetUser_ValidId_ReturnsUser() throws Exception {

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser1", "loginName1", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody();

    final User testUser = webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new PasswordUserRegistrationDto("TestUserDisplayName",
            "loginName", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody();

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser3", "loginName3", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody();

    FluxExchangeResult<User> getResult = webTestClient.get().uri("/api/v1/user/" + testUser.getId())
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(User.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(testUser));

    }).expectComplete().verify();

  }

  @Test
  public void apiListUsers_NoUsersExist_ReturnsNoUsers() throws Exception {

    StepVerifier.create(webTestClient.get().uri("/api/v1/user/list")
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(User.class).getResponseBody())
        .expectComplete().verify();

  }

  @Test
  public void apiListUsers_UsersExist_ReturnsUserList() throws Exception {

    HashSet<User> userSet = new HashSet<User>();

    userSet.add(webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new PasswordUserRegistrationDto("TestUserDisplayName",
            "loginName", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody());

    userSet.add(webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser2", "loginName2", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody());

    userSet.add(webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser3", "loginName3", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated().expectBody(User.class).returnResult()
        .getResponseBody());

    StepVerifier
        .create(webTestClient.get().uri("/api/v1/user/list").accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(User.class).getResponseBody())
        .recordWith(HashSet::new).thenConsumeWhile(x -> true)
        .expectRecordedMatches(x -> x.equals(userSet)).expectComplete().verify();

  }

  @Test
  public void apiUserCreate_ValidData_ReturnsValidUser() throws Exception {

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new PasswordUserRegistrationDto("TestUserDisplayName",
            "loginName", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated();

    FluxExchangeResult<String> getResult = webTestClient.get().uri("/api/v1/user/list")
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(String.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(notNullValue()));

    }).expectComplete().verify();

  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    userService.deleteAll().block();
  }

}
