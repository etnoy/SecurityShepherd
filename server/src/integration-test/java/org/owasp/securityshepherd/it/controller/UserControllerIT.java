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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.authentication.PasswordRegistrationDto;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserService;
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
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
properties = {"application.runner.enabled=false"})
@AutoConfigureWebTestClient
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("UserController integration test")
public class UserControllerIT {
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
  public void getUserList_AuthenticatedUser_Forbidden() {
    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    String token = JsonPath.parse(
        new String(webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"test\"}"),
                String.class))
            .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    webTestClient.get().uri("/api/v1/users").header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isForbidden();
  }

  @Test
  public void getUserList_UserPromotedToAdmin_Success() {
    final String loginName = "test";
    final String hashedPassword = "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW";

    final long userId =
        userService.createPasswordUser("Test User", loginName, hashedPassword).block();

    String token = JsonPath.parse(
        new String(webTestClient.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"test\"}"),
                String.class))
            .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    webTestClient.get().uri("/api/v1/users").header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isForbidden();

    // Promote user to admin
    userService.promote(userId).block();

    // Now the user should be able to see user list
    webTestClient.get().uri("/api/v1/users").header("Authorization", "Bearer " + token)
    .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk();
  }

  @Test
  public void listUsers_UsersExist_ReturnsUserList() throws Exception {
    final String loginName = "test";
    final String password = "paLswOrdha17£@£sh";

    HashSet<Long> userIdSet = new HashSet<Long>();

    final long userId = webTestClient.post().uri("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters
            .fromValue(new PasswordRegistrationDto("TestUserDisplayName", loginName, password)))
        .exchange().expectStatus().isCreated().expectBody(Long.class).returnResult()
        .getResponseBody();

    // Promote this user to admin
    userService.promote(userId).block();

    userIdSet.add(userId);

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    userIdSet
        .add(webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                new PasswordRegistrationDto("TestUser2", "loginName2", "paLswOrdha17£@£sh")))
            .exchange().expectStatus().isCreated().expectBody(Long.class).returnResult()
            .getResponseBody());

    userIdSet
        .add(webTestClient.post().uri("/api/v1/register").contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                new PasswordRegistrationDto("TestUser3", "loginName3", "paLswOrdha17£@£sh")))
            .exchange().expectStatus().isCreated().expectBody(Long.class).returnResult()
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
  public void register_ValidData_ReturnsValidUser() throws Exception {
    final String loginName = "test";
    final String password = "paLswOrdha17£@£sh";

    final int userId = webTestClient.post().uri("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(Mono.just("{\"displayName\": \"" + loginName
            + "\", \"userName\": \"" + loginName + "\",  \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isCreated().expectBody(Integer.class).returnResult()
        .getResponseBody();

    // Promote this user to admin
    userService.promote(userId).block();

    String token = JsonPath.parse(new String(webTestClient.post().uri("/api/v1/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromPublisher(
            Mono.just("{\"userName\": \"" + loginName + "\", \"password\": \"" + password + "\"}"),
            String.class))
        .exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody()))
        .read("$.token");

    FluxExchangeResult<User> getResult = webTestClient.get()
        .uri("/api/v1/user/" + Integer.toString(userId)).header("Authorization", "Bearer " + token)
        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
        .contentType(MediaType.APPLICATION_JSON).returnResult(User.class);

    StepVerifier.create(getResult.getResponseBody()).assertNext(getData -> {

      assertThat(getData, is(userService.findById(userId).block()));

    }).expectComplete().verify();
  }

  @BeforeEach
  private void setUp() {
    testService.deleteAll().block();
  }
}
