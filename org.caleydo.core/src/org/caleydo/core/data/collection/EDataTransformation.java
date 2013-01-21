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
package org.caleydo.core.data.collection;


/**
 * Enum that is used to signal the external data representation of a set, namely
 * what the normalized data refers to. This influences the visualization of the
 * data. The raw data is not changed
 *
 * @author Alexander Lex
 */

public enum EDataTransformation {
	NONE("None"), LOG10("Log10"), LOG2("Log2");
	// FOLD_CHANGE;

	private String humanReadableRep;

	private EDataTransformation(String humanReadableRep) {
		this.humanReadableRep = humanReadableRep;
	}

	/**
	 * @return the humanReadableRep, see {@link #humanReadableRep}
	 */
	public String getHumanReadableRep() {
		return humanReadableRep;

	}
}
