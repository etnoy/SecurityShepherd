package org.owasp.securityshepherd.web.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.dto.PasswordUserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping(path = "/list")
  public Flux<User> findAll() {

    log.debug("Listing all users");

    return userService.findAll();

  }

  @PostMapping(path = "/register/password")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<User> register(@Valid @RequestBody final PasswordUserRegistrationDto registerDto) {

    log.debug("Registering user account with information: {}", registerDto);

    return userService.createPasswordUser(registerDto.getDisplayName(), registerDto.getLoginName(),
        registerDto.getPassword());

  }

}
