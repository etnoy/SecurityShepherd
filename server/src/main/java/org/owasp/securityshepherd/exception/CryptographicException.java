package org.owasp.securityshepherd.exception;

public class CryptographicException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -4269722221163971517L;

  public CryptographicException(final String message) {
    super(message);
  }

  public CryptographicException(final String message, final Exception e) {
    super(message, e);
  }

}
