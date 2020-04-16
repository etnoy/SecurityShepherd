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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
@RequestMapping("/api/v1/")
public class ModuleController {
  private final SubmissionService submissionService;

  private final ModuleService moduleService;

  private final SqlInjectionTutorial sqlInjectionTutorial;

  private final XssTutorial xssTutorial;

  @GetMapping(path = "module/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<Module> getById(@PathVariable final int id) {
    return moduleService.findById(id);
  }

  @GetMapping(path = "modules")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Module> findAll() {
    return moduleService.findAll();
  }

  @PostMapping(path = "module/{id}/{resource}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Object> postResourceById(@PathVariable("id") final Long moduleId,
      @PathVariable("resource") final String resource, @RequestBody final String request) {
    final ObjectMapper jsonObjectMapper = new ObjectMapper();
    return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal).cast(Long.class).flatMapMany(userId -> {
          if (resource.equals("submit")) {
            try {
              return submissionService
                  .submit(userId, moduleId, jsonObjectMapper.readTree(request).get("flag").asText())
                  .map(submission -> {
                    try {
                      return jsonObjectMapper.writeValueAsString(submission);
                    } catch (JsonProcessingException e) {
                      return Mono.error(e);
                    }
                  });
            } catch (JsonProcessingException e) {
              return Mono.error(e);
            }
          } else {
            return moduleService.findById(moduleId).flatMapMany(module -> {
              switch (module.getUrl()) {
                case (SqlInjectionTutorial.MODULE_URL):
                  try {
                    return this.sqlInjectionTutorial.submitQuery(userId,
                        jsonObjectMapper.readTree(request).get("query").asText());
                  } catch (JsonProcessingException e) {
                    return Mono.error(e);
                  }
                case (XssTutorial.MODULE_URL):
                  try {
                    return this.xssTutorial.submitQuery(userId,
                        jsonObjectMapper.readTree(request).get("query").asText());
                  } catch (JsonProcessingException e) {
                    return Mono.error(e);
                  }
                default:
                  throw new RuntimeException(
                      String.format("Module %s could not be identified", module.getUrl()));
              }
            });
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
