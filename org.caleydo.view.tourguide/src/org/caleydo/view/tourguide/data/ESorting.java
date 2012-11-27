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
package org.caleydo.view.tourguide.data;


/**
 * sort type indicator
 *
 * @author Samuel Gratzl
 *
 */
public enum ESorting {
	NONE, DESC, ASC;

	public ESorting next() {
		ESorting[] values = ESorting.values();
		return values[(this.ordinal() + 1) % (values.length)];
	}

	public ESorting previous() {
		ESorting[] values = ESorting.values();
		return values[(this.ordinal() - 1) % (values.length)];
	}

	/**
	 * updates the compare result according to the current state
	 * 
	 * @param compare
	 * @return
	 */
	public int apply(int compare) {
		switch (this) {
		case ASC:
			return compare;
		case DESC:
			return -compare;
		case NONE:
			return 0;
		default:
			throw new IllegalStateException("unknown sorting");
		}
	}

	public String getFileName() {
		switch (this) {
		case ASC:
			return "resources/icons/view/tourguide/sort_ascending.png";
		case DESC:
			return "resources/icons/view/tourguide/sort_descending.png";
		case NONE:
			return "resources/icons/view/tourguide/sort_none.png";
		default:
			throw new IllegalStateException("unknown sorting");
		}
	}
}
