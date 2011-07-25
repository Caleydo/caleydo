package org.caleydo.core.data.collection.table;

import java.util.HashMap;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;

public class DataTableComparer {

	public static DataTableRelations compareSets(DataTable setLeft, DataTable setRight) {
		DataTableRelations setRelations = new DataTableRelations(setLeft, setRight);

		ContentVirtualArray contentVALeft = setLeft.getContentData(DataTable.RECORD).getContentVA();
		ContentVirtualArray contentVARight = setRight.getContentData(DataTable.RECORD).getContentVA();

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
