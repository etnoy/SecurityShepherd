package org.owasp.securityshepherd.web.controller;

import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @GetMapping(path = "/api/v1/user/add/{displayName}")
  public ResponseEntity<Mono<String>> addUser(@PathVariable String displayName) {

    // TODO: Set correct status code on mono exception
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.create(displayName).map(user -> user.toString()));

  }

  @GetMapping(path = "/api/v1/user/list")
  public Flux<User> allUsers() {

    return userRepository.findAll();

  }

}
