package org.owasp.securityshepherd.configuration;

import org.owasp.securityshepherd.repository.SecurityContextRepository;
import org.owasp.securityshepherd.security.AuthenticationManager;
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
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ShepherdSecurityConfiguration {
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity,
      AuthenticationManager authenticationManager,
      SecurityContextRepository securityContextRepository) {
    return serverHttpSecurity.exceptionHandling().authenticationEntryPoint(
        (serverWebExchange, authenticationException) -> Mono.fromRunnable(() -> {
          serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          Mono.error(authenticationException);
        }))
        .accessDeniedHandler((serverWebExchange, authenticationException) -> Mono.fromRunnable(
            () -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
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
