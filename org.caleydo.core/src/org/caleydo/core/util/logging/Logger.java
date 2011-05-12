package org.caleydo.core.util.logging;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Convenience interface for the RCP logger
 * 
 * @author Alexander Lex
 */
public class Logger {

	private static volatile ILog instance;

	public static void log(Status status) {
		if (instance == null) {
			synchronized (Logger.class) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if (instance == null) {
					instance = Platform.getLog(Platform.getBundle("org.caleydo.core"));
					instance.addLogListener(new LogListener());
				}
			}
		}
		instance.log(status);
	}

}
