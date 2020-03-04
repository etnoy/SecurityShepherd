package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginNameNotFoundException extends EntityIdNotFoundException {

  /**
   * 
   */
  private static final long serialVersionUID = -7957491465943328966L;

  public LoginNameNotFoundException(final String message) {
    super(message);
  }

  public LoginNameNotFoundException(final String message, final Exception e) {
    super(message, e);
  }

}
