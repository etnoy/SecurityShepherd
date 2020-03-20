package org.owasp.securityshepherd.it.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.controller.UserController;
import org.owasp.securityshepherd.web.dto.PasswordUserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserIT {

  @Autowired
  UserService userService;

  @Autowired
  private WebTestClient webTestClient;
 
  
  @Test
  public void userGet_ValidId_ReturnsUser() throws Exception {

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new PasswordUserRegistrationDto("TestUserDisplayName",
            "loginName", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated();

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser2", "loginName3", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated();

    FluxExchangeResult<String> getResult = webTestClient.get().uri("/api/v1/user/list")
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(String.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(notNullValue()));

    }).expectComplete().verify();

  }
  
  @Test
  public void userList_ValidData_ReturnsUserList() throws Exception {

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new PasswordUserRegistrationDto("TestUserDisplayName",
            "loginName", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated();

    webTestClient.post().uri("/api/v1/user/register/password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(
            new PasswordUserRegistrationDto("TestUser2", "loginName3", "paLswOrdha17£@£sh")))
        .exchange().expectStatus().isCreated();

    FluxExchangeResult<String> getResult = webTestClient.get().uri("/api/v1/user/list")
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(String.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(notNullValue()));

    }).expectComplete().verify();

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

  @Test
  @Disabled
  public void createPasswordUser_ValidData_RepositoryFindsCorrectUser() throws Exception {

    StepVerifier
        .create(userService.createPasswordUser("Test User", "user_login_name", "hashedPassword"))
        .assertNext(returnedUser -> {

          assertThat(returnedUser, is(notNullValue()));
          assertThat(returnedUser, is(instanceOf(User.class)));

          assertThat(returnedUser.getId(), is(notNullValue()));

          StepVerifier.create(userService.getById(returnedUser.getId())).assertNext(user -> {

            assertThat(user, is(notNullValue()));
            assertThat(user, is(instanceOf(User.class)));
            assertThat(user, is(returnedUser));

            assertThat(user.getAuth(), is(notNullValue()));
            assertThat(user.getAuth(), is(instanceOf(Auth.class)));
            assertThat(user.getAuth(), is(returnedUser.getAuth()));

            assertThat(user.getAuth().getPassword(), is(notNullValue()));
            assertThat(user.getAuth().getPassword(), is(instanceOf(PasswordAuth.class)));
            assertThat(user.getAuth().getPassword(), is(returnedUser.getAuth().getPassword()));

            assertThat(user.getAuth().getSaml(), is(nullValue()));

          }).expectComplete().verify();

        }).expectComplete().verify();


  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();
    
    userService.deleteAll().block();
  }

}
