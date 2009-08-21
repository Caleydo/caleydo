package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.GLDendrogram;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer
	extends AClusterer
	implements IClusterer {

	private Cobweb clusterer;

	private Tree<ClusterNode> tree = new Tree<ClusterNode>();

	private int iVAIdContent = 0;
	private int iVAIdStorage = 0;

	public HierarchicalClusterer(int iNrElements) {
		clusterer = new Cobweb();
	}

	private IVirtualArray cluster(ISet set, ClusterState clusterState) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indices = new ArrayList<Integer>();

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		int iPercentage = 1;

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			for (int nr = 0; nr < storageVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer iContentIndex : contentVA) {

				if (bClusteringCanceled == false) {

					int tempPercentage = (int) ((float) icnt / contentVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iStorageIndex : storageVA) {
						buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex)
							+ ", ");

					}
					buffer.append("\n");
					icnt++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return null;
				}
			}
		}
		else {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			for (int nr = 0; nr < contentVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int isto = 0;
			for (Integer iStorageIndex : storageVA) {
				if (bClusteringCanceled == false) {

					int tempPercentage = (int) ((float) isto / storageVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iContentIndex : contentVA) {
						buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex)
							+ ", ");

					}
					buffer.append("\n");
					isto++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return null;
				}
			}
		}
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Cobweb clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Cobweb clustering of experiments in progress"));

		Instances data = null;

		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
			// e1.printStackTrace();
		}

		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(10, false));

		// unsupervised learning --> no class given
		data.setClassIndex(-1);

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
			// e.printStackTrace();
		}

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(45, false));

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
			// e.printStackTrace();
		}
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(60, false));

		double[] clusterAssignments = eval.getClusterAssignments();

		// int nrclusters = eval.getNumClusters();
		// System.out.println(nrclusters);
		// System.out.println(data.numAttributes());
		// System.out.println(data.numInstances());

		ArrayList<Double> clusters = new ArrayList<Double>();

		for (int i = 0; i < clusterAssignments.length; i++) {
			 if (clusters.contains(clusterAssignments[i]) == false)
			clusters.add(clusterAssignments[i]);
		}

		// variant 1
//		for (double cluster : clusters) {
//			for (int i = 0; i < data.numInstances(); i++) {
//				if (clusterAssignments[i] == cluster) {
//					indices.add(i);
//				}
//			}
//		}

		// variant 2
		Arrays.sort(clusterAssignments);
		for (int i = 0; i < data.numInstances(); i++) {
			int temp = 0;
			for (double cluster : clusters) {
				if (clusterAssignments[i] == cluster) {
					indices.add(temp);
					break;
				}
				temp++;
			}
		}
		
		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(80, false));

		IVirtualArray virtualArray = null;
		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), indices);
		else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), indices);

		CNode node = clusterer.m_cobwebTree;

		ClusterNode clusterNode = new ClusterNode("Root", 0, 0f, 0, true);
		tree.setRootNode(clusterNode);

		CNodeToTree(clusterNode, node, clusterState.getClustererType());

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(90, false));

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		// set.setAlClusterSizes(temp);
		// set.setAlExamples(alExamples);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	/**
	 * Function converts tree used by {@link Cobweb} into tree used in {@link GLRadialHierarchy} and
	 * {@link GLDendrogram}
	 * 
	 * @param clusterNode
	 * @param node
	 * @param eClustererType
	 */
	private void CNodeToTree(ClusterNode clusterNode, CNode node, EClustererType eClustererType) {

		if (node.getChilds() != null) {
			int iNrChildsNode = node.getChilds().size();

			for (int i = 0; i < iNrChildsNode; i++) {

				CNode currentNode = (CNode) node.getChilds().elementAt(i);

				int clusterNr = 0;
				clusterNr = currentNode.getClusterNum();

				ClusterNode currentGraph = new ClusterNode("Node_" + clusterNr, clusterNr, 0f, 0, false);
				currentGraph.setNrElements(1);

				tree.addChild(clusterNode, currentGraph);
				CNodeToTree(currentGraph, currentNode, eClustererType);
			}
		}
	}

	@Override
	public IVirtualArray getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		IVirtualArray virtualArray = null;

		this.iVAIdContent = clusterState.getContentVaId();
		this.iVAIdStorage = clusterState.getStorageVaId();

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		virtualArray = cluster(set, clusterState);

		return virtualArray;
	}

}
