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

package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.UserAuth;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface PasswordAuthRepository extends ReactiveCrudRepository<PasswordAuth, Long> {
  @Query("SELECT * from password_auth WHERE login_name = :login_name")
  public Mono<PasswordAuth> findByLoginName(@Param("login_name") final String loginName);

  @Query("SELECT * from password_auth WHERE user_id = :user_id")
  public Mono<PasswordAuth> findByUserId(@Param("user_id") final long userId);

  @Modifying
  @Query("delete from password_auth WHERE user_id = :user_id")
  public Mono<UserAuth> deleteByUserId(@Param("user_id") final long userId);
}
