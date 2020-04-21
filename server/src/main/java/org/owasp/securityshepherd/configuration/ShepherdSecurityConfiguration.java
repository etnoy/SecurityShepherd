/**
 * This file is part of Security Shepherd.
 *
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.owasp.securityshepherd.configuration;

import org.owasp.securityshepherd.repository.SecurityContextRepository;
import org.owasp.securityshepherd.security.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ShepherdSecurityConfiguration {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity,
      AuthenticationManager authenticationManager,
      SecurityContextRepository securityContextRepository) {
    return serverHttpSecurity
        //
        .exceptionHandling()
        //
        .authenticationEntryPoint(
            (serverWebExchange, authenticationException) -> Mono.fromRunnable(() -> {
              serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
              Mono.error(authenticationException);
            }))
        .accessDeniedHandler((serverWebExchange, authenticationException) -> Mono.fromRunnable(
            () -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
        .and()
        //
        .csrf().disable()
        //
        .formLogin().disable()
        //
        .httpBasic().disable()
        //
        .authenticationManager(authenticationManager)
        .securityContextRepository(securityContextRepository).authorizeExchange()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/api/v1/register").permitAll()
        .pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/api/v1/login").permitAll()
        .anyExchange().authenticated().and().build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:4200");
    config.addAllowedHeader("*");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  @Primary
  public ObjectMapper buildObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    // Required for parsing LocalDateTime
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
