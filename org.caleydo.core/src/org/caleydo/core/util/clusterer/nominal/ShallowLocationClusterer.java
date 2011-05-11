package org.caleydo.core.util.clusterer.nominal;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.TempResult;

public class ShallowLocationClusterer
	extends ALocationClusterer {

	private final static int MAX_NUM_CLUSTERS = 24;

	@Override
	public TempResult getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();
		NumericalStorage keyStorage = null;

		for (Integer storageIndex : set.getDataDomain().getStorageVA(Set.STORAGE)) {
			IStorage storage =  set.getDataDomain().getSet().get(storageIndex);
			if (storage.getLabel().equals("RAUMSCHLUESSEL")) {
				keyStorage = (NumericalStorage) storage;
				break;
			}
		}

		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(MAX_NUM_CLUSTERS);
		for (int i = 0; i < MAX_NUM_CLUSTERS; i++) {
			clusters.add(new ArrayList<Integer>());
		}

		for (Integer contentID : contentVA) {
			int locationKey = (int) keyStorage.getFloat(EDataRepresentation.RAW, contentID);
			int clusterIndex = getClusterIndex(locationKey);
			clusters.get(clusterIndex).add(contentID);
		}

		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Integer> clusterSizes = new ArrayList<Integer>();
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();
		for (ArrayList<Integer> cluster : clusters) {
			if (cluster.size() <= 0)
				continue;

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
