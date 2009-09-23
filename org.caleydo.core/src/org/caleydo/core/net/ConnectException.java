package org.caleydo.core.net;

/**
 * Thrown when initiatian a client/server connection between 2 caleydo applications fails.
 * 
 * @author Werner Puff
 */
public class ConnectException
	extends Exception {

	public static final long serialVersionUID = 42L;

	public ConnectException() {
		super();
	}

	public ConnectException(String message) {
		super(message);
	}

	public ConnectException(Throwable cause) {
		super(cause);
	}

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}
}
