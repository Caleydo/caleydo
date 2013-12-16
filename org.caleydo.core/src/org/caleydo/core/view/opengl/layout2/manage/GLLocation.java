/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class GLLocation {
	public static interface ILocator extends Function<Integer, GLLocation> {
		GLLocation apply(int dataIndex);
	}

	public static abstract class ALocator implements ILocator {
		@Override
		public GLLocation apply(Integer input) {
			return applyPrimitive(this, input);
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

	public static GLLocation applyPrimitive(ILocator loc, Integer input) {
		return input == null ? UNKNOWN : loc.apply(input.intValue());
	}
}


