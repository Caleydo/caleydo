/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.math;


/**
 * @author Alexander Lex
 *
 */
public class MathHelper {
	public static double log(double value, double base) {
		if (base == 10)
			return Math.log10(value);
		return Math.log(value) / Math.log(base);
	}

	public static double log2(double value) {
		return Math.log(value) / Math.log(2);
	}
}
