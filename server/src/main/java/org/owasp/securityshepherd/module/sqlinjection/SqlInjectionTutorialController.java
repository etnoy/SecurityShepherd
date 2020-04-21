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

package org.owasp.securityshepherd.module.sqlinjection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/module/" + SqlInjectionTutorial.SHORT_NAME)
public class SqlInjectionTutorialController {
  private final SqlInjectionTutorial sqlInjectionTutorial;

  @PostMapping(path = "search")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<SqlInjectionTutorialRow> search(@RequestBody final String query) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class)
        .flatMapMany(userId -> sqlInjectionTutorial.submitQuery(userId, query));
  }
}
