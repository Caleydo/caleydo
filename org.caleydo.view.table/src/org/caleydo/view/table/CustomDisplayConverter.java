/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
