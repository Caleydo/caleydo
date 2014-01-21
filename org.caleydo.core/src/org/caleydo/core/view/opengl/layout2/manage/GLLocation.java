/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
	public static interface ILocator extends Function<Integer, GLLocation> {
		GLLocation apply(int dataIndex);

		List<GLLocation> apply(Iterable<Integer> dataIndizes);
	}

	public static abstract class ALocator implements ILocator {
		@Override
		public GLLocation apply(Integer input) {
			return applyPrimitive(this, input);
		}

		@Override
		public List<GLLocation> apply(Iterable<Integer> dataIndizes) {
			return Lists.newArrayList(Iterables.transform(dataIndizes, this));
		}
	}
	public static final GLLocation UNKNOWN = new GLLocation(Double.NaN, Double.NaN);
	public static final ILocator NO_LOCATOR = new ALocator() {

		@Override
		public GLLocation apply(int dataIndex) {
			return UNKNOWN;
		}
	};

	private final double offset;
	private final double size;

	public GLLocation(double offset, double size) {
		this.offset = offset;
		this.size = size;
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
		return new GLLocation(offset + offsetShift, size);
	}

	/**
	 * @param offsetShift
	 * @return
	 */
	protected GLLocation scale(double factor) {
		return new GLLocation(offset * factor, size * factor);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GLLocation [offset=");
		builder.append(offset);
		builder.append(", size=");
		builder.append(size);
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
		return true;
	}

	/**
	 * utility for applying the primitive integer integer version given the boxed one
	 *
	 * @param loc
	 * @param input
	 * @return
	 */
	public static GLLocation applyPrimitive(ILocator loc, Integer input) {
		return input == null ? UNKNOWN : loc.apply(input.intValue());
	}

	public static List<GLLocation> apply(ILocator loc, Iterable<Integer> dataIndizes) {
		return Lists.newArrayList(Iterables.transform(dataIndizes, loc));
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
			public GLLocation apply(int dataIndex) {
				GLLocation l = wrappee.apply(dataIndex);
				return l.shift(shift);
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
			public GLLocation apply(int dataIndex) {
				GLLocation l = wrappee.apply(dataIndex);
				return l.scale(factor);
			}
		};
	}
}


