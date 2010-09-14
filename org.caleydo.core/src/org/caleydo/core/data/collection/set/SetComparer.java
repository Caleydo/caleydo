package org.caleydo.core.data.collection.set;

import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;

public class SetComparer {

	public static SetRelations compareSets(ISet setLeft, ISet setRight) {
		SetRelations setRelations = new SetRelations(setLeft, setRight);

		ContentVirtualArray contentVALeft = setLeft.getContentData(Set.CONTENT).getContentVA();
		ContentVirtualArray contentVARight = setRight.getContentData(Set.CONTENT).getContentVA();

		/** hash ID to index for faster accessibility */
		HashMap<Integer, Integer> hashRightIndices = new HashMap<Integer, Integer>();

		int rightIndex = 0;
		for (Integer rightID : contentVARight) {
			hashRightIndices.put(rightID, rightIndex++);
		}

		int leftIndex = 0;
		for (Integer leftID : contentVALeft) {
			setRelations.hashLeftToRight.put(leftIndex, hashRightIndices.get(leftID));
			setRelations.hashRightToLeft.put(hashRightIndices.get(leftID), leftIndex);
			leftIndex++;
		}

		return setRelations;

	}
}
