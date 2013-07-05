/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
