package org.owasp.securityshepherd.service;

import java.security.Key;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.SubmissionDatabaseClient;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.security.ShepherdUserDetails;
import org.owasp.securityshepherd.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class WebTokenService {

  private static final long EXPIRATION_TIME = 28800;

  public static final Key JWT_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  private final Clock clock;
  
  private final UserService userService;

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token).getBody();
  }

  public Mono<ShepherdUserDetails> getUserDetailsFromToken(final String token) {
    final int userId = Integer.parseInt(getAllClaimsFromToken(token).getSubject());
    return userService.findUserDetailsByUserId(userId);
  }

  public Date getExpirationDateFromToken(final String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  private boolean isTokenExpired(final String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(final ShepherdUserDetails userDetails) {
    final Date creationTime = new Date(clock.millis());
    final Date expirationTime = new Date(clock.millis() + 1000*EXPIRATION_TIME);
    
    return Jwts.builder().setSubject(Integer.toString(userDetails.getUserId()))
        .setIssuedAt(creationTime).setExpiration(expirationTime).signWith(JWT_KEY).compact();
  }

  public boolean validateToken(String token) {
    return !isTokenExpired(token);
  }

}
