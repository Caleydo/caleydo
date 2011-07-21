package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.ContentData;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.StorageData;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.util.clusterer.nominal.AlphabeticalPartitioner;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
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

	private DataTable set;

	/**
	 * Constructor
	 * 
	 * @param set
	 */
	public ClusterManager(DataTable set) {
		this.set = set;
	}

	/**
	 * Depending on the cluster state the corresponding clusterer will be called. Virtual arrays for content
	 * and storage will be returned.
	 * 
	 * @param clusterState
	 *            All information needed ba cluster algorithm
	 * @return array list of {@link IVirtualArray}s including VAs for content and storage.
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
							Shell shell = new Shell();
							MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
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
			if (clusterState.getClustererType() == EClustererType.CONTENT_CLUSTERING) {
				runContentClustering(clusterer, clusterState, result, 0, 2);
			}
			else if (clusterState.getClustererType() == EClustererType.STORAGE_CLUSTERING) {

				runStorageClustering(clusterer, clusterState, result, 0, 2);
			}
			else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {

				runContentClustering(clusterer, clusterState, result, 0, 1);

				if (result.storageResult.getStorageVA() != null) {
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
		TempResult tempResult = clusterer.getSortedVA(set, clusterState, progressBarOffset, progressBarMulti);
		if (tempResult == null) {
			Logger.log(new Status(IStatus.ERROR, toString(), "Clustering result was null, clusterer was: "
				+ clusterer.toString()));
			return;
		}
		result.contentResult = new ContentData(set.getDataDomain().getContentIDType());
		result.contentResult.setContentVA(new ContentVirtualArray(clusterState.getContentVAType(),
			tempResult.indices));
		result.contentResult.setContentClusterSizes(tempResult.clusterSizes);
		result.contentResult.setContentSampleElements(tempResult.sampleElements);
		if (tempResult.tree != null) {
			tempResult.tree.initializeIDTypes(clusterState.getContentIDType());
			result.contentResult.setContentTree(tempResult.tree);
		}

	}

	private void runStorageClustering(AClusterer clusterer, ClusterState clusterState, ClusterResult result,
		int progressBarOffset, int progressBarMulti) {
		clusterer.setClusterState(clusterState);

		TempResult tempResult = clusterer.getSortedVA(set, clusterState, progressBarOffset, progressBarMulti);
		result.storageResult = new StorageData();
		result.storageResult.setStorageVA(new StorageVirtualArray(clusterState.getStorageVAType(),
			tempResult.indices));
		result.storageResult.setStorageClusterSizes(tempResult.clusterSizes);
		result.storageResult.setStorageSampleElements(tempResult.sampleElements);

		if (tempResult.tree == null) {
			result.storageResult.setDefaultTree(true);
		}
		else {
			result.storageResult.setStorageTree(tempResult.tree);
			set.getDataDomain().createDimensionGroupsFromStorageTree(tempResult.tree);
			result.storageResult.setDefaultTree(false);
			result.storageResult.getStorageTree().initializeIDTypes(clusterState.getStorageIDType());
		}
	}
}
