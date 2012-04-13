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
package org.caleydo.core.parser.parameter;

import java.util.Hashtable;

/**
 * @author Michael Kalkusch
 */
public final class ParameterKeyValueDataAndDefault<T> {

	private Hashtable<String, T> hashKey2Generic;

	private Hashtable<String, T> hashKey2DefaultValue;

	public ParameterKeyValueDataAndDefault() {

		hashKey2Generic = new Hashtable<String, T>();

		hashKey2DefaultValue = new Hashtable<String, T>();
	}

	public T getValue(final String key) {

		return hashKey2Generic.get(key);
	}

	public void setValueAndDefaultValue(final String key, final T value, final T defaultValue) {

		hashKey2Generic.put(key, value);
		hashKey2DefaultValue.put(key, defaultValue);
	}

	public void setDefaultValue(final String key, final T value) {

		hashKey2DefaultValue.put(key, value);
		hashKey2Generic.put(key, value);
	}
}
