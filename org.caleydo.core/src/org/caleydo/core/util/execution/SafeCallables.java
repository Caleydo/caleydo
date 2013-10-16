/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.execution;

/**
 * @author Samuel Gratzl
 *
 */
public class SafeCallables {
	public static <T> SafeCallable<T> newInstance(final Class<T> clazz) {
		// try {
		// assert clazz.getConstructor().isAccessible();
		// } catch (NoSuchMethodException | SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return new SafeCallable<T>() {
			@Override
			public T call() {
				try {
					return clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}
}
