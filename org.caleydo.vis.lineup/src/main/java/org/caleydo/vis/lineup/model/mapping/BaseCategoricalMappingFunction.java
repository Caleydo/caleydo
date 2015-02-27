/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.vis.lineup.data.ADoubleFunction;

/**
 * @author Samuel Gratzl
 *
 */
public class BaseCategoricalMappingFunction<T> extends ADoubleFunction<T> implements ICategoricalMappingFunction<T>,
		Cloneable {
	private final Map<T, Double> mapping = new LinkedHashMap<>();
	private double missingValue = Double.NaN;

	public BaseCategoricalMappingFunction(Set<T> items) {
		int i = 1;
		double f = 1.f / items.size();
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
	public double applyPrimitive(T in) {
		if (in == null)
			return missingValue;
		Double r = mapping.get(in);
		return r == null ? missingValue : r.doubleValue();
	}

	public void put(T in, double value) {
		mapping.put(in, value);
	}

	public void remove(T in) {
		mapping.remove(in);
	}

	@Override
	public void reset() {
		int i = 1;
		double f = 1.f / mapping.size();
		for (Map.Entry<T, Double> entry : mapping.entrySet())
			entry.setValue((i++) * f);
	}
}
