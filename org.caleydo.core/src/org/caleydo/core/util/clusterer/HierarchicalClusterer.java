package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer
	extends AClusterer
	implements IClusterer {

	private Cobweb clusterer;

	private ClusterTree tree;

	public HierarchicalClusterer() {
		clusterer = new Cobweb();
	}

	private TempResult cluster(DataTable set, ClusterState clusterState) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indices = new ArrayList<Integer>();

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		int iPercentage = 1;

		if (clusterState.getClustererType() == EClustererType.CONTENT_CLUSTERING) {

			tree = new ClusterTree(set.getDataDomain().getContentIDType());
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			for (int nr = 0; nr < dimensionVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer iContentIndex : contentVA) {

				if (bClusteringCanceled == false) {

					int tempPercentage = (int) ((float) icnt / contentVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iDimensionIndex : dimensionVA) {
						buffer.append(set.get(iDimensionIndex).getFloat(DataRepresentation.RAW, iContentIndex)
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
			tree = new ClusterTree(set.getDataDomain().getDimensionIDType());

			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			for (int nr = 0; nr < contentVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int isto = 0;
			for (Integer iDimensionIndex : dimensionVA) {
				if (bClusteringCanceled == false) {

					int tempPercentage = (int) ((float) isto / dimensionVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iContentIndex : contentVA) {
						buffer.append(set.get(iDimensionIndex).getFloat(DataRepresentation.RAW, iContentIndex)
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
		GeneralManager
			.get()
			.getEventPublisher()
			.triggerEvent(
				new ClusterProgressEvent(25 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		if (clusterState.getClustererType() == EClustererType.CONTENT_CLUSTERING)
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Cobweb clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Cobweb clustering of experiments in progress"));

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
		// for (double cluster : clusters) {
		// for (int i = 0; i < data.numInstances(); i++) {
		// if (clusterAssignments[i] == cluster) {
		// indices.add(i);
		// }
		// }
		// }

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

		// IVirtualArray virtualArray = null;
		// if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
		// virtualArray = new VirtualArray(set.getVA(iVAIdContent).getVAType(), set.depth(), indices);
		// else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
		// virtualArray = new VirtualArray(set.getVA(iVAIdDimension).getVAType(), set.size(), indices);

		CNode node = clusterer.m_cobwebTree;

		ClusterNode clusterNode = new ClusterNode(tree, "Root", 0, true, -1);
		tree.setRootNode(clusterNode);

		CNodeToTree(clusterNode, node, clusterState.getClustererType());

		// ClusterHelper.determineNrElements(tree);
		// ClusterHelper.determineHierarchyDepth(tree);

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(90, false));

		// set.setAlClusterSizes(temp);
		// set.setAlExamples(alExamples);

		GeneralManager
			.get()
			.getEventPublisher()
			.triggerEvent(
				new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		TempResult tempResult = new TempResult();
		tempResult.indices = indices;
		tempResult.tree = tree;
		return tempResult;
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

				ClusterNode currentGraph = new ClusterNode(tree, "Node_" + clusterNr, clusterNr, false, -1);
				// currentGraph.setNrElements(1);

				tree.addChild(clusterNode, currentGraph);
				CNodeToTree(currentGraph, currentNode, eClustererType);
			}
		}
	}

	@Override
	public TempResult getSortedVA(DataTable set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		return cluster(set, clusterState);
	}

}
