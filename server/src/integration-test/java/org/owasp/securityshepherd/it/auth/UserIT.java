package org.owasp.securityshepherd.it.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.DatabaseService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class UserIT {

  @Autowired
  UserService userService;
  
  @Autowired
  DatabaseService databaseService;

  @Test
  public void createPasswordUser_ValidData_RepositoryFindsCorrectUser() throws Exception {
    final User testUser = userService.createPasswordUser("Test User", "user_login_name",
        "$2y$12$53B6QcsGwF3Os1GVFUFSQOhIPXnWFfuEkRJdbknFWnkXfUBMUKhaW").block();

    StepVerifier.create(userService.findById(testUser.getId())).expectNext(testUser)
        .expectComplete().verify();
  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    databaseService.clearAll().block();
  }

}
