/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.format;

import java.text.DecimalFormat;

/**
 * A utility class for formating diverse things such as numbers and strings.
 * 
 * @author Alexander Lex
 */
public class Formatter {

	private static final DecimalFormat E_NOTATION = new DecimalFormat("0.#E0");
	private static final DecimalFormat NO_COMMA = new DecimalFormat("#####");
	private static final DecimalFormat ONE_COMMA = new DecimalFormat("#####.#");
	private static final DecimalFormat TWO_COMMAS = new DecimalFormat("#####.##");

	/**
	 * Formats a provided number to have two digits after the comma if it is smaller than 10, one digit if
	 * smaller than 100, no digits when smaller than 10000 and E notation for larger numbers. Returns a
	 * formatted string.
	 * 
	 * @param number
	 *            the number to format
	 * @return the formatted string
	 */
	public static String formatNumber(double number) {

		DecimalFormat decimalFormat;
		if (Math.abs(number) > 10000)
			decimalFormat = E_NOTATION;
		else if (Math.abs(number) > 100)
			decimalFormat = NO_COMMA;
		else if (Math.abs(number) > 10)
			decimalFormat = ONE_COMMA;
		else
			decimalFormat = TWO_COMMAS;

		return decimalFormat.format(number);
	}

}
