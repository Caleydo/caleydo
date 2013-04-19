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
package org.caleydo.view.table;

/**
 * different modi, which values should be shown
 *
 * TODO: add the log and log10 mode, but they aren't accessible at the moment
 *
 * @author Samuel Gratzl
 * 
 */
public enum EDataRepresentation {
	RAW, NORMALIZED;

	/**
	 * @return
	 */
	public String getLabel() {
		switch (this) {
		case RAW:
			return "Raw Values";
		case NORMALIZED:
			return "Normalized Values";
		}
		throw new IllegalStateException();
	}

	/**
	 * @return
	 */
	public String getTooltip() {
		switch (this) {
		case RAW:
			return "Show the raw values of the table";
		case NORMALIZED:
			return "Show the normalized values of the table";
		}
		throw new IllegalStateException();
	}
}
