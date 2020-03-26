package org.owasp.securityshepherd.web.controller;

import javax.validation.Valid;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.security.AuthResponse;
import org.owasp.securityshepherd.security.JwtUtil;
import org.owasp.securityshepherd.security.ShepherdUserDetails;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.web.dto.PasswordLoginDto;
import org.owasp.securityshepherd.web.dto.PasswordRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
  public Mono<ResponseEntity<AuthResponse>> login(@RequestBody @Valid PasswordLoginDto loginDto) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    final Mono<User> userMono = userService.findByLoginName(loginDto.getUserName());

    return userMono.map(ShepherdUserDetails::new)
        .filter(userDetails -> encoder.matches(loginDto.getPassword(), userDetails.getPassword()))
        .zipWith(userMono).map(Tuple2::getT2).map(jwtUtil::generateToken).map(AuthResponse::new)
        .map(authResponse -> new ResponseEntity<>(authResponse, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

  }

  @PostMapping(path = "/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Integer> register(@Valid @RequestBody final PasswordRegistrationDto registerDto) {

    return userService.createPasswordUser(registerDto.getDisplayName(), registerDto.getUserName(),
        passwordEncoder.encode(registerDto.getPassword())).map(User::getId);

  }

}
