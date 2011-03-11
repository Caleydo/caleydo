package org.caleydo.core.data.virtualarray;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Calculates the similarities of two virtual arrays on a {@link Group} basis.
 * 
 * @author Alexander Lex
 * @param <VAType>
 */
public class VASimilarity<VAType extends VirtualArray<?, ?, ?>> {

	HashMap<Integer, VAType> vaMap = new HashMap<Integer, VAType>(4);

	public void addVA(Integer setID, VAType va) {

		if (vaMap.size() <= 2 && !vaMap.containsKey(setID)) {
			vaMap.put(setID, va);
		}
		else {
			if (vaMap.containsKey(setID))
				throw new IllegalStateException("VASimilarity has already two VAs set.");

			vaMap.put(setID, va);
		}

	}

	public VAType getVA(Integer setID) {
		return vaMap.get(setID);
	}

	public Set<Integer> getSetIDs() {
		return vaMap.keySet();
	}

	public void calculateSimilarities() {
		System.out.println("Calculating similarities");
		
		
		
		
	}
}
