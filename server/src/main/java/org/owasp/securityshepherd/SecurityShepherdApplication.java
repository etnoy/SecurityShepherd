package org.owasp.securityshepherd;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
public class SecurityShepherdApplication {

  public static void main(String[] args) throws Throwable {
    SpringApplication.run(SecurityShepherdApplication.class, args);
  }

  @Bean
  public Clock clock() {
      return Clock.systemDefaultZone();
  }
  
}
