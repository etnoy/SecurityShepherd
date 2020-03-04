package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidUserIdException extends InvalidEntityIdException {

  /**
   * 
   */
  private static final long serialVersionUID = -6203877167408885331L;

  public InvalidUserIdException(final String message) {
    super(message);
  }

  public InvalidUserIdException(final String message, final Exception e) {
    super(message, e);
  }

}
