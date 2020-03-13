package org.owasp.securityshepherd;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
public class SecurityShepherdApplication {

  public static void main(String[] args) throws Throwable {
    SpringApplication.run(SecurityShepherdApplication.class, args);
  }

  public R2dbcCustomConversions r2dbcCustomConversions() {

    List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
    converterList.add(new UserReadConverter());
    return new R2dbcCustomConversions(converterList);

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
