package org.owasp.securityshepherd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ShepherdSecurityConfig {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private SecurityContextRepository securityContextRepository;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.exceptionHandling().authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
      log.debug("Denied: " + swe + e);
      swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      Mono.error(e);
    })).accessDeniedHandler(
        (swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
        .and().csrf().disable().formLogin().disable().httpBasic().disable()
        .authenticationManager(authenticationManager)
        .securityContextRepository(securityContextRepository).authorizeExchange()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/api/v1/register").permitAll()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/api/v1/login").permitAll()
        .anyExchange().authenticated().and().build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
