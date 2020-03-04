package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserIdNotFoundException extends EntityIdNotFoundException {

  /**
   * 
   */
  private static final long serialVersionUID = -5692051527699555141L;

  public UserIdNotFoundException(final String message) {
    super(message);
  }

  public UserIdNotFoundException(final String message, final Exception e) {
    super(message, e);
  }

}
