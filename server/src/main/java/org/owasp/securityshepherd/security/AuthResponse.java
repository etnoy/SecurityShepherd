package org.owasp.securityshepherd.security;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AuthResponse implements Serializable {
  private static final long serialVersionUID = 5631647781132001719L;
  private String token;
}
