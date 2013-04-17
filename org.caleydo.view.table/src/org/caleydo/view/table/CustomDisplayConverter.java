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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * a custom {@link DisplayConverter} to manipulate the representation of numbers, such that changing the format is
 * possible
 * 
 * @author Samuel Gratzl
 *
 */
public class CustomDisplayConverter extends DisplayConverter {
	private final DecimalFormat formatter = new DecimalFormat("0.000",
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	public CustomDisplayConverter() {
		formatter.setMinimumFractionDigits(3);
	}

	public void changeMinFractionDigits(int delta) {
		int value = Math.max(0, formatter.getMinimumFractionDigits() + delta);
		formatter.setMinimumFractionDigits(value);
		formatter.setMaximumFractionDigits(value);
	}

	@Override
	public Object canonicalToDisplayValue(Object sourceValue) {

		if (sourceValue == null)
			return "";
		if (sourceValue instanceof Float) {
			return formatter.format(sourceValue);
		}
		return sourceValue.toString();
	}

	@Override
	public Object displayToCanonicalValue(Object destinationValue) {
		if (destinationValue == null || destinationValue.toString().length() == 0) {
			return null;
		} else {
			return destinationValue.toString();
		}
	}
}