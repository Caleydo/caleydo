package org.caleydo.core.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class Logger {

	private static volatile ILog instance;

	/**
	 * @param severity
	 *            use {@link IStatus#OK}, {@link IStatus#WARNING} etc.
	 * @param caller
	 * @param message
	 */
	public static void log(int severity, String caller, String message) {
		if (instance == null) {
			synchronized (Logger.class) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if (instance == null)
					instance = Platform.getLog(Platform.getBundle("org.caleydo.rcp"));;
			}
		}
		instance.log(new Status(severity, caller, message));
	}

}
