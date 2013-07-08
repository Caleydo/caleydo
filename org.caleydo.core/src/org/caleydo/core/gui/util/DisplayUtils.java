/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.util;

import java.util.concurrent.Callable;

import org.caleydo.core.util.execution.SafeCallable;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public class DisplayUtils {
	private static class Result<T> {
		private T result;
		private Exception exception;
	}

	public static <T> T syncExec(Display display, final SafeCallable<T> callable) {
		final Result<T> result = new Result<T>();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				result.result = callable.call();
			}
		});
		return result.result;
	}

	public static <T> T syncExec(Display display, final Callable<T> callable) throws Exception {
		final Result<T> result = new Result<T>();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					result.result = callable.call();
				} catch (Exception e) {
					result.exception = e;
				}
			}
		});
		if (result.exception != null) {
			throw result.exception;
		}
		return result.result;
	}
}
