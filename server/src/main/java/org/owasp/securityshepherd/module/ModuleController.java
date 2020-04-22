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

package org.owasp.securityshepherd.module;

import javax.validation.Valid;
import org.owasp.securityshepherd.dto.SubmissionDto;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.service.SubmissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ModuleController {
  private final SubmissionService submissionService;

  private final ModuleService moduleService;
  
  private final ModuleSolutions moduleListingComponent;

  @GetMapping(path = "module/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getModuleById(@PathVariable final long id) {
    return moduleService.findById(id);
  }

  @GetMapping(path = "module/by-name/{shortName}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<ModuleListItem> getModuleByShortName(@PathVariable final String shortName) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class)
        .flatMap(userId -> moduleListingComponent.findOpenModuleByShortNameWithSolutionStatus(userId, shortName));
  }

  @GetMapping(path = "modules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<ModuleListItem> findAll() {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class)
        .flatMapMany(moduleListingComponent::findOpenModulesByUserIdWithSolutionStatus);
  }

  @PostMapping(path = "module/submit/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Submission> submitFlag(@PathVariable("id") final Long moduleId,
      @RequestBody final String flag) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class)
        .flatMap(userId -> submissionService.submit(userId, moduleId, flag));
  }

  @PostMapping(path = "module/")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Boolean> submitById(@RequestBody @Valid SubmissionDto submissionDto) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class)
        .flatMap(userId -> submissionService
            .submit(userId, submissionDto.getModuleId(), submissionDto.getFlag())
            .map(Submission::isValid));
  }
}
