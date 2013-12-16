/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import static org.caleydo.core.util.function.DoublePredicates.alwaysFalse;
import static org.caleydo.core.util.function.DoublePredicates.alwaysTrue;
import static org.caleydo.core.util.function.DoublePredicates.and;
import static org.caleydo.core.util.function.DoublePredicates.ge;
import static org.caleydo.core.util.function.DoublePredicates.le;
import static org.caleydo.core.view.opengl.layout2.manage.GLLocation.NO_LOCATOR;

import org.caleydo.core.util.function.IDoublePredicate;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation.ALocator;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation.ILocator;

import com.google.common.base.Objects;
/**
 * @author Samuel Gratzl
 *
 */
public class GLElementDimensionDesc extends ALocator {
	private final Value before;
	private final Value factor;
	private final Value after;
	private final ILocator locator;

	private GLElementDimensionDesc(Value before, Value factor, Value after, ILocator locator) {
		this.before = Objects.firstNonNull(before, ZERO);
		this.factor = Objects.firstNonNull(factor, ZERO);
		this.after = Objects.firstNonNull(after, ZERO);
		this.locator = Objects.firstNonNull(locator, NO_LOCATOR);
	}

	public boolean hasLocation() {
		return locator != NO_LOCATOR;
	}

	@Override
	public GLLocation apply(int dataIndex) {
		return locator.apply(dataIndex);
	}

	/**
	 * @return the before, see {@link #before}
	 */
	public Value getBefore() {
		return before;
	}

	/**
	 * @return the factor, see {@link #factor}
	 */
	public Value getFactor() {
		return factor;
	}

	/**
	 * @return the after, see {@link #after}
	 */
	public Value getAfter() {
		return after;
	}

	public boolean isSizeDependent() {
		return factor != ZERO;
	}

	public double getOffset() {
		return before.getValue() + after.getValue();
	}

	public static DescBuilder newBuilder() {
		return new DescBuilder();
	}

	public static class DescBuilder {
		private Value before;
		private Value factor;
		private Value after;
		private ILocator locator;

		private DescBuilder() {
		}

		public DescBuilder before(Value value) {
			this.before = value;
			return this;
		}

		public DescBuilder constant(double value) {
			return before(GLElementDimensionDesc.constant(value));
		}

		public DescBuilder fix(double value) {
			before(GLElementDimensionDesc.unbound(value));
			return this;
		}

		public DescBuilder factor(Value value) {
			this.factor = value;
			return this;
		}

		public DescBuilder linear(double value) {
			factor(GLElementDimensionDesc.unbound(value));
			return this;
		}

		public DescBuilder after(Value value) {
			this.after = value;
			return this;
		}

		public DescBuilder locateUsing(ILocator value) {
			this.locator = value;
			return this;
		}

		public GLElementDimensionDesc build() {
			return new GLElementDimensionDesc(before, factor, after, locator);
		}
	}

	public static final Value ZERO = new Value(0, alwaysFalse);

	public static final Value constant(double value) {
		return new Value(value, alwaysFalse);
	}

	public static final Value unbound(double value) {
		return new Value(value, alwaysTrue);
	}

	public static final Value inRange(double value, double min, double max) {
		final IDoublePredicate f = and(ge(min), le(max));
		return new Value(value, f);
	}

	public static final class Value implements IDoublePredicate {
		private final double value;
		private final IDoublePredicate isValid;

		public Value(double value, IDoublePredicate isValid) {
			this.value = value;
			this.isValid = isValid;
		}

		/**
		 * @return the value, see {@link #value}
		 */
		public double getValue() {
			return value;
		}

		@Override
		public boolean apply(double in) {
			return isValid.apply(in) && in > 0;
		}

		@Override
		public boolean apply(Double input) {
			return isValid.apply(input) && input.doubleValue() > 0;
		}
	}


}