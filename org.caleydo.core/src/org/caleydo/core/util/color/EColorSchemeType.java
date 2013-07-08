/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color;

import com.google.common.base.Predicate;

/**
 * Different types for color schemes.
 *
 * @author Christian Partl
 *
 */
public enum EColorSchemeType {
	SEQUENTIAL, DIVERGING, QUALITATIVE;

	/**
	 * @return
	 */
	public Predicate<IColorPalette> isOf() {
		return new Predicate<IColorPalette>() {
			@Override
			public boolean apply(IColorPalette in) {
				return in.getType() == EColorSchemeType.this;
			}
		};
	}
}
