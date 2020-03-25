package org.owasp.securityshepherd.it.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.SecurityShepherdApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SecurityShepherdApplicationIT {

  @Test
  public void applicationContextTest() throws Throwable {
     SecurityShepherdApplication.main(new String[] {});
  }

}
