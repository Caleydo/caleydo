package org.caleydo.core.util.exception;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.Status;

/**
 * Catches exceptions and logs them, hides them when in release mode
 * 
 * @author Alexander Lex
 */
public class ExceptionHandler {
	/**
	 * Determine whether to hide exceptions and log them, or whether to throw them
	 */
	public static final boolean HIDE_EXCEPTIONS = false;

	private static ExceptionHandler singletonInstance = null;

	private ExceptionHandler() {

	}

	/**
	 * Get singleton instance of exception handler
	 * 
	 * @return
	 */
	public static ExceptionHandler get() {
		if (singletonInstance == null) {
			singletonInstance = new ExceptionHandler();
		}

		return singletonInstance;
	}

	/**
	 * Handle runtime exceptions. Depending on {@link ExceptionHandler#HIDE_EXCEPTIONS} exceptions are either
	 * rethrown or hidden and logged
	 * 
	 * @param exception
	 */
	public void handleException(RuntimeException exception) {
		if (HIDE_EXCEPTIONS) {
			GeneralManager.get().getLogger().log(new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
				"Caught Exception: " + exception.getMessage(), exception));
			// Log here
		}
		else
			throw exception;
	}

}
