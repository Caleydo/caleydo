package org.caleydo.core.data.collection.table;

import java.util.HashMap;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;

public class DataTableComparer {

	public static DataTableRelations compareSets(DataTable setLeft, String leftRecordPerspectiveID,
		DataTable setRight, String rightRecordPerspectiveID) {
		DataTableRelations setRelations = new DataTableRelations(setLeft, setRight);

		RecordVirtualArray recordVALeft = setLeft.getRecordPerspective(leftRecordPerspectiveID).getVA();
		RecordVirtualArray recordVARight = setRight.getRecordPerspective(rightRecordPerspectiveID).getVA();

		/** hash ID to index for faster accessibility */
		HashMap<Integer, Integer> hashRightIndices = new HashMap<Integer, Integer>();

		int rightIndex = 0;
		for (Integer rightID : recordVARight) {
			hashRightIndices.put(rightID, rightIndex++);
		}

		int leftIndex = 0;
		for (Integer leftID : recordVALeft) {
			setRelations.hashLeftToRight.put(leftIndex, hashRightIndices.get(leftID));
			setRelations.hashRightToLeft.put(hashRightIndices.get(leftID), leftIndex);
			leftIndex++;
		}

		return setRelations;

	}
}
