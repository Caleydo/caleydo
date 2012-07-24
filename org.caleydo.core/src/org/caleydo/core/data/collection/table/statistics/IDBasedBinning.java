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
package org.caleydo.core.data.collection.table.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;

public class IDBasedBinning {
	IDMappingManager mappingManager;

	HashMap<String, ArrayList<Integer>> bin = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> truncatedBin = new HashMap<String, ArrayList<Integer>>();
	int numberOfBins = 10;

	public IDBasedBinning(IDMappingManager mappingManager) {
		mappingManager = this.mappingManager;
	}

	public void setNumberOfBins(int numberOfBins) {
		this.numberOfBins = numberOfBins;
	}

	public HashMap<String, ArrayList<Integer>> getBinning(IDType sourceType, IDType targetType,
		RecordVirtualArray contenVA) {
		if (bin.isEmpty()) {
			ArrayList<Integer> nonMappingIDs = new ArrayList<Integer>();
			for (Integer recordID : contenVA) {

				Set<String> refSeqIDs =
					mappingManager.getIDAsSet(sourceType, IDType.getIDType("REFSEQ_MRNA"), recordID);

				for (String refSeq : refSeqIDs) {
					Set<String> mappedIDs =
						mappingManager.getIDAsSet(IDType.getIDType("REFSEQ_MRNA"), targetType, refSeq);
					if (mappedIDs == null) {
						nonMappingIDs.add(recordID);
						// bin.put("Other", nonMappingIDs);
						continue;
					}
					for (String id : mappedIDs) {
						if (!bin.containsKey(id)) {
							ArrayList<Integer> ids = new ArrayList<Integer>();
							ids.add(recordID);
							bin.put(id, ids);
						}
						else {
							ArrayList<Integer> ids = bin.get(id);
							ids.add(recordID);
							bin.put(id, ids);
						}
					}
				}
			}
		}

		ArrayList<Pair<Integer, String>> sizeToKey = new ArrayList<Pair<Integer, String>>();
		for (Entry<String, ArrayList<Integer>> entry : bin.entrySet()) {
			sizeToKey.add(new Pair<Integer, String>(entry.getValue().size(), entry.getKey()));
		}
		Collections.sort(sizeToKey, Collections.reverseOrder());

		for (int count = 0; count < (numberOfBins > sizeToKey.size() ? sizeToKey.size() : numberOfBins); count++) {
			String key = sizeToKey.get(count).getSecond();
			truncatedBin.put(key, bin.get(key));
		}

		return truncatedBin;
	}
}
