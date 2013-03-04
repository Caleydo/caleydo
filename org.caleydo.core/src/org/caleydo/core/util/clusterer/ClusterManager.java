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
package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterer;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterer;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Cluster manager handels {@link ClusterConfiguration} and calls corresponding
 * clusterer.
 *
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class ClusterManager {

	private final ATableBasedDataDomain dataDomain;

	/**
	 * Constructor
	 *
	 * @param set
	 */
	public ClusterManager(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * Runs clustering as specified in the provided clusterConfiguration.
	 *
	 * @param clusterConfiguration the configuration of the clustering to be
	 *            executed.
	 * @return the results of the clustering which can be used to initialize
	 *         {@link Perspective}s.
	 */
	public ClusterResult cluster(ClusterConfiguration clusterConfiguration) {
		Logger.log(new Status(IStatus.INFO, this.toString(),
				"Started clustering with clusterConfiguration: " + clusterConfiguration));
		try {
			ClusterResult clusterResult = null;

			if (clusterConfiguration.getClusterAlgorithmConfiguration() instanceof TreeClusterConfiguration) {
				clusterResult = runClustering(new TreeClusterer(), clusterConfiguration);
			}
			else if (clusterConfiguration.getClusterAlgorithmConfiguration() instanceof AffinityClusterConfiguration) {
				clusterResult = runClustering(new AffinityClusterer(), clusterConfiguration);
			}
			else if (clusterConfiguration.getClusterAlgorithmConfiguration() instanceof KMeansClusterConfiguration) {
				clusterResult = runClustering(new KMeansClusterer(), clusterConfiguration);
			}
			else {
				throw new IllegalStateException("Unknown ClusterConfiguration: "
						+ clusterConfiguration);

			}
			// break;
			// }
			// case COBWEB_CLUSTERER:
			// clusterResult = runClustering(new HierarchicalClusterer(),
			// clusterConfiguration);
			// break;
			// case AFFINITY_PROPAGATION:
			// clusterResult = runClustering(new AffinityClusterer(),
			// clusterConfiguration);
			// break;
			// case KMEANS_CLUSTERER:
			// clusterResult = runClustering(new KMeansClusterer(),
			// clusterConfiguration);
			// break;
			// case ALPHABETICAL:
			// clusterResult = runClustering(new AlphabeticalPartitioner(),
			// clusterConfiguration);
			// break;

			return clusterResult;
		}
		catch (final Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Clustering failed", e));

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR);
							messageBox.setText("Error");
							messageBox.setMessage("A problem occured during clustering!");
							messageBox.open();
						}
					});
				}
			});

		}

		return null;
	}

	private ClusterResult runClustering(AClusterer clusterer,
			ClusterConfiguration clusterState) {
		ClusterResult result = new ClusterResult();

		try {
			if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
				runContentClustering(clusterer, clusterState, result, 0, 2);
			}
			else if (clusterState.getClusterTarget() == EClustererTarget.DIMENSION_CLUSTERING) {

				runDimensionClustering(clusterer, clusterState, result, 0, 2);
			}
			else {
				throw new IllegalStateException("Unkonwn cluster target: "
						+ clusterState.getClusterTarget());
			}
			clusterer.destroy();
			// result.finish();
			return result;
		}
		catch (OutOfMemoryError e) {
			throw new IllegalStateException("Clusterer out of memory", e);
		}
	}

	private void runContentClustering(AClusterer clusterer,
			ClusterConfiguration clusterState, ClusterResult result, int progressBarOffset,
			int progressBarMulti) {

		clusterer.setClusterState(clusterState);
		PerspectiveInitializationData tempResult = clusterer.getSortedVA(dataDomain,
				clusterState, progressBarOffset, progressBarMulti);
		if (tempResult == null) {
			Logger.log(new Status(IStatus.ERROR, toString(),
					"Clustering result was null, clusterer was: " + clusterer.toString()));
			return;
		}
		result.setRecordResult(tempResult);

	}

	private void runDimensionClustering(AClusterer clusterer,
			ClusterConfiguration clusterConfiguration, ClusterResult result,
			int progressBarOffset, int progressBarMulti) {
		clusterer.setClusterState(clusterConfiguration);

		PerspectiveInitializationData tempResult = clusterer.getSortedVA(dataDomain,
				clusterConfiguration, progressBarOffset, progressBarMulti);
		result.setDimensionResult(tempResult);

	}
}
