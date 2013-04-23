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
package org.caleydo.core.util.clusterer.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

import com.jogamp.common.util.IntIntHashMap;


/**
 * Abstract base class for clusters that
 *
 * @author Alexander Lex
 */
public abstract class ALinearClusterer extends AClusterer {

	protected final int nrSamples;

	public ALinearClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		super(config, progressMultiplier, progressOffset);
		nrSamples = va.size();
	}

	/**
	 * @param assignments
	 *            index to alExample value
	 * @param alExamples
	 * @return
	 */
	protected PerspectiveInitializationData postProcess(int[] assignments, List<Integer> alExamples) {
		final int numClusters = alExamples.size();
		// Sort cluster depending on their color values
		IntIntHashMap order = sortClusters(alExamples);
		List<Integer> sampleElements = createSampleElements(alExamples, order);
		AssignmentIndex[] sort = new AssignmentIndex[nrSamples];
		for (int i = 0; i < nrSamples; ++i) {
			sort[i] = new AssignmentIndex(va.get(i), order.get(assignments[i]));
		}
		Arrays.sort(sort);

		int actCluster = 0;
		int actCount = 0;

		List<Integer> indices = new ArrayList<Integer>(nrSamples);
		List<Integer> clusterSizes = new ArrayList<Integer>(numClusters);

		for (int i = 0; i < sort.length; ++i) {
			AssignmentIndex a = sort[i];
			indices.add(a.id);
			if (a.cluster == actCluster) {
				actCount++;
			} else {
				clusterSizes.add(actCount);
				actCluster = a.cluster;
				actCount = 1;
			}
		}
		clusterSizes.add(actCount);

		progressScaled(50);

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();
		tempResult.setData(indices, clusterSizes, sampleElements);
		return tempResult;
	}

	private List<Integer> createSampleElements(List<Integer> alExamples, IntIntHashMap order) {
		List<Integer> sampleElements = new ArrayList<Integer>();
		for (int i = 0; i < alExamples.size(); ++i)
			sampleElements.add(null);
		for (Integer alSample : alExamples) {
			sampleElements.set(order.get(alSample), alSample);
		}
		return sampleElements;
	}

	private static class AssignmentIndex implements Comparable<AssignmentIndex> {
		private final int cluster;
		private final int id;

		public AssignmentIndex(int id, int cluster) {
			this.id = id;
			this.cluster = cluster;
		}

		@Override
		public int compareTo(AssignmentIndex o) {
			int r = cluster - o.cluster;
			if (r == 0)
				r = id - o.id;
			return r;
		}

		@Override
		public String toString() {
			return "c" + cluster + ":" + id;
		}

	}
}