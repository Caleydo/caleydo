package org.caleydo.core.util.clusterer.nominal;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.util.clusterer.AClusterer;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.TempResult;

public class AlphabeticalPartitioner
	extends AClusterer {

	@Override
	public TempResult getSortedVA(DataTable set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {
		ContentVirtualArray contentVA = set.getContentData(DataTable.RECORD).getContentVA();
		NominalDimension<String> storage =
			(NominalDimension<String>) set.get(set.getStorageData(DataTable.DIMENSION).getStorageVA().get(0));

		HashMap<String, ArrayList<Integer>> letterBins = new HashMap<String, ArrayList<Integer>>(40);

		ArrayList<String> letters = new ArrayList<String>(27);

		for (char letter = 'a'; letter < 'z'; letter++) {
			String stringLetter = Character.toString(letter);

			letters.add(stringLetter);
			letterBins.put(stringLetter, new ArrayList<Integer>());
		}
		String unknown = "UNKNOWN";
		letterBins.put(unknown, new ArrayList<Integer>());

		for (Integer contentID : contentVA) {
			String value = storage.getRaw(contentID);
			String firstLetter = value.substring(0, 1);
			firstLetter = firstLetter.toLowerCase();
			if (letterBins.containsKey(firstLetter))
				letterBins.get(firstLetter).add(contentID);
			else {
				letterBins.get(unknown).add(contentID);
			}
		}

		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();
		for (String letter : letters) {
			ArrayList<Integer> contentIDs = letterBins.get(letter);
			if (contentIDs.size() == 0)
				continue;

			indices.addAll(contentIDs);
			clusterSizes.add(contentIDs.size());
			// set the first to be the sample element, not really a nice solution
			sampleElements.add(indices.get(0));
		}
		indices.addAll(letterBins.get(unknown));

		TempResult result = new TempResult();
		result.setIndices(indices);
		result.setClusterSizes(clusterSizes);
		result.setSampleElements(sampleElements);
		return result;

	}
}
