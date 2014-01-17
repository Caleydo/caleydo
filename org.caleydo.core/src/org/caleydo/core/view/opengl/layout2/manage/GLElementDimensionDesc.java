/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import static org.caleydo.core.util.function.DoublePredicates.and;
import static org.caleydo.core.util.function.DoublePredicates.ge;
import static org.caleydo.core.util.function.DoublePredicates.le;

import java.util.List;

import org.caleydo.core.util.function.DoublePredicates;
import org.caleydo.core.util.function.IDoublePredicate;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation.ILocator;

/**
 * description of a dimension of a visualization including size information and location service
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementDimensionDesc implements GLLocation.ILocator {
	private final GLLocation.ILocator locator;
	private final double offset;
	private final double factor;
	private IDoublePredicate validator;

	private GLElementDimensionDesc(ILocator locator, double offset, double factor, IDoublePredicate validator) {
		this.locator = locator;
		this.offset = offset;
		this.factor = factor;
		this.validator = validator;
	}

	public boolean isCountDependent() {
		return factor > 0;
	}

	public double countDependentSize(int count) {
		return factor * count;
	}

	public double fixSize() {
		return offset;
	}

	public boolean isValid(double size, int count) {
		if (isCountDependent()) {
			if (size < offset)
				return false;
			size -= offset; // no offset
			size /= count; // size per item
		}
		return validator.apply(size);
	}

	@Override
	public GLLocation apply(int dataIndex) {
		return locator.apply(dataIndex);
	}

	@Override
	public List<GLLocation> apply(Iterable<Integer> dataIndizes) {
		return locator.apply(dataIndizes);
	}

	@Override
	public GLLocation apply(Integer input) {
		return locator.apply(input);
	}

	public boolean hasLocation() {
		return locator != GLLocation.NO_LOCATOR;
	}

	public double size(int count) {
		return fixSize() + countDependentSize(count);
	}

	public static DescBuilder newCountDependent(double scale) {
		return newCountDependent(scale, 0);
	}

	public static DescBuilder newCountDependent(double scale, double offset) {
		return new DescBuilder(offset, scale);
	}

	public static DescBuilder newFix(double fix) {
		return new DescBuilder(fix, 0);
	}

	public static class DescBuilder {
		private GLLocation.ILocator locator = GLLocation.NO_LOCATOR;
		private IDoublePredicate validator = DoublePredicates.alwaysTrue;

		private final double offset;
		private final double factor;

		public DescBuilder(double offset, double factor) {
			this.offset = offset;
			this.factor = factor;
		}

		public DescBuilder validateUsing(IDoublePredicate validator) {
			this.validator = validator;
			return this;
		}

		public DescBuilder inRange(double min, double max) {
			return validateUsing(and(ge(min), le(max)));
		}

		public DescBuilder minimum(double min) {
			return validateUsing(ge(min));
		}

		public DescBuilder locateUsing(GLLocation.ILocator locator) {
			this.locator = locator;
			return this;
		}

		public GLElementDimensionDesc build() {
			return new GLElementDimensionDesc(locator, offset, factor, validator);
		}
	}
}