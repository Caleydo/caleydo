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
package org.caleydo.core.data.collection.column.container;

import org.caleydo.core.data.virtualarray.VAIterator;
import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * Abstract container iterator for all ICContainers. Supports virtual arrays.
 * 
 * @author Alexander Lex
 */
public class AContainerIterator
	implements IContainerIterator {
	protected VirtualArray<?, ?, ?> virtualArray = null;
	protected VAIterator vaIterator = null;
	protected int iIndex = 0;
	protected int iSize = 0;

	@Override
	public boolean hasNext() {
		if (virtualArray == null) {
			if (iIndex < iSize - 1)
				return true;
			else
				return false;
		}
		else
			return vaIterator.hasNext();
	}

	@Override
	public void remove() {
		if (virtualArray == null)
			throw new IllegalStateException(
				"Remove is only defined if a virtual array is enabled, which is currently not the case");
		else {
			vaIterator.remove();
		}
	}
}
