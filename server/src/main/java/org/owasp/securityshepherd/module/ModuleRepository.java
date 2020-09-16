/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.module;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ModuleRepository extends ReactiveCrudRepository<Module, Long> {
  @Modifying
  @Query("delete from module where name = :name")
  public void deleteByName(@Param("name") final String name);

  @Query("select count(1) from module where name = :name")
  public Mono<Boolean> existsByName(@Param("name") final String name);

  @Query("select * from module where name = :name")
  public Mono<Module> findByName(@Param("name") final String name);

  @Query("select * from module where short_name = :short_name")
  public Mono<Module> findByShortName(@Param("url") final String short_name);

  @Query("select * from module where is_open = true")
  public Flux<Module> findAllOpen();
}
