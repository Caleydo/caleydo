/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

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
					try {
						instance = Platform.getLog(Platform.getBundle("org.caleydo.core"));
					} catch (NullPointerException e) {
						System.err.println("can't create log maybe running in sandbox");
						e.printStackTrace();
						instance = new DummyLog();
					}
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

	private static class DummyLog implements ILog {
		private final List<ILogListener> listeners = new ArrayList<>();
		private final DateFormat dateformat = new SimpleDateFormat("hh:MM:ss,SSS");

		@Override
		public void addLogListener(ILogListener listener) {
			this.listeners.add(listener);
		}

		@Override
		public Bundle getBundle() {
			return null;
		}

		@Override
		public synchronized void log(IStatus status) {
			for (ILogListener l : listeners) {
				l.logging(status, status.getPlugin());
			}
			StringBuilder b = new StringBuilder();
			b.append(dateformat.format(new Date()));
			int severity = status.getSeverity();
			switch(severity) {
			case IStatus.OK:
				b.append(" FINE  ");
				break;
			case IStatus.ERROR:
				b.append(" ERROR ");
				break;
			case IStatus.WARNING:
				b.append(" WARN  ");
				break;
			case IStatus.INFO:
				b.append(" INFO  ");
				break;
			case IStatus.CANCEL:
				b.append(" FATAL ");
				break;
			default:
				b.append(String.format("%4d ", severity));
				break;
			}
			b.append('[').append(Thread.currentThread().getName()).append("] ");
			b.append(status.getPlugin()).append(" - ").append(status.getMessage());
			if (severity >= IStatus.ERROR)
				System.err.println(b);
			else
				System.out.println(b);
			if (status.getException() != null)
				status.getException().printStackTrace();
		}

		@Override
		public void removeLogListener(ILogListener listener) {
			listeners.remove(listener);
		}

	}
}
