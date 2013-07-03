/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	private float missingValue = Float.NaN;

	public BaseCategoricalMappingFunction(Set<T> items) {
		int i = 1;
		float f = 1.f / items.size();
		for (T key : items)
			mapping.put(key, (i++) * f);
	}

	public BaseCategoricalMappingFunction(BaseCategoricalMappingFunction<T> copy) {
		this.mapping.putAll(copy.mapping);
	}

	@Override
	public BaseCategoricalMappingFunction<T> clone() {
		return new BaseCategoricalMappingFunction<T>(this);
	}

	@Override
	public boolean isComplexMapping() {
		return false;
	}

	@Override
	public float applyPrimitive(T in) {
		if (in == null)
			return missingValue;
		Float r = mapping.get(in);
		return r == null ? missingValue : r.floatValue();
	}

	public void put(T in, float value) {
		mapping.put(in, value);
	}

	public void remove(T in) {
		mapping.remove(in);
	}

	@Override
	public void reset() {
		int i = 1;
		float f = 1.f/mapping.size();
		for (Map.Entry<T, Float> entry : mapping.entrySet())
			entry.setValue((i++) * f);
	}
}
