package org.owasp.securityshepherd;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
@EnableWebFluxSecurity
public class SecurityShepherdApplication {

  public static void main(String[] args) throws Throwable {
    SpringApplication.run(SecurityShepherdApplication.class, args);
  }

  @Bean
  public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator
        .addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema-mysql.sql")));
    initializer.setDatabasePopulator(populator);

    return initializer;
  }

  @Bean
  public Clock clock() {
      return Clock.systemDefaultZone();
  }
  
}
