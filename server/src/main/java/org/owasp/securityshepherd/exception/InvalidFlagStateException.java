package org.owasp.securityshepherd.exception;

public class InvalidFlagStateException extends Exception {

  private static final long serialVersionUID = -4875847423419350969L;

  public InvalidFlagStateException(final String message) {
    super(message);
  }

  public InvalidFlagStateException(final String message, final Exception e) {
    super(message, e);
  }
}
