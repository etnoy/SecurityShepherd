package org.owasp.securityshepherd.exception;

public class DuplicateClassNameException extends Exception {

	private static final long serialVersionUID = 4254881749730515445L;

	public DuplicateClassNameException(final String message, final Exception e) {
		super(message, e);
	}

	public DuplicateClassNameException(final String message) {
		super(message);
	}
	
}