package org.owasp.securityshepherd.repository;

import java.time.LocalDateTime;
import org.owasp.securityshepherd.dto.RankedSubmissionDto;
import org.owasp.securityshepherd.dto.RankedSubmissionDto.RankedSubmissionDtoBuilder;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class SubmissionDatabaseClient {
  private final DatabaseClient databaseClient;

  public Flux<RankedSubmissionDto> findAllValidByModuleIdSortedBySubmissionTime(
      final int moduleId) {
    return databaseClient.execute(
        "SELECT user_id, time, RANK() over(ORDER BY time) user_rank from submission WHERE is_valid = true AND module_id = "
            + moduleId)
        .map((row, rowMetadata) -> {
          final RankedSubmissionDtoBuilder rankedSubmissionDtoBuilder =
              RankedSubmissionDto.builder();
          rankedSubmissionDtoBuilder.userId(row.get("user_id", Integer.class));
          rankedSubmissionDtoBuilder.time(row.get("time", LocalDateTime.class));
          rankedSubmissionDtoBuilder.rank(Math.toIntExact(row.get("user_rank", Long.class)));
          return rankedSubmissionDtoBuilder.build();
        }).all();
  }
}
