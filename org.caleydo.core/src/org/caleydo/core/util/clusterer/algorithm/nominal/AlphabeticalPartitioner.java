/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.nominal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

public class AlphabeticalPartitioner extends AClusterer {

	public AlphabeticalPartitioner(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);

	}

	@Override
	protected PerspectiveInitializationData cluster() {
		Map<String, List<Integer>> letterBins = new HashMap<>(40);
		final List<String> letters = new ArrayList<>(27);
		for (char letter = 'a'; letter < 'z'; letter++) {
			String stringLetter = Character.toString(letter);

			letters.add(stringLetter);
			letterBins.put(stringLetter, new ArrayList<Integer>());
		}
		String unknown = "UNKNOWN";
		letterBins.put(unknown, new ArrayList<Integer>());

		int firstDim = config.getSourceDimensionPerspective().getVirtualArray().get(0);
		for (Integer recordID : config.getSourceRecordPerspective().getVirtualArray()) {
			String value = table.getRawAsString(firstDim, recordID);
			String firstLetter = value.substring(0, 1);
			firstLetter = firstLetter.toLowerCase();
			if (letterBins.containsKey(firstLetter))
				letterBins.get(firstLetter).add(recordID);
			else {
				letterBins.get(unknown).add(recordID);
			}
		}

		List<Integer> indices = new ArrayList<Integer>();
		List<Integer> clusterSizes = new ArrayList<Integer>();
		List<Integer> sampleElements = new ArrayList<Integer>();

		for (String letter : letters) {
			List<Integer> recordIDs = letterBins.get(letter);
			if (recordIDs.isEmpty())
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
