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

package org.owasp.securityshepherd.service;

import java.security.Key;
import java.time.Clock;
import java.util.Date;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WebTokenService {

  private static final long EXPIRATION_TIME = 28800;

  public static final Key JWT_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  private final Clock clock;

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token).getBody();
  }

  public long getUserIdFromToken(final String token) {
    return Long.parseLong(getAllClaimsFromToken(token).getSubject());
  }

  public Date getExpirationDateFromToken(final String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  private boolean isTokenExpired(final String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(final long userId) {
    final Date creationTime = new Date(clock.millis());
    final Date expirationTime = new Date(clock.millis() + 1000 * EXPIRATION_TIME);

    return Jwts.builder().setSubject(Long.toString(userId)).setIssuedAt(creationTime)
        .setExpiration(expirationTime).signWith(JWT_KEY).compact();
  }

  public boolean validateToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (SignatureException e) {
      return false;
    }
  }
}
