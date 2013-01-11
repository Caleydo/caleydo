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
package org.caleydo.core.data.collection.column;

/**
 * Describes of what kind a container is, independent of its data type. Examples are raw data, normalized data
 * etc. Raw data can e.g. be primitive float, int, double or (numerical) objects, or even nominal data.
 *
 * @author Alexander Lex
 */
public class DataRepresentation {
	public static String RAW = "RAW";
	public static String LOG10 = "LOG10";
	public static String LOG2 = "LOG2";
	public static String NORMALIZED = "NORMALIZED";

	// HashMap<String, q registeredDataRepresentations
	//
	// FOLD_CHANGE_RAW,
	// FOLD_CHANGE_NORMALIZED,
	//
	// UNCERTAINTY_RAW,
	// UNCERTAINTY_NORMALIZED;
}