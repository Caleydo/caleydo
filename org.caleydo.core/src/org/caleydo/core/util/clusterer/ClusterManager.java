package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.tablebased.UpdateViewEvent;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterer;
import org.caleydo.core.util.clusterer.algorithm.cobweb.HierarchicalClusterer;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterer;
import org.caleydo.core.util.clusterer.algorithm.nominal.AlphabeticalPartitioner;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterState;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Cluster manager handels {@link ClusterState} and calls corresponding clusterer.
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class ClusterManager {

	private DataTable table;

	/**
	 * Constructor
	 * 
	 * @param set
	 */
	public ClusterManager(DataTable table) {
		this.table = table;
	}

	/**
	 * Depending on the cluster state the corresponding clusterer will be called. Virtual arrays for content
	 * and dimension will be returned.
	 * 
	 * @param clusterState
	 *            All information needed ba cluster algorithm
	 * @return array list of {@link IVirtualArray}s including VAs for content and dimension.
	 */
	public ClusterResult cluster(ClusterState clusterState) {

		try {
			ClusterResult clusterResult = null;

			switch (clusterState.getClustererAlgo()) {
				case TREE_CLUSTERER:
					clusterResult = runClustering(new TreeClusterer(), clusterState);
					break;
				case COBWEB_CLUSTERER:
					clusterResult = runClustering(new HierarchicalClusterer(), clusterState);
					break;
				case AFFINITY_PROPAGATION:
					clusterResult = runClustering(new AffinityClusterer(), clusterState);
					break;
				case KMEANS_CLUSTERER:
					clusterResult = runClustering(new KMeansClusterer(), clusterState);
					break;
				case ALPHABETICAL:
					clusterResult = runClustering(new AlphabeticalPartitioner(), clusterState);
					break;
			}

			if (clusterResult != null)
				GeneralManager.get().getEventPublisher().triggerEvent(new UpdateViewEvent());

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

	private ClusterResult runClustering(AClusterer clusterer, ClusterState clusterState) {
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
//			result.finish();
			return result;
		}
		catch (OutOfMemoryError e) {
			throw new IllegalStateException("Clusterer out of memory");
		}
	}

	private void runContentClustering(AClusterer clusterer, ClusterState clusterState, ClusterResult result,
		int progressBarOffset, int progressBarMulti) {

		clusterer.setClusterState(clusterState);
		TempResult tempResult =
			clusterer.getSortedVA(table, clusterState, progressBarOffset, progressBarMulti);
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

	private void runDimensionClustering(AClusterer clusterer, ClusterState clusterState,
		ClusterResult result, int progressBarOffset, int progressBarMulti) {
		clusterer.setClusterState(clusterState);

		TempResult tempResult =
			clusterer.getSortedVA(table, clusterState, progressBarOffset, progressBarMulti);
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
