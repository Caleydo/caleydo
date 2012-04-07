/**
 * 
 */
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

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
	public static <VAType extends VirtualArray<?, VADelta, ?>, VADelta extends VirtualArrayDelta<?>> List<VAType> createIntersectingVAs(
		List<VAType> sourceVAs) {
		if (sourceVAs == null)
			return null;
		if (sourceVAs.size() == 0)
			return new ArrayList<VAType>();

		List<VAType> targetVAs = new ArrayList<VAType>();

		for (VAType sourceVA : sourceVAs) {
			targetVAs.add((VAType) sourceVA.clone());
		}

		for (int vaCount = 0; vaCount < sourceVAs.size(); vaCount++) {
			VAType firstSourceVA = sourceVAs.get(vaCount);

			IDMappingManager mappingManager =
				IDMappingManagerRegistry.get().getIDMappingManager(firstSourceVA.getIdType().getIDCategory());

			VADelta delta = firstSourceVA.getConcreteVADeltaInstance();

			for (Integer id : firstSourceVA) {
				for (VAType testVA : sourceVAs) {
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
