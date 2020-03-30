package org.owasp.securityshepherd.test.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.SecurityShepherdApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Application")
class SecurityShepherdApplicationTest {

  @Test
  @DisplayName("Application context loads")
  public void main_ApplicationStartup_ContextLoads() throws Throwable {
     SecurityShepherdApplication.main(new String[] {});
  }
}
