package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClassIdNotFoundException extends EntityIdNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 848671651267142565L;

	public ClassIdNotFoundException(final String message, final Exception e) {
		super(message, e);
	}

	public ClassIdNotFoundException(final String message) {
		super(message);
	}
	
}