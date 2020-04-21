package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Submission;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SubmissionRepository extends ReactiveCrudRepository<Submission, Long> {
  @Query("SELECT * from submission WHERE module_id = :module_id")
  public Flux<Submission> findAllByModuleId(@Param("module_id") final long moduleId);

  @Query("SELECT * from submission WHERE user_id = :user_id and is_valid = 1")
  public Flux<Submission> findAllValidByUserId(@Param("user_id") final long userId);

  @Query("SELECT * from submission WHERE user_id = :user_id AND module_id = :module_id and is_valid = 1")
  public Flux<Submission> findValidByUserIdAndModuleId(@Param("user_id") final long userId,
      @Param("module_id") final long moduleId);
}
