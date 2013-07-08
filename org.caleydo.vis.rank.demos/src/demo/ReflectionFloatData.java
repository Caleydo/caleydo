/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package demo;

import java.lang.reflect.Field;

import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.model.IRow;

/**
 * @author Samuel Gratzl
 *
 */
public class ReflectionFloatData extends AFloatFunction<IRow> {
	private final Field field;

	public ReflectionFloatData(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	@Override
	public float applyPrimitive(IRow in) {
		try {
			Number v = (Number) field.get(in);
			return v.floatValue();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return Float.NaN;
	}
}

