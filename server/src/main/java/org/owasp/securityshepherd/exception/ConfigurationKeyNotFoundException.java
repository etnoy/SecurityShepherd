package org.owasp.securityshepherd.exception;

public class ConfigurationKeyNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -761777268031129302L;

	public ConfigurationKeyNotFoundException(String message) {
		super(message);
	}

	public ConfigurationKeyNotFoundException(Throwable cause) {
		super(cause);
	}

	public ConfigurationKeyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationKeyNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


}
