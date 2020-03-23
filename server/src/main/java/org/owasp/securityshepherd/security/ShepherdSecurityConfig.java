package org.owasp.securityshepherd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class ShepherdSecurityConfig {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private SecurityContextRepository securityContextRepository;

    
  @Bean
  public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
    return http.exceptionHandling().authenticationEntryPoint((swe, e) -> {
      return Mono.fromRunnable(() -> {
        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      });
    }).accessDeniedHandler((swe, e) -> {
      return Mono.fromRunnable(() -> {
        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      });
    }).and().csrf().disable().formLogin().disable().httpBasic().disable()
        .authenticationManager(authenticationManager)
        .securityContextRepository(securityContextRepository).authorizeExchange()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/login").permitAll()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/create").permitAll()
        .anyExchange().authenticated().and().build();
  }

}
