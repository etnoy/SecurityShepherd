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
package org.owasp.securityshepherd.module;

import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ModuleController {
  private final ModuleService moduleService;

  private final ModuleSolutions moduleSolutions;

  private final ControllerAuthentication controllerAuthentication;

  @GetMapping(path = "modules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<ModuleListItem> findAllByUserId() {
    return controllerAuthentication
        .getUserId()
        .flatMapMany(moduleSolutions::findOpenModulesByUserIdWithSolutionStatus);
  }

  @GetMapping(path = "module/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getModuleById(@Min(1) @PathVariable final long id) {
    return moduleService.findById(id);
  }

  @GetMapping(path = "module/by-name/{shortName}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<ModuleListItem> getModuleByShortName(@PathVariable final String shortName) {
    return controllerAuthentication
        .getUserId()
        .flatMap(
            userId ->
                moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(userId, shortName));
  }
}
