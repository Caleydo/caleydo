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
package org.caleydo.view.tourguide.api.util.ui;

import java.util.Objects;

import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * @author Samuel Gratzl
 *
 */
public class CellEditorValidators {

	public final static ICellEditorValidator isFloat = new ICellEditorValidator() {

		@Override
		public String isValid(Object value) {
			if (value == null)
				return "You have to enter a number";
			try {
				Float.parseFloat(value.toString());
				return null;
			} catch (NumberFormatException e) {
				return "You have to enter a valid number";
			}
		}
	};
	public final static ICellEditorValidator isNotBlank = new ICellEditorValidator() {

		@Override
		public String isValid(Object value) {
			if (Objects.toString(value, "").trim().isEmpty())
				return "You have to enter a value";
			return null;
		}
	};
}

