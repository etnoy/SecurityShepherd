package org.owasp.securityshepherd.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.dto.SubmissionDto;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
@RequestMapping("/api/v1/")
public class ModuleController {
  private final SubmissionService submissionService;

  private final ModuleService moduleService;

  @GetMapping(path = "modules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Module> findAll() {
    return moduleService.findAll();
  }

  @GetMapping(path = "module/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getById(@PathVariable final int id) {
    return moduleService.findById(id);
  }

  @PostMapping(path = "module/submit")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Boolean> submitById(@RequestBody @Valid SubmissionDto submissionDto) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class).flatMap(
            id -> submissionService.submit(id, submissionDto.getModuleId(), submissionDto.getFlag())
                .map(Submission::isValid));
  }
}
