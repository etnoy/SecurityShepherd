package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class InvalidEntityIdException extends EntityIdException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1260769378077474805L;

	public InvalidEntityIdException(final String message, final Exception e) {
		super(message, e);
	}

	public InvalidEntityIdException(final String message) {
		super(message);
	}

}