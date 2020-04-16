package org.owasp.securityshepherd.module.sqlinjection;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.module.SubmittableModule;
import org.owasp.securityshepherd.service.ModuleService;
import org.springframework.data.r2dbc.BadSqlGrammarException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.R2dbcBadGrammarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqlInjectionTutorial implements SubmittableModule {

  private final ModuleService moduleService;

  private Long moduleId;

  public static final String MODULE_URL = "sql-injection-tutorial";

  @PostConstruct
  public Mono<Long> initialize() {
    log.info("Creating sql tutorial module");
    final Mono<Module> moduleMono = moduleService.create("Sql Injection Tutorial", MODULE_URL,
        "Tutorial for making sql injections");

    return moduleMono.flatMap(module -> {
      this.moduleId = module.getId();
      return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
    });
  }

  public Mono<String> submitQuery(final long userId, final String usernameQuery) {
    if (this.moduleId == null) {
      return Mono.error(new RuntimeException("Must initialize module before submitting to it"));
    }
    // Generate a dynamic flag and add it as a row to the database creation script. The flag is
    // different for every user to prevent copying flags
    final Mono<String> insertionQuery = moduleService.getDynamicFlag(userId, this.moduleId)
        .map(flag -> String.format(
            "INSERT INTO sqlinjection.users values ('666', 'Union Jack', 'Well done, flag is %s')",
            flag));

    // Create a connection URL to a H2SQL in-memory database. Each submission call creates a
    // completely new instance of this database.
    final Mono<String> connectionUrl = insertionQuery
        .map(query -> String.format("r2dbc:h2:mem:///sql-injection-tutorial-for-uid%d;"
            // Load the initial sql file
            + "INIT=RUNSCRIPT FROM 'classpath:module/sql-injection-tutorial.sql'" +
            // %5C%3B is a backslash and semicolon URL-formatted
            "%s%s", userId, "%5C%3B", query));

    // Create a DatabaseClient that allows us to manually interact with the database
    final Mono<DatabaseClient> databaseClientMono = connectionUrl
        // We have to replace all spaces with URL-encoded spaces for r2dbc to work
        .map(url -> url.replace(" ", "%20"))
        // Create a connection factory from the URL
        .map(ConnectionFactories::get)
        // Create a database client from the connection factory
        .map(DatabaseClient::create);

    // Create the database query. Yes, this is vulnerable to SQL injection. That's the whole point.
    final String injectionQuery =
        String.format("SELECT * FROM sqlinjection.users WHERE name = '%s'", usernameQuery);

    // Initialize json mapper and root node
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    return databaseClientMono
        // Execute database query
        .flatMapMany(databaseClient -> databaseClient.execute(injectionQuery).fetch().all())
        // Convert rows to string
        .map(Map::toString)
        // Collect all rows to a list
        .collectList()
        // Handle the happy path
        .map(rows -> {
          // Covert results to json
          ArrayNode arrayNode = rootNode.putArray("result");
          for (final String row : rows) {
            arrayNode.add(row);
          }
          // Convert json to string
          return rootNode.toString();
        })
        // Handle errors
        .onErrorResume(exception -> {
          // We want to forward database syntax errors to the user
          if (exception instanceof BadSqlGrammarException && exception.getCause() != null
              && exception.getCause() instanceof R2dbcBadGrammarException) {
            // We extract the nested R2dbcBadGrammarException and return it as json
            rootNode.put("error", exception.getCause().toString());
            return Mono.just(rootNode.toString());
          } else {
            // All other errors are handled in the usual way
            return Mono.error(exception);
          }
        });
  }

  @Override
  public Long getModuleId() {
    return this.moduleId;
  }
}
