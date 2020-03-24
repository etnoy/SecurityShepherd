package org.owasp.securityshepherd.web.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.security.Message;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.dto.PasswordUserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/v1/")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping(path = "/deleteAll")
  public Mono<Void> deleteAll() {

    return userService.deleteAll();

  }

  @PostMapping(path = "/delete/{id}")
  public Mono<Void> deleteById(@PathVariable int id) {

    return userService.deleteById(id);

  }

  @GetMapping(path = "/list")
  public Flux<User> findAll() {

    return userService.findAll();

  }

  @GetMapping(path = "/{id}")
  public Mono<User> getById(@PathVariable int id) {

    return userService.getById(id);

  }

  @PostMapping(path = "/register/password")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<User> register(@Valid @RequestBody final PasswordUserRegistrationDto registerDto) {

    return userService.createPasswordUser(registerDto.getDisplayName(), registerDto.getLoginName(),
        registerDto.getPassword());

  }

  @RequestMapping(value = "/resource/user", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<ResponseEntity<?>> user() {
    log.debug("User Resource");

    return Mono.just(ResponseEntity.ok(new Message("Content for user")));
  }

  @RequestMapping(value = "/resource/admin", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<ResponseEntity<?>> admin() {
    log.debug("Admin Resource");

    return Mono.just(ResponseEntity.ok(new Message("Content for admin")));
  }

  @RequestMapping(value = "/resource/user-or-admin", method = RequestMethod.GET)
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public Mono<ResponseEntity<?>> userOrAdmin() {
    return Mono.just(ResponseEntity.ok(new Message("Content for user or admin")));
  }

}
