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
/**
 *
 */
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;

/**
 * Util stuff for Virtual Arrays
 *
 * @author Alexander Lex
 */
public class VAUtils {

	/**
	 * Calculates the intersection of the virtual arrays specified and returns a new list with the same order
	 * of virtual array types, where each va has only the elements all others share
	 *
	 * @param sourceVAs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<VirtualArray> createIntersectingVAs(List<VirtualArray> sourceVAs) {
		if (sourceVAs == null)
			return null;
		if (sourceVAs.size() == 0)
			return new ArrayList<VirtualArray>();

		List<VirtualArray> targetVAs = new ArrayList<VirtualArray>();

		for (VirtualArray sourceVA : sourceVAs) {
			targetVAs.add((VirtualArray) sourceVA.clone());
		}

		for (int vaCount = 0; vaCount < sourceVAs.size(); vaCount++) {
			VirtualArray firstSourceVA = sourceVAs.get(vaCount);

			IDMappingManager mappingManager =
				IDMappingManagerRegistry.get().getIDMappingManager(firstSourceVA.getIdType().getIDCategory());

			VirtualArrayDelta delta = new VirtualArrayDelta();

			for (Integer id : firstSourceVA) {
				for (VirtualArray testVA : sourceVAs) {
					if (testVA == firstSourceVA)
						continue;
					if (!testVA.contains((Integer) mappingManager.getID(firstSourceVA.getIdType(),
						testVA.getIdType(), id))) {
						delta.add(VADeltaItem.removeElement(id));
						continue;
					}
				}
			}
			targetVAs.get(vaCount).setDelta(delta);
		}

		return targetVAs;

	}
}
