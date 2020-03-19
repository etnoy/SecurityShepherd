package org.owasp.securityshepherd.it.auth;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIT {

  @Autowired
  UserService userService;

  @Test
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
  }

}
