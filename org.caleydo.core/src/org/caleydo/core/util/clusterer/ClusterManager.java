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
 * Cluster manager handels {@link ClusterState} and calls corresponding clusterer.
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
	 * @param clusterState
	 * @return
	 */
	public ArrayList<IVirtualArray> cluster(ClusterState clusterState) {

		ArrayList<IVirtualArray> iAlVAs = new ArrayList<IVirtualArray>();
		iAlVAs.add(set.getVA(clusterState.getContentVaId()));
		iAlVAs.add(set.getVA(clusterState.getStorageVaId()));

		IVirtualArray tempVA = null;

		AClusterer clusterer = null;

		switch (clusterState.getClustererAlgo()) {
			case TREE_CLUSTERER:

				clusterer = new TreeClusterer(0);

				try {
					if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {
						clusterer = new TreeClusterer(set.getVA(clusterState.getContentVaId()).size());

						tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

						if (tempVA != null)
							iAlVAs.set(0, tempVA);

					}
					else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {
						clusterer = new TreeClusterer(set.getVA(clusterState.getStorageVaId()).size());

						tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

						if (tempVA != null)
							iAlVAs.set(1, tempVA);

					}
					else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {

						clusterer = new TreeClusterer(set.getVA(clusterState.getStorageVaId()).size());

						clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
						tempVA = clusterer.getSortedVA(set, clusterState, 0, 1);

						if (tempVA != null) {
							iAlVAs.set(1, tempVA);

							clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
							clusterer = new TreeClusterer(set.getVA(clusterState.getContentVaId()).size());

							tempVA = clusterer.getSortedVA(set, clusterState, 50, 1);

							if (tempVA != null)
								iAlVAs.set(0, tempVA);
						}
					}
				}
				catch (OutOfMemoryError e) {

				}

				break;

			case COBWEB_CLUSTERER:

				clusterer = new HierarchicalClusterer(0);

				if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

					tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

					if (tempVA != null)
						iAlVAs.set(0, tempVA);

				}
				else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {

					tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

					if (tempVA != null)
						iAlVAs.set(1, tempVA);

				}
				else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {

					clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
					tempVA = clusterer.getSortedVA(set, clusterState, 0, 1);

					if (tempVA != null) {
						iAlVAs.set(1, tempVA);

						clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
						tempVA = clusterer.getSortedVA(set, clusterState, 50, 1);

						if (tempVA != null)
							iAlVAs.set(0, tempVA);
					}
				}

				break;

			case AFFINITY_PROPAGATION:

				clusterer = new AffinityClusterer(0);

				try {
					if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {
						clusterer = new AffinityClusterer(set.getVA(clusterState.getContentVaId()).size());

						tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

						if (tempVA != null) {
							iAlVAs.set(0, tempVA);
							setGroupList(tempVA);
						}

					}
					else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {
						clusterer = new AffinityClusterer(set.getVA(clusterState.getStorageVaId()).size());

						tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

						if (tempVA != null) {
							iAlVAs.set(1, tempVA);
							setGroupList(tempVA);
						}

					}
					else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {

						clusterer = new AffinityClusterer(set.getVA(clusterState.getStorageVaId()).size());

						clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
						tempVA = clusterer.getSortedVA(set, clusterState, 0, 1);

						if (tempVA != null) {
							iAlVAs.set(1, tempVA);
							setGroupList(tempVA);

							clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
							clusterer =
								new AffinityClusterer(set.getVA(clusterState.getContentVaId()).size());

							tempVA = clusterer.getSortedVA(set, clusterState, 50, 1);

							if (tempVA != null) {
								iAlVAs.set(0, tempVA);
								setGroupList(tempVA);
							}
						}
					}
				}
				catch (OutOfMemoryError e) {
					clusterer.destroy();
				}

				break;

			case KMEANS_CLUSTERER:

				clusterer = new KMeansClusterer(0);

				if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

					tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

					if (tempVA != null) {
						iAlVAs.set(0, tempVA);
						setGroupList(tempVA);
					}
				}
				else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {

					tempVA = clusterer.getSortedVA(set, clusterState, 0, 2);

					if (tempVA != null) {
						iAlVAs.set(1, tempVA);
						setGroupList(tempVA);
					}

				}
				else if (clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {

					clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
					tempVA = clusterer.getSortedVA(set, clusterState, 0, 1);

					if (tempVA != null) {
						iAlVAs.set(1, tempVA);
						setGroupList(tempVA);

						clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
						tempVA = clusterer.getSortedVA(set, clusterState, 50, 1);

						if (tempVA != null) {
							iAlVAs.set(0, tempVA);
							setGroupList(tempVA);
						}
					}
				}
				break;
		}

		clusterer.destroy();

		if (tempVA == null) {

			GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
					messageBox.setText("Error");
					messageBox.setMessage("A problem occured during clustering!");
					messageBox.open();
				}
			});

		}
		// if (tempVA == -2) {
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// Shell shell = new Shell();
		// MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
		// messageBox.setText("Cancel");
		// messageBox.setMessage("Clustering aborted by user!");
		// messageBox.open();
		// }
		// });
		// }
		// if (tempVA == -3) {
		//
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// Shell shell = new Shell();
		// MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
		// messageBox.setText("Error");
		// messageBox
		// .setMessage("Algorithm did not converge! \n\nIn case of affinity propagation please try to use another cluster factor");
		// messageBox.open();
		// }
		// });
		//
		// }

		return iAlVAs;
	}

	/**
	 * Function determines group information for virtual array. Used by affinity propagation and kMeans.
	 * 
	 * @param VAId
	 *            Id of virtual array
	 */
	private void setGroupList(IVirtualArray virtualArray) {

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

}
