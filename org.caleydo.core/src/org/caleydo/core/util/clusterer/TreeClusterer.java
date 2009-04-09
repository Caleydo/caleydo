package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.IGraph;

public class TreeClusterer
	implements IClusterer {

	private class ClosestPair {
		private float correlation;
		private int x;
		private int y;
		private boolean update;

		public ClosestPair() {
			this.correlation = 0;
			this.x = 0;
			this.y = 0;
			this.update = false;
		}
	}

	private float[][] similarities = null;

	private int iNrSamples = 0;

	private Tree<ClusterNode> tree;

	public TreeClusterer(int iNrSamples) {
		this.iNrSamples = iNrSamples;
		this.similarities = new float[this.iNrSamples][this.iNrSamples];
	}

	/**
	 * Calculates the similarity matrix for a given set and VA´s
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @return
	 */
	public void determineSimilarities(ISet set, Integer iVAIdContent, Integer iVAIdStorage,
		EClustererType eClustererType) {
		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		IDistanceMeasure distanceMeasure = new EuclideanDistance();

		int icnt1 = 0, icnt2 = 0, isto = 0;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			float[] dArInstance1 = new float[storageVA.size()];
			float[] dArInstance2 = new float[storageVA.size()];

			for (Integer iContentIndex1 : contentVA) {
				isto = 0;
				for (Integer iStorageIndex1 : storageVA) {
					dArInstance1[isto] =
						set.get(iStorageIndex1).getFloat(EDataRepresentation.RAW, iContentIndex1);
					isto++;
				}

				icnt2 = 0;
				for (Integer iContentIndex2 : contentVA) {
					isto = 0;

					if (icnt2 < icnt1) {
						for (Integer iStorageIndex2 : storageVA) {
							dArInstance2[isto] =
								set.get(iStorageIndex2).getFloat(EDataRepresentation.RAW, iContentIndex2);
							isto++;
						}

						similarities[icnt1][icnt2] = distanceMeasure.getMeasure(dArInstance1, dArInstance2);
					}
					icnt2++;
				}
				icnt1++;
			}
		}
		else {

			float[] dArInstance1 = new float[contentVA.size()];
			float[] dArInstance2 = new float[contentVA.size()];

			for (Integer iStorageIndex1 : storageVA) {
				isto = 0;
				for (Integer iContentIndex1 : contentVA) {
					dArInstance1[isto] =
						set.get(iStorageIndex1).getFloat(EDataRepresentation.RAW, iContentIndex1);
					isto++;
				}

				icnt2 = 0;
				for (Integer iStorageIndex2 : storageVA) {
					isto = 0;

					if (icnt2 < icnt1) {
						for (Integer iContentIndex2 : contentVA) {
							dArInstance2[isto] =
								set.get(iStorageIndex2).getFloat(EDataRepresentation.RAW, iContentIndex2);
							isto++;
						}

						similarities[icnt1][icnt2] = distanceMeasure.getMeasure(dArInstance1, dArInstance2);
					}
					icnt2++;
				}
				icnt1++;
			}
		}
	}

	private ClosestPair find_closest_pair(int n, float[][] distmatrix) {

		ClosestPair pair = new ClosestPair();
		float temp;
		float distance = distmatrix[1][0];

		for (int x = 1; x < n; x++) {
			for (int y = 0; y < x; y++) {

				temp = distmatrix[x][y];
				if (temp < distance) {
					distance = temp;
					pair.correlation = temp;
					pair.x = x;
					pair.y = y;
					pair.update = true;
				}
			}
		}
		return pair;
	}

	/**
	 * The palcluster routine performs clustering using pairwise average linking on the given distance matrix.
	 * 
	 * @param set
	 * @return index of virtual array
	 */
	public Integer palcluster(ISet set) {

		int[] clusterid = new int[iNrSamples];
		int[] number = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		for (int j = 0; j < iNrSamples; j++) {
			number[j] = 1;
			clusterid[j] = j;
		}

		int j;

		ClosestPair pair = null;

		ArrayList<Integer> AlIndexes = new ArrayList<Integer>();

		float[][] distmatrix = new float[iNrSamples][iNrSamples];
		distmatrix = similarities.clone();

		for (int n = iNrSamples; n > 1; n--) {
			int sum;
			int is = 1;
			int js = 0;

			pair = find_closest_pair(n, distmatrix);

			if (pair.update) {
				is = pair.x;
				js = pair.y;
			}

			// Update clusterids
			Node node = new Node();

			node.setCorrelation(pair.correlation);
			node.setLeft(clusterid[is]);
			node.setRight(clusterid[js]);

			// Save result
			result[iNrSamples - n] = node;

			// Fix the distances
			sum = number[is] + number[js];
			for (j = 0; j < js; j++) {
				distmatrix[js][j] = distmatrix[is][j] * number[is] + distmatrix[js][j] * number[js];
				distmatrix[js][j] /= sum;
			}
			for (j = js + 1; j < is; j++) {
				distmatrix[j][js] = distmatrix[is][j] * number[is] + distmatrix[j][js] * number[js];
				distmatrix[j][js] /= sum;
			}
			for (j = is + 1; j < n; j++) {
				distmatrix[j][js] = distmatrix[j][is] * number[is] + distmatrix[j][js] * number[js];
				distmatrix[j][js] /= sum;
			}

			for (j = 0; j < is; j++)
				distmatrix[is][j] = distmatrix[n - 1][j];
			for (j = is + 1; j < n - 1; j++)
				distmatrix[j][is] = distmatrix[n - 1][j];

			// Update number of elements in the clusters
			number[js] = sum;
			number[is] = number[n - 1];

			// Update clusterids
			clusterid[js] = n - iNrSamples - 1;
			clusterid[is] = clusterid[n - 1];
		}

		for (int i = 0; i < result.length; i++) {
			if (result[i].getLeft() >= 0)
				AlIndexes.add(result[i].getLeft());
			if (result[i].getRight() >= 0)
				AlIndexes.add(result[i].getRight());
		}

		// set cluster result in Set
		HierarchyGraph graph = new HierarchyGraph();
		graph = treeToGraph(graph, result);

		AlIndexes = graph.getAl();

		// set.setClusteredGraph(graph);
		// set.setTreeStructure(result);

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		return clusteredVAId;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given
	 * distance matrix.
	 * 
	 * @param set
	 * @return index of virtual array
	 */
	public Integer pmlcluster(ISet set) {

		int[] clusterid = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		// Setup a list specifying to which cluster a gene belongs
		for (int j = 0; j < iNrSamples; j++)
			clusterid[j] = j;

		// Arraylist holding clustered indexes
		ArrayList<Integer> AlIndexes = new ArrayList<Integer>();

		int j;

		ClosestPair pair = null;

		float[][] distmatrix = new float[iNrSamples][iNrSamples];
		distmatrix = similarities.clone();

		for (int n = iNrSamples; n > 1; n--) {

			int is = 1;
			int js = 0;

			pair = find_closest_pair(n, distmatrix);

			if (pair.update) {
				is = pair.x;
				js = pair.y;
			}

			// Fix the distances
			for (j = 0; j < js; j++)
				distmatrix[js][j] = Math.max(distmatrix[is][j], distmatrix[js][j]);
			for (j = js + 1; j < is; j++)
				distmatrix[j][js] = Math.max(distmatrix[is][j], distmatrix[j][js]);
			for (j = is + 1; j < n; j++)
				distmatrix[j][js] = Math.max(distmatrix[j][is], distmatrix[j][js]);

			for (j = 0; j < is; j++)
				distmatrix[is][j] = distmatrix[n - 1][j];
			for (j = is + 1; j < n - 1; j++)
				distmatrix[j][is] = distmatrix[n - 1][j];

			// Update clusterids
			Node node = new Node();

			node.setCorrelation(pair.correlation);
			node.setLeft(clusterid[is]);
			node.setRight(clusterid[js]);

			result[iNrSamples - n] = node;

			clusterid[js] = n - iNrSamples - 1;
			clusterid[is] = clusterid[n - 1];
		}

		// set cluster result in Set
		tree = new Tree<ClusterNode>();
		ClusterNode node = new ClusterNode("Root", 1, 0f, 0);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1);

		Node[] result2 = new Node[iNrSamples - 1];
		TreeToTreeStructure(tree.getRoot(), result2);

		// graph = treeToGraph(graph, result);

		// PrintWriter out = null;
		// try {
		// out = new PrintWriter(new BufferedWriter(new FileWriter("result.txt")));
		// }
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// for (int i = 0; i < result.length; i++) {
		// out
		// .println(result[i].getLeft() + "\t" + result[i].getRight() + "\t"
		// + result[i].getCorrelation());
		// }
		// out.close();

		// Node[] result2 = new Node[iNrSamples - 1];
		// graphToTree(graph, result2);

		// PrintWriter out2 = null;
		// try {
		// out2 = new PrintWriter(new BufferedWriter(new FileWriter("result2.txt")));
		// }
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// for (int i = 0; i < result.length; i++) {
		// out2.println(result2[i].getLeft() + "\t" + result2[i].getRight() + "\t"
		// + result2[i].getCorrelation());
		// }
		// out2.close();

		AlIndexes = getAl();

		set.setClusteredTree(tree);

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		return clusteredVAId;
	}

	private ArrayList<Integer> traverse(ArrayList<Integer> indexes, ClusterNode node) {

		if (tree.hasChildren(node) == false)
			indexes.add(node.getClusterNr());
		else {
			for (ClusterNode current : tree.getChildren(node)) {
				traverse(indexes, current);
			}
		}

		return indexes;
	}

	public ArrayList<Integer> getAl() {

		ArrayList<Integer> indexes = new ArrayList<Integer>();
		traverse(indexes, tree.getRoot());

		return indexes;
	}

	private void graphToTree(HierarchyGraph graph, Node[] treeStructure) {
		int index = graph.getClusterNr();

		List<IGraph> graphList = graph.getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN);

		int iNrChildsNode = graphList.size();

		float cor = graph.getCoefficient();

		if (iNrChildsNode == 2) {
			{
				treeStructure[index] = new Node();

				HierarchyGraph current1 = (HierarchyGraph) graphList.get(0);
				if (current1.getNodeName().charAt(0) == 'L')
					treeStructure[index].setLeft(current1.getClusterNr());
				else
					treeStructure[index].setLeft(-(current1.getClusterNr() + 1));

				HierarchyGraph current2 = (HierarchyGraph) graphList.get(1);
				if (current2.getNodeName().charAt(0) == 'L')
					treeStructure[index].setRight(current2.getClusterNr());
				else
					treeStructure[index].setRight(-(current2.getClusterNr() + 1));

				treeStructure[index].setCorrelation(cor);

				graphToTree(current1, treeStructure);
				graphToTree(current2, treeStructure);
			}
		}
		return;
	}

	private void TreeToTreeStructure(ClusterNode node, Node[] treeStructure) {
		int index = node.getClusterNr();

		List<IGraph> graphList = null;// graph.getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN);

		// int iNrChildsNode = graphList.size();

		float cor = node.getCoefficient();

		if (tree.hasChildren(node)) {
			{
				treeStructure[index] = new Node();

				int cnt = 1;

				for (ClusterNode currentNode : tree.getChildren(node)) {

					// HierarchyGraph current1 = (HierarchyGraph) graphList.get(0);
					if (cnt == 1) {
						if (currentNode.getNodeName().charAt(0) == 'L')
							treeStructure[index].setLeft(currentNode.getClusterNr());
						else
							treeStructure[index].setLeft(-(currentNode.getClusterNr() + 1));
					}
					// HierarchyGraph current2 = (HierarchyGraph) graphList.get(1);
					else {
						if (currentNode.getNodeName().charAt(0) == 'L')
							treeStructure[index].setRight(currentNode.getClusterNr());
						else
							treeStructure[index].setRight(-(currentNode.getClusterNr() + 1));
					}
					treeStructure[index].setCorrelation(cor);

					TreeToTreeStructure(currentNode, treeStructure);
					// graphToTree(current2, treeStructure);

					cnt++;
				}
			}
		}
		return;
	}

	private void treeStructureToTree(ClusterNode node, Node[] treeStructure, int index) {

		ClusterNode left = null;
		ClusterNode right = null;

		if (treeStructure[index].getLeft() >= 0) {

			left =
				new ClusterNode("Leaf_" + treeStructure[index].getLeft(), treeStructure[index].getLeft(), 0,
					0);
			tree.addChild(node, left);

		}
		else {
			left =
				new ClusterNode("Node_" + (-(treeStructure[index].getLeft()) - 1), -(treeStructure[index]
					.getLeft()) - 1, treeStructure[index].getCorrelation(), 0);
			tree.addChild(node, left);
			treeStructureToTree(left, treeStructure, -(treeStructure[index].getLeft()) - 1);
		}

		if (treeStructure[index].getRight() >= 0) {

			right =
				new ClusterNode("Leaf_" + treeStructure[index].getRight(), treeStructure[index].getRight(),
					0, 0);
			tree.addChild(node, right);

		}
		else {
			right =
				new ClusterNode("Node_" + (-(treeStructure[index].getRight()) - 1), -(treeStructure[index]
					.getRight()) - 1, treeStructure[index].getCorrelation(), 0);
			tree.addChild(node, right);
			treeStructureToTree(right, treeStructure, -(treeStructure[index].getRight()) - 1);
		}

	}

	private HierarchyGraph treeToGraph(HierarchyGraph graph, Node[] treeStructure) {

		HierarchyGraph[] graphList = new HierarchyGraph[treeStructure.length];

		for (int i = 0; i < treeStructure.length; i++) {

			HierarchyGraph left = null;
			HierarchyGraph right = null;

			if (treeStructure[i].getLeft() >= 0) {
				left =
					new HierarchyGraph("Leaf_" + treeStructure[i].getLeft(), treeStructure[i].getLeft(), 0);
			}
			else {
				left = graphList[-(treeStructure[i].getLeft()) - 1];
				graphList[-(treeStructure[i].getLeft()) - 1] = null;
			}

			if (treeStructure[i].getRight() >= 0) {
				right =
					new HierarchyGraph("Leaf_" + treeStructure[i].getRight(), treeStructure[i].getRight(), 0);
			}
			else {
				right = graphList[-(treeStructure[i].getRight()) - 1];
				graphList[-(treeStructure[i].getRight()) - 1] = null;
			}

			HierarchyGraph temp = new HierarchyGraph("Node_" + i, i, treeStructure[i].getCorrelation());

			temp.addGraph(left, EGraphItemHierarchy.GRAPH_CHILDREN);
			temp.addGraph(right, EGraphItemHierarchy.GRAPH_CHILDREN);

			graphList[i] = temp;
		}

		graph = graphList[treeStructure.length - 1];

		return graph;
	}

	private ArrayList<Integer> TreeSort(int nNodes, double[] order, double[] nodeorder, int[] nodecounts,
		Node[] tree) {

		ArrayList<Integer> indexes = new ArrayList<Integer>();

		int nElements = nNodes + 1;
		int i;
		double[] neworder = new double[nElements];
		int[] clusterids = new int[nElements];
		for (i = 0; i < nElements; i++)
			clusterids[i] = i;
		for (i = 0; i < nNodes; i++) {
			int i1 = tree[i].getLeft();
			int i2 = tree[i].getRight();
			double order1 = (i1 < 0) ? nodeorder[-i1 - 1] : order[i1];
			double order2 = (i2 < 0) ? nodeorder[-i2 - 1] : order[i2];
			int count1 = (i1 < 0) ? nodecounts[-i1 - 1] : 1;
			int count2 = (i2 < 0) ? nodecounts[-i2 - 1] : 1;

			if (i1 < i2) {
				double increase = (order1 < order2) ? count1 : count2;
				int j;
				for (j = 0; j < nElements; j++) {
					int clusterid = clusterids[j];
					if (clusterid == i1 && order1 >= order2)
						neworder[j] += increase;
					if (clusterid == i2 && order1 < order2)
						neworder[j] += increase;
					if (clusterid == i1 || clusterid == i2)
						clusterids[j] = -i - 1;
				}
			}
			else {
				double increase = (order1 <= order2) ? count1 : count2;
				int j;
				for (j = 0; j < nElements; j++) {
					int clusterid = clusterids[j];
					if (clusterid == i1 && order1 > order2)
						neworder[j] += increase;
					if (clusterid == i2 && order1 <= order2)
						neworder[j] += increase;
					if (clusterid == i1 || clusterid == i2)
						clusterids[j] = -i - 1;
				}
			}
		}

		for (i = 0; i < iNrSamples; i++)
			indexes.add(i);

		sort(nElements, neworder, indexes);

		return indexes;
	}

	private void sort(int n, double data[], ArrayList<Integer> indexes) {

		int f, i, iTemp;
		double temp;

		for (f = 1; f < n; f++) {
			if (data[f] > data[f - 1])
				continue;
			temp = data[f];
			iTemp = indexes.get(f);
			i = f - 1;
			while ((i >= 0) && (data[i] > temp)) {
				data[i + 1] = data[i];
				indexes.set(i + 1, indexes.get(i));
				i--;
			}
			data[i + 1] = temp;
			indexes.set(i + 1, iTemp);
		}
	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, EClustererType eClustererType) {

		Integer VAId = 0;

		determineSimilarities(set, idContent, idStorage, eClustererType);

		VAId = pmlcluster(set);
		// VAId = palcluster(set);

		return VAId;
	}
}
