package org.owasp.securityshepherd.exception;

public class DuplicateModuleNameException extends Exception {

  private static final long serialVersionUID = 1164167605174502173L;

  public DuplicateModuleNameException(final String message) {
    super(message);
  }

  public DuplicateModuleNameException(final String message, final Exception e) {
    super(message, e);
  }

}
