package org.caleydo.core.util.clusterer.nominal;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.util.clusterer.AClusterer;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.TempResult;

public class MetalDecisionTreeClusterer
	extends AClusterer {

	HashMap<String, NumericalStorage> metalStorages;
	HashMap<String, NumericalStorage> metalSignStorages;

	@Override
	public TempResult getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		initMetalStorages(set);

		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(6);
		for (int i = 0; i < 6; i++) {
			clusters.add(new ArrayList<Integer>());
		}

		NumericalStorage asStorage = metalStorages.get("AS");
		NumericalStorage vasStorage = metalSignStorages.get("VAS");

		NumericalStorage sbStorage = metalStorages.get("SB");
		NumericalStorage vsbStorage = metalSignStorages.get("VSB");

		for (Integer contentID : contentVA) {

			float as = asStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vas = (int) vasStorage.getFloat(EDataRepresentation.RAW, contentID);

			float sb = sbStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vsb = (int) vsbStorage.getFloat(EDataRepresentation.RAW, contentID);

			boolean allZero = true;

			for (String metalKey : metalStorages.keySet()) {
				NumericalStorage metalStorage = metalStorages.get(metalKey);
				NumericalStorage metalSignStorage = metalSignStorages.get("V" + metalKey);
				float metal = metalStorage.getFloat(EDataRepresentation.RAW, contentID);
				int metalSign = (int) metalSignStorage.getFloat(EDataRepresentation.RAW, contentID);

				if (metal > 0.0f || metalSign != 1) {
					allZero = false;
					break;
				}
			}

			if (allZero) {
				clusters.get(0).add(contentID);
			}

			if (((as > 0.025f) || (vas == 5)) && ((sb > 0.025f) || (vsb == 5))) {
				NumericalStorage biStorage = metalStorages.get("BI");
				NumericalStorage vbiStorage = metalSignStorages.get("VBI");
				float bi = biStorage.getFloat(EDataRepresentation.RAW, contentID);
				int vbi = (int) vbiStorage.getFloat(EDataRepresentation.RAW, contentID);

				if ((bi >= 0.02f) || (vbi == 5)) {
					clusters.get(2).add(contentID);
					continue;
				}
				if ((bi < 0.02f) || (vbi == 1)) {
					clusters.get(1).add(contentID);
					continue;
				}
			}

			if (((as <= 0.025f) || (vas == 1)) && ((sb <= 0.025f) || (vsb == 1))) {
				clusters.get(3).add(contentID);
				continue;
			}
			if (((as <= 0.025f) || (vas == 1)) && ((sb > 0.025f) || (vsb == 5))) {
				clusters.get(4).add(contentID);
				continue;
			}
			if (((as > 0.025f) || (vas == 5)) && ((sb <= 0.025f) || (vsb == 1))) {
				clusters.get(5).add(contentID);
				continue;
			}

			throw new IllegalStateException("Metal could not be classified");

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

	ArrayList<ArrayList<Integer>> getClusterOneSubClusters(ContentVirtualArray contentVA) {

		NumericalStorage biStorage = metalStorages.get("BI");
		NumericalStorage vbiStorage = metalSignStorages.get("VBI");

		NumericalStorage niStorage = metalStorages.get("NI");
		NumericalStorage vniStorage = metalSignStorages.get("VNI");

		NumericalStorage sbStorage = metalStorages.get("SB");
		NumericalStorage vsbStorage = metalSignStorages.get("VSB");

		NumericalStorage agStorage = metalStorages.get("AG");
		NumericalStorage vagStorage = metalSignStorages.get("VAG");

		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(11);
		for (int i = 0; i < 11; i++) {
			clusters.add(new ArrayList<Integer>());
		}

		for (Integer contentID : contentVA) {

			float bi = biStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vbi = (int) vbiStorage.getFloat(EDataRepresentation.RAW, contentID);

			float sb = sbStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vsb = (int) vsbStorage.getFloat(EDataRepresentation.RAW, contentID);

			float ni = niStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vni = (int) vniStorage.getFloat(EDataRepresentation.RAW, contentID);

			float ag = agStorage.getFloat(EDataRepresentation.RAW, contentID);
			int vag = (int) vagStorage.getFloat(EDataRepresentation.RAW, contentID);

			if ((ni >= 0.1f) || (vni == 5)) {
				// Iab
				if ((ag <= 0.1f) || (vag == 1)) {
					// Ia
					if ((bi <= 0.002f) || (vbi == 1)) {
						// FB1
						clusters.get(1).add(contentID);
						continue;
					}

					if ((bi > 0.002f) || (vbi == 5)) {
						// FB2
						clusters.get(2).add(contentID);
						continue;
					}
				}

				if (((ag > 0.1f) || (vag == 5)) && ((sb > 0.12f) || (vsb == 5))) {
					// Ib
					if ((ni >= 0.64f) || (vni == 5)) {
						// Ib1
						if ((bi <= 0.002f) || (vbi == 1)) {
							// A
							clusters.get(3).add(contentID);
							continue;
						}
						if ((bi > 0.002f) || (vbi == 5)) {
							// Ib1a
							if ((sb >= 0.8f) || (vsb == 5)) {
								//A1
								clusters.get(4).add(contentID);
								continue;
							}
							if((sb < 0.8f) || (vsb == 1)) {
								//A2
								clusters.get(5).add(contentID);
								continue;
							}
						}
					}
					
					if((ni < 0.64f) || (vni == 1)) {
						//B2
						clusters.get(6).add(contentID);
						continue;
					}
				}

			}
			if ((ni < 0.1f) || (vni == 1)) {
				// Ic
//TODO
			}

		}
		
		return clusters;

	}

	private void initMetalStorages(ISet set) {

		metalStorages = new HashMap<String, NumericalStorage>();
		metalStorages.put("CU", null);
		metalStorages.put("SN", null);
		metalStorages.put("PB", null);
		metalStorages.put("AS", null);
		metalStorages.put("SB", null);
		metalStorages.put("CU", null);
		metalStorages.put("AG", null);
		metalStorages.put("CU", null);
		metalStorages.put("NI", null);
		metalStorages.put("BI", null);
		metalStorages.put("CU", null);
		metalStorages.put("AU", null);
		metalStorages.put("CU", null);
		metalStorages.put("ZN", null);
		metalStorages.put("CO", null);
		metalStorages.put("FE", null);

		metalSignStorages = new HashMap<String, NumericalStorage>();
		metalSignStorages.put("VCU", null);
		metalSignStorages.put("VSN", null);
		metalSignStorages.put("VPB", null);
		metalSignStorages.put("VAS", null);
		metalSignStorages.put("VSB", null);
		metalSignStorages.put("VCU", null);
		metalSignStorages.put("VAG", null);
		metalSignStorages.put("VCU", null);
		metalSignStorages.put("VNI", null);
		metalSignStorages.put("VBI", null);
		metalSignStorages.put("VCU", null);
		metalSignStorages.put("VAU", null);
		metalSignStorages.put("VCU", null);
		metalSignStorages.put("VZN", null);
		metalSignStorages.put("VCO", null);
		metalSignStorages.put("VFE", null);

		for (Integer storageIndex : set.getDataDomain().getStorageVA(Set.STORAGE)) {
			IStorage storage = set.getDataDomain().getSet().get(storageIndex);
			for (String key : metalStorages.keySet()) {
				if (storage.getLabel().equals(key)) {
					metalStorages.put(key, (NumericalStorage) storage);
					break;
				}
			}
			for (String key : metalSignStorages.keySet()) {
				if (storage.getLabel().equals(key)) {
					metalSignStorages.put(key, (NumericalStorage) storage);
					break;
				}
			}
		}

	}

}
