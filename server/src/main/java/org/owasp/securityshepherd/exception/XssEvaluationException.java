package org.owasp.securityshepherd.exception;

public class XssEvaluationException extends RuntimeException {

  private static final long serialVersionUID = 2621122945600165910L;

  public XssEvaluationException(final String message) {
    super(message);
  }

  public XssEvaluationException(final String message, final Exception e) {
    super(message, e);
  }
  
  public XssEvaluationException(final Exception e) {
    super(e);
  }

}
