/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
