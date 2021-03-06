/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.scoring;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RankedSubmissionRepository extends ReactiveCrudRepository<RankedSubmission, Long> {
  @Query("SELECT * from ranked_submission WHERE module_name = :module_name")
  public Flux<RankedSubmission> findAllByModuleName(@Param("module_name") final long moduleName);

  @Query("SELECT * from ranked_submission WHERE user_id = :user_id")
  public Flux<RankedSubmission> findAllByUserId(@Param("user_id") final long userId);

  @Query("SELECT * from ranked_submission WHERE user_id = :user_id AND module_name = :module_name")
  public Mono<RankedSubmission> findAllByUserIdAndModuleName(
      @Param("user_id") final long userId, @Param("module_name") final long moduleName);
}
