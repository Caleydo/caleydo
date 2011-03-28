package org.caleydo.core.util.clusterer.nominal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.TempResult;

public class DeepTypeClusterer
	extends ATypeClusterer {

	@Override
	public TempResult getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();
		NumericalStorage keyStorage = null;

		for (Integer storageIndex : set.getDataDomain().getStorageVA(Set.STORAGE)) {
			IStorage storage = set.getDataDomain().getSet().get(storageIndex);
			if (storage.getLabel().equals("OBJEKTTYP")) {
				keyStorage = (NumericalStorage) storage;
				break;
			}
		}

		HashMap<Integer, ArrayList<Integer>> clusters = new HashMap<Integer, ArrayList<Integer>>();

		for (Integer contentID : contentVA) {
			int locationKey = (int) keyStorage.getFloat(EDataRepresentation.RAW, contentID);
			int clusterIndex = getClusterIndex(locationKey);
			if (clusterIndex != 0)
				clusterIndex = locationKey;

			ArrayList<Integer> cluster = clusters.get(clusterIndex);
			if (cluster == null) {
				cluster = new ArrayList<Integer>();
			}
			cluster.add(contentID);
			clusters.put(clusterIndex, cluster);
		}

		List<Integer> clusterIndices = new ArrayList<Integer>(clusters.keySet());
		Collections.sort(clusterIndices);

		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();
		for (Integer clusterIndex : clusterIndices) {
			ArrayList<Integer> cluster = clusters.get(clusterIndex);

			indices.addAll(cluster);
			clusterSizes.add(cluster.size());
			// set the first to be the sample element, not really a nice solution
			sampleElements.add(indices.get(0));
		}

		TempResult result = new TempResult();
		result.setIndices(indices);
		result.setClusterSizes(clusterSizes);
		result.setSampleElements(sampleElements);
		return result;
	}

}
