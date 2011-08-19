package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.tablebased.UpdateViewEvent;
import org.caleydo.core.util.clusterer.nominal.AlphabeticalPartitioner;
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
		catch (final RuntimeException e) {
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

				if (result.dimensionResult.getVA() != null) {
					runContentClustering(clusterer, clusterState, result, 50, 1);
				}
			}
			clusterer.destroy();
			result.finish();
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
		result.recordResult = clusterState.getRecordPerspective();
		result.recordResult.setVA(new RecordVirtualArray(result.recordResult.getPerspectiveID(),
			tempResult.indices));
		result.recordResult.setClusterSizes(tempResult.clusterSizes);
		result.recordResult.setSampleElements(tempResult.sampleElements);
		if (tempResult.tree != null) {
			tempResult.tree.initializeIDTypes(clusterState.getRecordIDType());
			result.recordResult.setTree(tempResult.tree);
		}

	}

	private void runDimensionClustering(AClusterer clusterer, ClusterState clusterState,
		ClusterResult result, int progressBarOffset, int progressBarMulti) {
		clusterer.setClusterState(clusterState);

		TempResult tempResult =
			clusterer.getSortedVA(table, clusterState, progressBarOffset, progressBarMulti);
		result.dimensionResult = clusterState.getDimensionPerspective();
		result.dimensionResult.setVA(new DimensionVirtualArray(result.dimensionResult
			.getPerspectiveID(), tempResult.indices));
		result.dimensionResult.setClusterSizes(tempResult.clusterSizes);
		result.dimensionResult.setSampleElements(tempResult.sampleElements);

		if (tempResult.tree != null) {

			result.dimensionResult.setTree(tempResult.tree);
			table.getDataDomain().createDimensionGroupsFromDimensionTree(tempResult.tree);
			result.dimensionResult.getTree().initializeIDTypes(clusterState.getDimensionIDType());
		}
	}
}
