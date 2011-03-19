package org.caleydo.core.util.clusterer.nominal;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.util.clusterer.AClusterer;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.TempResult;

public class AlphabeticalPartitioner
	extends AClusterer {

	@Override
	public TempResult getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {
		set.getBaseContentVA();
		return null;
	}

}
