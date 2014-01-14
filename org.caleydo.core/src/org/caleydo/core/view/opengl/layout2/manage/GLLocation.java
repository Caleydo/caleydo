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

	public double getOffset2() {
		return offset + size;
	}

	public static GLLocation applyPrimitive(ILocator loc, Integer input) {
		return input == null ? UNKNOWN : loc.apply(input.intValue());
	}

	public static List<GLLocation> apply(ILocator loc, Iterable<Integer> dataIndizes) {
		return Lists.newArrayList(Iterables.transform(dataIndizes, loc));
	}

	/**
	 * @return
	 */
	public boolean isDefined() {
		return !Double.isNaN(offset) && !Double.isNaN(size);
	}

	public static ILocator shift(final ILocator wrappee, final double offsetShift) {
		return new ALocator() {

			@Override
			public GLLocation apply(int dataIndex) {
				GLLocation l = wrappee.apply(dataIndex);
				return l.shift(offsetShift);
			}
		};
	}

	public static ILocator scale(final ILocator wrappee, final double factor) {
		return new ALocator() {

			@Override
			public GLLocation apply(int dataIndex) {
				GLLocation l = wrappee.apply(dataIndex);
				return l.scale(factor);
			}
		};
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
}


