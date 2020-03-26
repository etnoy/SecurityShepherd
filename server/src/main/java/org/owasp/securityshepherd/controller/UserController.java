package org.owasp.securityshepherd.controller;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/")
public class UserController {

  private final UserService userService;

  @PostMapping(path = "user/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Mono<Void> deleteById(@PathVariable final int id) {
    log.debug("Deleting user with id " + id);
    
    return userService.deleteById(id);
  }

  @GetMapping(path = "users")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Flux<User> findAll() {
    return userService.findAll();
  }

  @GetMapping(path = "user/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Mono<User> getById(@PathVariable final int id) {
    return userService.findById(id);
  }

}
