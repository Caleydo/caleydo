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
package org.caleydo.vis.rank.model.mapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.vis.rank.data.AFloatFunction;

/**
 * @author Samuel Gratzl
 *
 */
public class BaseCategoricalMappingFunction<T> extends AFloatFunction<T> implements ICategoricalMappingFunction<T>,
		Cloneable {
	private final Map<T, Float> mapping = new LinkedHashMap<>();
	private float missingValue = 0.f;

	public BaseCategoricalMappingFunction(Set<T> items) {
		int i = 1;
		float f = 1.f / items.size();
		for (T key : items)
			mapping.put(key, (i++) * f);
		System.out.println(mapping);
	}

	public BaseCategoricalMappingFunction(BaseCategoricalMappingFunction<T> copy) {
		this.mapping.putAll(copy.mapping);
	}

	@Override
	public float applyPrimitive(T in) {
		if (in == null)
			return missingValue;
		Float r = mapping.get(in);
		return r == null ? missingValue : r.floatValue();
	}

	@Override
	public BaseCategoricalMappingFunction<T> clone() {
		return new BaseCategoricalMappingFunction<T>(this);
	}

	@Override
	public void reset() {
		int i = 1;
		float f = 1.f/mapping.size();
		for (Map.Entry<T, Float> entry : mapping.entrySet())
			entry.setValue((i++) * f);
	}
}
