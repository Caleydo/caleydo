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
package org.caleydo.core.util.clusterer.algorithm.cobweb;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.IClusterer;
import org.caleydo.core.util.clusterer.algorithm.AClusterer;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;

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

	private PerspectiveInitializationData cluster(DataTable table, ClusterConfiguration clusterState) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		int iPercentage = 1;

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING) {

			tree =
				new ClusterTree(table.getDataDomain().getRecordIDType(), clusterState
					.getSourceRecordPerspective().getVirtualArray().size());
			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			for (int nr = 0; nr < dimensionVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer recordIndex : recordVA) {

				if (bClusteringCanceled == false) {

					int tempPercentage = (int) ((float) icnt / recordVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher()
							.triggerEvent(new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					for (Integer iDimensionIndex : dimensionVA) {
						buffer.append(table.getFloat(DataRepresentation.RAW, recordIndex, iDimensionIndex)
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
			tree =
				new ClusterTree(table.getDataDomain().getDimensionIDType(), clusterState
					.getSourceDimensionPerspective().getVirtualArray().size());

			GeneralManager.get().getEventPublisher()
				.triggerEvent(new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			for (int nr = 0; nr < recordVA.size(); nr++) {
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

					for (Integer recordIndex : recordVA) {
						buffer.append(table.getFloat(DataRepresentation.RAW, recordIndex, iDimensionIndex)
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

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING)
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
		// Arrays.sort(clusterAssignments);
		// for (int i = 0; i < data.numInstances(); i++) {
		// int temp = 0;
		// for (double cluster : clusters) {
		// if (clusterAssignments[i] == cluster) {
		// indices.add(temp);
		// break;
		// }
		// temp++;
		// }
		// }

		processEvents();
		if (bClusteringCanceled) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(80, false));

		// IVirtualArray virtualArray = null;
		// if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
		// virtualArray = new VirtualArray(table.getVA(iVAIdContent).getVAType(), table.depth(), indices);
		// else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING)
		// virtualArray = new VirtualArray(table.getVA(iVAIdDimension).getVAType(), table.size(), indices);

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

		// table.setAlClusterSizes(temp);
		// table.setAlExamples(alExamples);

		GeneralManager
			.get()
			.getEventPublisher()
			.triggerEvent(
				new ClusterProgressEvent(50 * iProgressBarMultiplier + iProgressBarOffsetValue, true));

		PerspectiveInitializationData tempResult = new PerspectiveInitializationData();

		tempResult.setData(tree);
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
	private void CNodeToTree(ClusterNode clusterNode, CNode node, ClustererType eClustererType) {

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
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain,
		ClusterConfiguration clusterState, int iProgressBarOffsetValue, int iProgressBarMultiplier) {

		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		return cluster(dataDomain.getTable(), clusterState);
	}

}
