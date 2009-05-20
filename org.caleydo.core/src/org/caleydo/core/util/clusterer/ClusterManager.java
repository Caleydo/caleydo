package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IGroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Cluster manager handels clusterstate and calls corresponding clusterer.
 * 
 * @author Bernhard Schlegl
 */
public class ClusterManager {

	private ISet set;

	/**
	 * Constructor
	 * 
	 * @param set
	 */
	public ClusterManager(ISet set) {
		this.set = set;
	}

	/**
	 * Depending on the clusterstate the corresponding clusterer will be called. In case of an error -1 will
	 * be returned and a message box will be triggered.
	 * 
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @param clusterState
	 * @return -1 in case of error, Id of VA
	 */
	public int cluster(Integer iVAIdContent, Integer iVAIdStorage, ClusterState clusterState,
		int iProgressBarOffsetValue, int iProgressMultiplier) {

		Integer VAId = 0;

		AClusterer clusterer;

		switch (clusterState.getClustererAlgo()) {
			case TREE_CLUSTERER:

				if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
					clusterer = new TreeClusterer(set.getVA(iVAIdContent).size());
				else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
					clusterer = new TreeClusterer(set.getVA(iVAIdStorage).size());
				else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {
					System.out.println("Not implemented yet");
					clusterer = new TreeClusterer(set.getVA(iVAIdContent).size());
				}
				else
					return -1;

				// System.out.println("treeClustering in progress ... ");
				VAId =
					clusterer.getSortedVAId(set, iVAIdContent, iVAIdStorage, clusterState,
						iProgressBarOffsetValue, iProgressMultiplier);
				// System.out.println("treeClustering done");

				clusterer.destroy();
				break;

			case COBWEB_CLUSTERER:

				clusterer = new HierarchicalClusterer(0);

				// System.out.println("Cobweb in progress ... ");
				VAId =
					clusterer.getSortedVAId(set, iVAIdContent, iVAIdStorage, clusterState,
						iProgressBarOffsetValue, iProgressMultiplier);
				// System.out.println("Cobweb done");

				clusterer.destroy();
				break;

			case AFFINITY_PROPAGATION:

				if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
					clusterer = new AffinityClusterer(set.getVA(iVAIdContent).size());
				else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
					clusterer = new AffinityClusterer(set.getVA(iVAIdStorage).size());
				else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {
					System.out.println("Not implemented yet");
					clusterer = new AffinityClusterer(set.getVA(iVAIdContent).size());
				}
				else
					return -1;

				// System.out.println("affinityPropagation in progress ... ");
				VAId =
					clusterer.getSortedVAId(set, iVAIdContent, iVAIdStorage, clusterState,
						iProgressBarOffsetValue, iProgressMultiplier);
				// System.out.println("affinityPropagation done");

				clusterer.destroy();
				break;

			case KMEANS_CLUSTERER:

				clusterer = new KMeansClusterer(0);

				// System.out.println("KMeansClusterer in progress ... ");
				VAId =
					clusterer.getSortedVAId(set, iVAIdContent, iVAIdStorage, clusterState,
						iProgressBarOffsetValue, iProgressMultiplier);
				// System.out.println("KMeansClusterer done");

				clusterer.destroy();
				break;
		}

		if (VAId == -1) {

			GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
					messageBox.setText("Problem");
					messageBox.setMessage("A problem occured during clustering!");
					messageBox.open();
				}
			});

			return -1;
		}

		IVirtualArray virtualArray = set.getVA(VAId);

		if (clusterState.getClustererAlgo() == EClustererAlgo.AFFINITY_PROPAGATION
			|| clusterState.getClustererAlgo() == EClustererAlgo.KMEANS_CLUSTERER) {
			// || clusterState.getClustererAlgo() == EClustererAlgo.COBWEB_CLUSTERER) {

			IGroupList groupList = new GroupList(virtualArray.size());

			ArrayList<Integer> examples = set.getAlExamples();
			int cnt = 0;
			for (Integer iter : set.getAlClusterSizes()) {
				Group temp = new Group(iter, false, examples.get(cnt), ESelectionType.NORMAL);
				groupList.append(temp);
				cnt++;
			}
			virtualArray.setGroupList(groupList);
		}

		return VAId;
	}
}
