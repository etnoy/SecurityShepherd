package org.owasp.securityshepherd.it.module;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.module.SqlModule;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SqlModule integration test")
public class SqlModuleIT {

  SqlModule sqlModule;

  @Autowired
  TestUtils testUtils;

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Test
  public void testSqlModule() {
    final Long userId1 = userService.create("TestUser1").block();
    final Long userId2 = userService.create("TestUser2").block();

    final long moduleId = moduleService.create("ScoreTestModule").block().getId();

    moduleService.setDynamicFlag(moduleId).block();

    sqlModule = new SqlModule(moduleService);

    StepVerifier.create(sqlModule.submitSql(userId2, moduleId, "OR 1=1")).expectNextCount(1)
        .expectComplete().verify();

    StepVerifier.create(sqlModule.submitSql(userId2, moduleId, "' OR '1' = '1")).expectNextCount(6)
        .expectComplete().verify();
  }

  @BeforeEach
  private void clear() {
    testUtils.deleteAll().block();
  }
}
