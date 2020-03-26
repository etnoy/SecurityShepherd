package org.owasp.securityshepherd.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtUtil {

  private static final long EXPIRATION_TIME = 28800;

  public static final Key JWT_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  @Autowired
  UserService userService;

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token).getBody();
  }

  public Mono<User> getUserFromToken(String token) {
    final int userId = Integer.parseInt(getAllClaimsFromToken(token).getSubject());
    return userService.findById(userId);
  }

  public Date getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  private boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    ShepherdUserDetails userDetails = new ShepherdUserDetails(user);
    Role role = userDetails.getRole();

    claims.put("role", role);

    return doGenerateToken(claims, user.getId());
  }

  private String doGenerateToken(Map<String, Object> claims, int userId) {

    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + EXPIRATION_TIME * 1000);

    return Jwts.builder().setClaims(claims).setSubject(Integer.toString(userId))
        .setIssuedAt(createdDate).setExpiration(expirationDate).signWith(JWT_KEY).compact();

  }

  public boolean validateToken(String token) {
    // FIXME: Must check signature
    return !isTokenExpired(token);
  }

}
