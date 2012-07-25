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
package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;

public class DataTableComparer {

	public static DataTableRelations compareSets(DataTable setLeft, String leftRecordPerspectiveID,
		DataTable setRight, String rightRecordPerspectiveID) {
		DataTableRelations setRelations = new DataTableRelations(setLeft, setRight);

		RecordVirtualArray recordVALeft =
			setLeft.getRecordPerspective(leftRecordPerspectiveID).getVirtualArray();
		RecordVirtualArray recordVARight =
			setRight.getRecordPerspective(rightRecordPerspectiveID).getVirtualArray();

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
