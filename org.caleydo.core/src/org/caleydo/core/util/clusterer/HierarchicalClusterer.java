package org.caleydo.core.util.clusterer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class HierarchicalClusterer
	implements IClusterer {

	private Cobweb clusterer;

	Tree<ClusterNode> tree = new Tree<ClusterNode>();

	private ProgressBar pbBuildInstances;
	private ProgressBar pbClusterer;
	private Shell shell;

	public HierarchicalClusterer(int iNrElements) {
		clusterer = new Cobweb();
	}

	private void buildProgressBar() {

		shell = new Shell();

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		composite.setFocus();

		Group progressBarGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		progressBarGroup.setText("Progress");
		progressBarGroup.setLayout(new RowLayout(1));
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		progressBarGroup.setLayoutData(gridData);

		Label label = new Label(progressBarGroup, SWT.NULL);
		label.setText("Building instances used by weka clusterer in progress");
		label.setAlignment(SWT.RIGHT);

		pbBuildInstances = new ProgressBar(progressBarGroup, SWT.SMOOTH);

		Label label2 = new Label(progressBarGroup, SWT.NULL);
		label2.setText("Cobweb clustering in progress");
		label2.setAlignment(SWT.RIGHT);

		pbClusterer = new ProgressBar(progressBarGroup, SWT.SMOOTH);

		composite.pack();

		shell.pack();
		shell.open();
	}

	public Integer cluster(ISet set, Integer iVAIdOriginal, Integer iVAIdStorage,
		EClustererType eClustererType) {

		// Arraylist holding clustered indexes
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		StringBuffer buffer = new StringBuffer();

		buffer.append("@relation test\n\n");

		IVirtualArray contentVA = set.getVA(iVAIdOriginal);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			int iNrElements = contentVA.size();
			pbBuildInstances.setMinimum(0);
			pbBuildInstances.setMaximum(iNrElements);

			for (int nr = 0; nr < storageVA.size(); nr++) {
				buffer.append("@attribute Patient" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer iContentIndex : contentVA) {
				pbBuildInstances.setSelection(icnt);
				for (Integer iStorageIndex : storageVA) {
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex)
						+ ", ");

				}
				buffer.append("\n");
				icnt++;
			}
		}
		else {

			int iNrElements = storageVA.size();
			pbBuildInstances.setMinimum(0);
			pbBuildInstances.setMaximum(iNrElements);

			for (int nr = 0; nr < contentVA.size(); nr++) {
				buffer.append("@attribute Gene" + nr + " real\n");
			}

			buffer.append("@data\n");

			int icnt = 0;
			for (Integer iStorageIndex : storageVA) {
				pbBuildInstances.setSelection(icnt);
				for (Integer iContentIndex : contentVA) {
					buffer.append(set.get(iStorageIndex).getFloat(EDataRepresentation.RAW, iContentIndex)
						+ ", ");

				}
				buffer.append("\n");
				icnt++;
			}
		}

		Instances data = null;
		try {
			data = new Instances(new StringReader(buffer.toString()));
		}
		catch (IOException e1) {
			return -1;
			// e1.printStackTrace();
		}

		pbClusterer.setMinimum(0);
		pbClusterer.setMaximum(5);

		pbClusterer.setSelection(1);

		// unsupervised learning --> no class given
		data.setClassIndex(-1);

		try {
			// train the clusterer
			clusterer.buildClusterer(data);
		}
		catch (Exception e) {
			return -1;
			// e.printStackTrace();
		}
		pbClusterer.setSelection(2);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer); // the cluster to evaluate
		try {
			eval.evaluateClusterer(data);
		}
		catch (Exception e) {
			return -1;
			// e.printStackTrace();
		}
		pbClusterer.setSelection(3);

		double[] clusterAssignments = eval.getClusterAssignments();

		// int nrclusters = eval.getNumClusters();
		// System.out.println(nrclusters);
		// System.out.println(data.numAttributes());
		// System.out.println(data.numInstances());

		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<Integer> alExamples = new ArrayList<Integer>();

		ArrayList<Double> clusters = new ArrayList<Double>();

		for (int i = 0; i < clusterAssignments.length; i++) {
			if (clusters.contains(clusterAssignments[i]) == false)
				clusters.add(clusterAssignments[i]);
		}

		HashMap<Double, Integer> hashClusters = new HashMap<Double, Integer>();

		for (int i = 0; i < clusters.size(); i++) {
			hashClusters.put(clusters.get(i), i);
			temp.add(0);
			alExamples.add(0);
		}

		for (double cluster : clusters) {
			for (int i = 0; i < data.numInstances(); i++) {
				if (clusterAssignments[i] == cluster) {
					indexes.add(i);
					temp.set(hashClusters.get(cluster), temp.get(hashClusters.get(cluster)) + 1);
				}
			}
		}
		pbClusterer.setSelection(4);

		Integer clusteredVAId = set.createStorageVA(indexes);

		CNode node = clusterer.m_cobwebTree;

		ClusterNode clusterNode = new ClusterNode("Root", 1, 0f, 0, true);
		tree.setRootNode(clusterNode);

		CNodeToTree(clusterNode, node);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		pbClusterer.setSelection(5);

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		set.setAlClusterSizes(temp);
		set.setAlExamples(alExamples);

		return clusteredVAId;
	}

	private void CNodeToTree(ClusterNode clusterNode, CNode node) {

		if (node.getChilds() != null) {
			int iNrChildsNode = node.getChilds().size();

			for (int i = 0; i < iNrChildsNode; i++) {

				CNode currentNode = (CNode) node.getChilds().elementAt(i);
				ClusterNode currentGraph =
					new ClusterNode("Node_" + currentNode.getClusterNum(), currentNode.getClusterNum(), 0f,
						0, false);
				currentGraph.setNrElements(1);

				tree.addChild(clusterNode, currentGraph);
				// temp.addGraph(matchTree(currentGraph, currentNode), EGraphItemHierarchy.GRAPH_CHILDREN);
				CNodeToTree(currentGraph, currentNode);
			}
		}

	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, ClusterState clusterState) {

		Integer VAId = 0;

		buildProgressBar();

		VAId = cluster(set, idContent, idStorage, clusterState.getClustererType());

		shell.close();

		return VAId;
	}
}
