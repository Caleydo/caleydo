/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.util.Collections;
import java.util.Set;

import org.caleydo.core.util.function.Function2;

/**
 * a pair of offset + size, used for locating elements
 *
 * @author Samuel Gratzl
 *
 */
public class GLLocation {
	/**
	 * service, which can be a {@link GLLocation} to a given data Index
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public static interface ILocator extends Function2<Integer, Boolean, GLLocation> {
		GLLocation apply(int dataIndex, boolean topLeft);

		Set<Integer> unapply(GLLocation location);
	}

	public static abstract class ALocator implements ILocator {
		@Override
		public GLLocation apply(Integer input, Boolean topLeft) {
			return applyPrimitive(this, input, topLeft);
		}
	}

	public static final GLLocation UNKNOWN = new GLLocation(Double.NaN, Double.NaN, 0);
	public static final Set<Integer> UNKNOWN_IDS = Collections.emptySet();
	public static final ILocator NO_LOCATOR = new ALocator() {

		@Override
		public GLLocation apply(int dataIndex, boolean topLeft) {
			return UNKNOWN;
		}

		@Override
		public Set<Integer> unapply(GLLocation location) {
			return Collections.emptySet();
		}
	};

	private final double offset;
	private final double size;
	private final double indentation;

	public GLLocation(double offset, double size) {
		this(offset, size, 0);
	}
	public GLLocation(double offset, double size, double indentation) {
		this.offset = offset;
		this.size = size;
		this.indentation = indentation;
	}

	/**
	 * @return the indentation, see {@link #indentation}
	 */
	public double getIndentation() {
		return indentation;
	}

	/**
	 * @return the offset, see {@link #offset}
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * @return the size, see {@link #size}
	 */
	public double getSize() {
		return size;
	}

	/**
	 * @return {@link #offset}+{@link #size}
	 */
	public double getOffset2() {
		return offset + size;
	}

	/**
	 * @return whether the location has valid values or not
	 */
	public boolean isDefined() {
		return !Double.isNaN(offset) && !Double.isNaN(size);
	}



	/**
	 * @param offsetShift
	 * @return
	 */
	protected GLLocation shift(double offsetShift) {
		return new GLLocation(offset + offsetShift, size, indentation);
	}

	/**
	 * @param offsetShift
	 * @return
	 */
	protected GLLocation scale(double factor) {
		return new GLLocation(offset * factor, size * factor, indentation);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GLLocation [offset=");
		builder.append(offset);
		builder.append(", size=");
		builder.append(size);
		builder.append(", indentation=");
		builder.append(indentation);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(offset);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(size);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(indentation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GLLocation other = (GLLocation) obj;
		if (Double.doubleToLongBits(offset) != Double.doubleToLongBits(other.offset))
			return false;
		if (Double.doubleToLongBits(size) != Double.doubleToLongBits(other.size))
			return false;
		if (Double.doubleToLongBits(indentation) != Double.doubleToLongBits(other.indentation))
			return false;
		return true;
	}

	/**
	 * utility for applying the primitive integer integer version given the boxed one
	 *
	 * @param loc
	 * @param input
	 * @return
	 */
	public static GLLocation applyPrimitive(ILocator loc, Integer input, Boolean topLeft) {
		return input == null ? UNKNOWN : loc.apply(input.intValue(), topLeft.booleanValue());
	}

	/**
	 * wraps the given {@link ILocator} such that location will be shifted
	 *
	 * @param wrappee
	 * @param factor
	 * @return
	 */
	public static ILocator shift(final ILocator wrappee, final double shift) {
		return new ALocator() {

			@Override
			public GLLocation apply(int dataIndex, boolean topLeft) {
				GLLocation l = wrappee.apply(dataIndex, topLeft);
				return l.shift(shift);
			}

			@Override
			public Set<Integer> unapply(GLLocation location) {
				return wrappee.unapply(location.shift(-shift));
			}
		};
	}

	/**
	 * wraps the given {@link ILocator} such that location will be scaled by the given factor
	 *
	 * @param wrappee
	 * @param factor
	 * @return
	 */
	public static ILocator scale(final ILocator wrappee, final double factor) {
		return new ALocator() {

			@Override
			public GLLocation apply(int dataIndex, boolean topLeft) {
				GLLocation l = wrappee.apply(dataIndex, topLeft);
				return l.scale(factor);
			}

			@Override
			public Set<Integer> unapply(GLLocation location) {
				return wrappee.unapply(location.scale(1. / factor));
			}
		};
	}
}


