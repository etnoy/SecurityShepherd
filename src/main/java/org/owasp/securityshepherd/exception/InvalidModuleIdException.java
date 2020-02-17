package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidModuleIdException extends InvalidEntityIdException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3189695115052847549L;

	public InvalidModuleIdException(final String message, final Exception e) {
		super(message, e);
	}

	public InvalidModuleIdException(final String message) {
		super(message);
	}
	
}

