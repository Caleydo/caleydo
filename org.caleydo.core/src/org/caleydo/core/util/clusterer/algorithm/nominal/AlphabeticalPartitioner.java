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
package org.caleydo.core.util.clusterer.algorithm.nominal;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

public class AlphabeticalPartitioner
	extends AClusterer{

	/**
	 * 
	 */
	public AlphabeticalPartitioner() {
	
	}
	
	@Override
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
		ClusterConfiguration clusterState, int iProgressBarOffsetValue, int iProgressBarMultiplier) {
		RecordVirtualArray recordVA = clusterState.getSourceRecordPerspective().getVirtualArray();
		// NominalDimension<String> dimension =
		// (NominalDimension<String>)
		// table.get(clusterState.getSourceDimensionPerspective().getVirtualArray().get(0));

		HashMap<String, ArrayList<Integer>> letterBins = new HashMap<String, ArrayList<Integer>>(40);

		ArrayList<String> letters = new ArrayList<String>(27);

		for (char letter = 'a'; letter < 'z'; letter++) {
			String stringLetter = Character.toString(letter);

			letters.add(stringLetter);
			letterBins.put(stringLetter, new ArrayList<Integer>());
		}
		String unknown = "UNKNOWN";
		letterBins.put(unknown, new ArrayList<Integer>());

		for (Integer recordID : recordVA) {
			String value =
				dataDomain.getTable().getRaw(
					clusterState.getSourceDimensionPerspective().getVirtualArray().get(0), recordID);
			String firstLetter = value.substring(0, 1);
			firstLetter = firstLetter.toLowerCase();
			if (letterBins.containsKey(firstLetter))
				letterBins.get(firstLetter).add(recordID);
			else {
				letterBins.get(unknown).add(recordID);
			}
		}

		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();
		for (String letter : letters) {
			ArrayList<Integer> recordIDs = letterBins.get(letter);
			if (recordIDs.size() == 0)
				continue;

			indices.addAll(recordIDs);
			clusterSizes.add(recordIDs.size());
			// set the first to be the sample element, not really a nice solution
			sampleElements.add(indices.get(0));
		}
		indices.addAll(letterBins.get(unknown));

		PerspectiveInitializationData result = new PerspectiveInitializationData();
		result.setData(indices, clusterSizes, sampleElements);
		return result;

	}
}
