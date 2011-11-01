package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterer;
import org.caleydo.core.util.clusterer.algorithm.cobweb.HierarchicalClusterer;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterer;
import org.caleydo.core.util.clusterer.algorithm.nominal.AlphabeticalPartitioner;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.mapping.color.UpdateColorMappingEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Cluster manager handels {@link ClusterConfiguration} and calls corresponding clusterer.
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class ClusterManager {

	ATableBasedDataDomain dataDomain;

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
	 * @param clusterConfiguration
	 *            the configuration of the clustering to be executed.
	 * @return the results of the clustering which can be used to initialize {@link ADataPerspective}s.
	 */
	public ClusterResult cluster(ClusterConfiguration clusterConfiguration) {
		Logger.log(new Status(Status.INFO, this.toString(), "Started clustering with clusterConfiguration: "
			+ clusterConfiguration));
		try {
			ClusterResult clusterResult = null;

			switch (clusterConfiguration.getClustererAlgo()) {
				case TREE_CLUSTERER:
					clusterResult = runClustering(new TreeClusterer(), clusterConfiguration);
					break;
				case COBWEB_CLUSTERER:
					clusterResult = runClustering(new HierarchicalClusterer(), clusterConfiguration);
					break;
				case AFFINITY_PROPAGATION:
					clusterResult = runClustering(new AffinityClusterer(), clusterConfiguration);
					break;
				case KMEANS_CLUSTERER:
					clusterResult = runClustering(new KMeansClusterer(), clusterConfiguration);
					break;
				case ALPHABETICAL:
					clusterResult = runClustering(new AlphabeticalPartitioner(), clusterConfiguration);
					break;
			}

			return clusterResult;
		}
		catch (final Exception e) {
			Logger.log(new Status(Status.ERROR, this.toString(), "Clustering failed", e));

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

	private ClusterResult runClustering(AClusterer clusterer, ClusterConfiguration clusterState) {
		ClusterResult result = new ClusterResult();

		try {
			if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING) {
				runContentClustering(clusterer, clusterState, result, 0, 2);
			}
			else if (clusterState.getClustererType() == ClustererType.DIMENSION_CLUSTERING) {

				runDimensionClustering(clusterer, clusterState, result, 0, 2);
			}
			else if (clusterState.getClustererType() == ClustererType.BI_CLUSTERING) {

				runContentClustering(clusterer, clusterState, result, 0, 1);

				// if (result.dimensionResult.getVirtualArray() != null) {
				runContentClustering(clusterer, clusterState, result, 50, 1);
				// }
			}
			clusterer.destroy();
			// result.finish();
			return result;
		}
		catch (OutOfMemoryError e) {
			throw new IllegalStateException("Clusterer out of memory");
		}
	}

	private void runContentClustering(AClusterer clusterer, ClusterConfiguration clusterState,
		ClusterResult result, int progressBarOffset, int progressBarMulti) {

		clusterer.setClusterState(clusterState);
		PerspectiveInitializationData tempResult =
			clusterer.getSortedVA(dataDomain, clusterState, progressBarOffset, progressBarMulti);
		if (tempResult == null) {
			Logger.log(new Status(IStatus.ERROR, toString(), "Clustering result was null, clusterer was: "
				+ clusterer.toString()));
			return;
		}
		result.setRecordResult(tempResult);
		// result.recordResult = clusterState.getRecordPerspective();
		// result.recordResult.setVirtualArray(new RecordVirtualArray(result.recordResult.getPerspectiveID(),
		// tempResult.getIndices()));
		// result.recordResult.setClusterSizes(tempResult.getClusterSizes());
		// result.recordResult.setSampleElements(tempResult.getSampleElements());
		// if (tempResult.getTree() != null) {
		// tempResult.getTree().initializeIDTypes(clusterState.getRecordIDType());
		// result.recordResult.setTree(tempResult.getTree());
		// }

	}

	private void runDimensionClustering(AClusterer clusterer, ClusterConfiguration clusterState,
		ClusterResult result, int progressBarOffset, int progressBarMulti) {
		clusterer.setClusterState(clusterState);

		PerspectiveInitializationData tempResult =
			clusterer.getSortedVA(dataDomain, clusterState, progressBarOffset, progressBarMulti);
		result.setDimensionResult(tempResult);
		// result.dimensionResult = clusterState.getDimensionPerspective();
		// result.dimensionResult.setVirtualArray(new DimensionVirtualArray(result.dimensionResult
		// .getPerspectiveID(), tempResult.getIndices()));
		// result.dimensionResult.setClusterSizes(tempResult.getClusterSizes());
		// result.dimensionResult.setSampleElements(tempResult.getSampleElements());
		//
		// if (tempResult.getTree() != null) {
		//
		// result.dimensionResult.setTree(tempResult.getTree());
		// // table.getDataDomain().createDimensionGroupsFromDimensionTree(tempResult.tree);
		// result.dimensionResult.getTree().initializeIDTypes(clusterState.getDimensionIDType());
		// }
	}
}
