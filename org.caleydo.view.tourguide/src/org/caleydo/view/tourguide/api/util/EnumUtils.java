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
package org.caleydo.view.tourguide.api.util;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * collection of utility function for managing enums
 * 
 * @author Samuel Gratzl
 * 
 */
public class EnumUtils {
	private EnumUtils() {

	}

	public static <E extends Enum<E>> String[] getNames(Class<E> clazz) {
		E[] values = clazz.getEnumConstants();
		String[] n = new String[values.length];
		for (int i = 0; i < values.length; ++i)
			n[i] = values[i].name();
		return n;
	}

	public static <E extends Enum<E> & ILabelProvider> String[] getLabels(Class<E> clazz) {
		E[] values = clazz.getEnumConstants();
		String[] n = new String[values.length];
		for (int i = 0; i < values.length; ++i)
			n[i] = values[i].getLabel();
		return n;
	}

	public static <E extends Enum<E> & ILabelProvider> E valueOfByLabels(Class<E> clazz, String label) {
		for (E value : clazz.getEnumConstants())
			if (value.getLabel().equals(label))
				return value;
		return null;
	}
}
