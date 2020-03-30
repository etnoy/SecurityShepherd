package org.owasp.securityshepherd.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class SubmissionDatabaseClient {

  private final DatabaseClient databaseClient;

  public Flux<Map<String, Integer>> findAllValidByModuleIdSortedBySubmissionTime(
      final int moduleId) {

    return databaseClient.execute(
        "SELECT user_id, RANK() over(ORDER BY time) user_rank from submission WHERE is_valid = true AND module_id = "
            + moduleId)
        .map((row, rowMetadata) -> {
          Map<String, Integer> resultMap = new HashMap<>();
          resultMap.put("userId", row.get("user_id", Integer.class));
          resultMap.put("rank", Math.toIntExact(row.get("user_rank", Long.class)));
          return resultMap;
        }).all();

  }

}
