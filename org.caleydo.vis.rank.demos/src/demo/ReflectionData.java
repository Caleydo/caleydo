/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package demo;

import java.lang.reflect.Field;

import org.caleydo.vis.rank.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class ReflectionData<T> implements Function<IRow, T> {
	private final Field field;
	private final Class<T> clazz;

	public ReflectionData(Field field, Class<T> clazz) {
		this.field = field;
		this.clazz = clazz;
		field.setAccessible(true);
	}

	@Override
	public T apply(IRow in) {
		try {
			Object r = field.get(in);
			if (clazz == String.class)
				return clazz.cast(r == null ? "" : r.toString());
			if (clazz.isInstance(r))
				return clazz.cast(r);
			return null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

