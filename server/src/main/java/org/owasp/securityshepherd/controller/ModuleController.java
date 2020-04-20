package org.owasp.securityshepherd.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.dto.SubmissionDto;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.service.ModuleService;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ModuleController {
  private final SubmissionService submissionService;

  private final ModuleService moduleService;

  private final ObjectMapper objectMapper;

  @GetMapping(path = "module/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getModuleById(@PathVariable final long id) {
    return moduleService.findById(id);
  }

  @GetMapping(path = "module/by-name/{shortName}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getModuleByShortName(@PathVariable final String shortName) {
    return moduleService.findByShortName(shortName);
  }
  
  @GetMapping(path = "modules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Module> findAll() {
    return moduleService.findAll();
  }

  @PostMapping(path = "module/submit/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Object> submitFlag(@PathVariable("id") final Long moduleId,
      @RequestBody final String request) {
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class).flatMapMany(userId -> {
          try {
            return submissionService.submit(userId, moduleId,
                objectMapper.readTree(request).get("flag").asText());
          } catch (JsonProcessingException e) {
            return Mono.error(e);
          }
        });
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
