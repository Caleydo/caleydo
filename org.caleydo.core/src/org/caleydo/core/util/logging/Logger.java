/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.logging;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Convenience interface for the RCP logger
 *
 * @author Alexander Lex
 */
public class Logger {

	private static volatile ILog instance;

	private final String source;

	private Logger(String source) {
		this.source = source;
	}

	public static Logger create(Class<?> clazz) {
		return new Logger(clazz.getCanonicalName());
	}

	public static void log(Status status) {
		if (instance == null) {
			synchronized (Logger.class) {
				// this is needed if two threads are waiting at the monitor at
				// the
				// time when singleton was getting instantiated
				if (instance == null) {
					instance = Platform.getLog(Platform.getBundle("org.caleydo.core"));
					instance.addLogListener(new LogListener());

				}
			}
		}
		instance.log(status);
	}

	public void warn(String msg, Object... args) {
		this.log(IStatus.WARNING, msg, args);
	}

	public void warn(String msg, Throwable e) {
		this.log(IStatus.WARNING, e, msg);
	}

	public void error(String msg, Object... args) {
		this.log(IStatus.ERROR, msg, args);
	}

	public void error(String msg, Throwable e) {
		this.log(IStatus.ERROR, e, msg);
	}

	public void error(Throwable e, String msg, Object... args) {
		this.log(IStatus.ERROR, e, msg, args);
	}

	public void info(String msg, Object... args) {
		this.log(IStatus.INFO, msg, args);
	}

	public void debug(String msg, Object... args) {
		this.log(IStatus.OK, msg, args);
	}

	public void log(int level, String msg, Object... args) {
		log(new Status(level, this.source, String.format(msg, args)));
	}

	public void log(int level, Throwable e, String msg, Object... args) {
		log(new Status(level, this.source, String.format(msg, args), e));
	}
}
