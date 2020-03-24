package org.owasp.securityshepherd.web.controller;

import java.io.UnsupportedEncodingException;
import org.owasp.securityshepherd.security.AuthRequest;
import org.owasp.securityshepherd.security.AuthResponse;
import org.owasp.securityshepherd.security.JWTUtil;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/v1/")
public class LoginController {

  @Autowired
  private UserService userService;

  @Autowired
  private JWTUtil jwtUtil;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest authRequest) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    return userService.getByLoginName(authRequest.getUsername()).map((userDetails) -> {

      if (encoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(userDetails)));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      
    }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }


}
