package org.owasp.securityshepherd.exception;

public class RNGException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -2579994940836702698L;

  public RNGException(final String message) {
    super(message);
  }

  public RNGException(final String message, final Exception e) {
    super(message, e);
  }

}
