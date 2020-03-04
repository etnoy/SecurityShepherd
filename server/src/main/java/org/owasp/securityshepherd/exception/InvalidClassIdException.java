package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidClassIdException extends InvalidEntityIdException {

  /**
   * 
   */
  private static final long serialVersionUID = -5350036580936173008L;

  public InvalidClassIdException(final String message) {
    super(message);
  }

  public InvalidClassIdException(final String message, final Exception e) {
    super(message, e);
  }

}
