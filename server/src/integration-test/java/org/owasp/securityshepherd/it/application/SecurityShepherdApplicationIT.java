package org.owasp.securityshepherd.it.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.SecurityShepherdApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Application integration test")
class SecurityShepherdApplicationIT {
  @Test
  @DisplayName("Application context loads")
  public void main_ApplicationStartup_ContextLoads() {
    // We just test that no exception is thrown
    assertDoesNotThrow(() -> SecurityShepherdApplication.main(new String[] {}));;
  }
}
