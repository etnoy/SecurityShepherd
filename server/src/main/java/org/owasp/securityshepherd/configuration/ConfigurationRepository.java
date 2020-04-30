/**
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

package org.owasp.securityshepherd.configuration;

import org.owasp.securityshepherd.model.Configuration;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface ConfigurationRepository extends ReactiveCrudRepository<Configuration, Long> {
  @Modifying
  @Query("delete from configuration where config_key = :key")
  public void deleteByKey(@Param("key") final String key);

  @Query("select count(1) from configuration where config_key = :key")
  public Mono<Boolean> existsByKey(@Param("key") final String key);

  @Query("select * from configuration where config_key = :key")
  public Mono<Configuration> findByKey(@Param("key") final String key);
}
