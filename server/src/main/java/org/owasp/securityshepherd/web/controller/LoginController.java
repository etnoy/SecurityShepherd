package org.owasp.securityshepherd.web.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.security.AuthRequest;
import org.owasp.securityshepherd.security.AuthResponse;
import org.owasp.securityshepherd.security.JwtUtil;
import org.owasp.securityshepherd.security.ShepherdUserDetails;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.dto.PasswordUserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/")
public class LoginController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping(value = "/login")
  public Mono<ResponseEntity> login(@RequestBody AuthRequest authRequest) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    return userService.getByLoginName(authRequest.getUsername())
        .map(user -> {
          UserDetails userDetails = new ShepherdUserDetails(user);
          if (encoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(user)));
          } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
          }
        })
        .onErrorReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        .cast(ResponseEntity.class)
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PostMapping(path = "/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Integer> register(@Valid @RequestBody final PasswordUserRegistrationDto registerDto) {

    return userService.createPasswordUser(registerDto.getDisplayName(), registerDto.getLoginName(),
        passwordEncoder.encode(registerDto.getPassword())).map(User::getId);

  }

}
