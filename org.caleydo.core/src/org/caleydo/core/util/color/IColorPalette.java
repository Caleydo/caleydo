/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
