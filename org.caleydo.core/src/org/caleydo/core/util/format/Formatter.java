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
