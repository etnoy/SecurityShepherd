package org.owasp.securityshepherd.exception;

public class InvalidFlagException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5098332156898854294L;

	public InvalidFlagException(final String message, final Exception e) {
		super(message, e);
	}

	public InvalidFlagException(final String message) {
		super(message);
	}

}