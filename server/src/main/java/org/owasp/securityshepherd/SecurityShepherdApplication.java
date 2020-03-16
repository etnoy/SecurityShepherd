package org.owasp.securityshepherd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
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
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data-mysql.sql")));
    initializer.setDatabasePopulator(populator);

    return initializer;
  }

}
