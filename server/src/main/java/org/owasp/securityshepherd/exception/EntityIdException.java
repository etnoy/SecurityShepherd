package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class EntityIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4138243642687772483L;

	public EntityIdException(final String message, final Exception e) {
		super(message, e);
	}

	public EntityIdException(final String message) {
		super(message);
	}

}