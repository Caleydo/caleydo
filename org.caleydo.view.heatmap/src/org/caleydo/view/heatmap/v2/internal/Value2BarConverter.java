/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.function.IDoubleFunction;

import com.google.common.base.Function;

/**
 * converts a given double value to a bar, including left, right, and center alignment
 *
 * @author Samuel Gratzl
 *
 */
public class Value2BarConverter implements Function<Double, Vec2f> {
	private final float center;

	private final IDoubleFunction normalize;

	public Value2BarConverter(boolean left, IDoubleFunction normalize) {
		this(left ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY, normalize);
	}

	/**
	 * @param center
	 * @param normalize
	 */
	public Value2BarConverter(double center, IDoubleFunction normalize) {
		this.center = (float) normalize.apply(center);
		this.normalize = normalize;
	}

	@Override
	public Vec2f apply(Double input) {
		if (input == null)
			return new Vec2f(0,0);
		float n = (float)normalize.apply(input.doubleValue());
		if (center == Float.NEGATIVE_INFINITY)
			return new Vec2f(0,n);
		else if (center == Float.POSITIVE_INFINITY)
			return new Vec2f(1 - n, n);
		else {
			float l = n - center;
			return new Vec2f(center - l, n);
		}
	}

}
