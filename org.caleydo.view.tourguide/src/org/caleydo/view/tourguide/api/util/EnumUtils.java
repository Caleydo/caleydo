/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.util;


/**
 * collection of utility function for managing enums
 * 
 * @author Samuel Gratzl
 * 
 */
public class EnumUtils {
	private EnumUtils() {

	}

	public static <E extends Enum<E>> String[] getNames(Class<E> clazz) {
		E[] values = clazz.getEnumConstants();
		String[] n = new String[values.length];
		for (int i = 0; i < values.length; ++i)
			n[i] = values[i].name();
		return n;
	}
}
