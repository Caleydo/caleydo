/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color;

import java.util.List;
import java.util.SortedSet;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.mapping.ColorMapper;

/**
 * abstract version of a color palette
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IColorPalette extends ILabeled {
	/**
	 * return a sorted set of available sizes
	 * 
	 * @return
	 */
	SortedSet<Integer> getSizes();

	/**
	 * returns a specific palette
	 * 
	 * @param size
	 * @return
	 */
	List<Color> get(int size);

	/**
	 * short cut for {@link #getSizes()} and {@link List#get(int)}
	 * 
	 * @param size
	 * @param index
	 * @return
	 */
	Color get(int size, int index);

	/**
	 * converts a given palette to a {@link ColorManager}
	 * 
	 * @param size
	 * @return
	 */
	ColorMapper asColorMapper(int size);

	/**
	 * @return
	 */
	EColorSchemeType getType();
}
