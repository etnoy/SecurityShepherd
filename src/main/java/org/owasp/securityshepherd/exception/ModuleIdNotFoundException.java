package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ModuleIdNotFoundException extends EntityIdNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2976620163436522813L;

	public ModuleIdNotFoundException(final String message, final Exception e) {
		super(message, e);
	}

	public ModuleIdNotFoundException(final String message) {
		super(message);
	}
	
}