package org.owasp.securityshepherd.exception;

public class DuplicateUserDisplayNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5442400129568765216L;

	public DuplicateUserDisplayNameException(final String message, final Exception e) {
		super(message, e);
	}

	public DuplicateUserDisplayNameException(final String message) {
		super(message);
	}
	
}