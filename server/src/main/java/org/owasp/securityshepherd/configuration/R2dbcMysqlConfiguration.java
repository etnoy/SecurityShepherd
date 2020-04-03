package org.owasp.securityshepherd.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class R2dbcMysqlConfiguration {
  @Bean
  @Primary
  public MySqlConnectionFactory mySqlConnectionFactory() {
    return MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder().host("localhost")
        .username("root").password("").database("core").build());
  }

  @Bean
  @Primary
  public ConnectionFactoryInitializer mySqlInitializer(ConnectionFactory connectionFactory) {
    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator
        .addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema-mysql.sql")));
    initializer.setDatabasePopulator(populator);

    return initializer;
  }
}
