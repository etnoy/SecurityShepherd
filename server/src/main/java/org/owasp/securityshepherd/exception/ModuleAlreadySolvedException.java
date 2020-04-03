package org.owasp.securityshepherd.exception;

public class ModuleAlreadySolvedException extends Exception {

  private static final long serialVersionUID = 1983915587716845537L;

  public ModuleAlreadySolvedException(final String message) {
    super(message);
  }

  public ModuleAlreadySolvedException(final String message, final Exception e) {
    super(message, e);
  }

}
