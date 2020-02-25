package org.owasp.securityshepherd.exception;

public class DuplicateUserLoginNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3346040286993109868L;

	public DuplicateUserLoginNameException(final String message, final Exception e) {
		super(message, e);
	}

	public DuplicateUserLoginNameException(final String message) {
		super(message);
	}
	
}