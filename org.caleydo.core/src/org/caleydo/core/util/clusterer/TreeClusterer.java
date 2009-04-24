package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.mapping.IDMappingHelper;

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

	private ISet set = null;
	private Integer idContent = 0;
	private Integer idStorage = 0;

	private float[][] similarities = null;

	private int iNrSamples = 0;

	private boolean bStart0 = true;

	private Tree<ClusterNode> tree;

	public TreeClusterer(int iNrSamples) {
		this.iNrSamples = iNrSamples;
		this.similarities = new float[this.iNrSamples][this.iNrSamples];
	}

	/**
	 * Calculates the similarity matrix for a given set and VA�s
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

			bStart0 = true;

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

			bStart0 = false;

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
		normalizeSimilarities();

	}

	private void normalizeSimilarities() {

		float max = Float.MIN_VALUE;

		for (int i = 0; i < similarities.length; i++) {
			for (int j = 0; j < similarities.length; j++) {
				max = Math.max(max, similarities[i][j]);
			}
		}

		for (int i = 0; i < similarities.length; i++) {
			for (int j = 0; j < similarities.length; j++) {
				similarities[i][j] = similarities[i][j] / max;
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
	public Integer palcluster() {

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
		tree = new Tree<ClusterNode>();
		ClusterNode node = new ClusterNode("Root", 0, 0f, 0);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		AlIndexes = getAl();

		set.setClusteredTree(tree);

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
	public Integer pmlcluster() {

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
		ClusterNode node = new ClusterNode("Root", 0, 0f, 0);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		AlIndexes = getAl();

		set.setClusteredTree(tree);

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		return clusteredVAId;
	}

	private ArrayList<Integer> traverse(ArrayList<Integer> indexes, ClusterNode node) {

		if (tree.hasChildren(node) == false) {
			// FIXME: problem with indexes (storageVA vs. contentVA ???)
			indexes.add(node.getClusterNr() + 1);
		}
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

	private void treeStructureToTree(ClusterNode node, Node[] treeStructure, int index) {

		ClusterNode left = null;
		ClusterNode right = null;

		if (treeStructure[index].getLeft() >= 0) {

			String NodeName; // = "Leaf_" + treeStructure[index].getLeft();

			NodeName = IDMappingHelper.get().getShortNameFromDavid(treeStructure[index].getLeft() + 1);
			if (NodeName == null) {
				NodeName = "Unknown";
			}

			NodeName += " | ";
			NodeName +=
				IDMappingHelper.get().getRefSeqStringFromStorageIndex(treeStructure[index].getLeft() + 1);

			left =
				new ClusterNode(NodeName, treeStructure[index].getLeft(), treeStructure[index]
					.getCorrelation(), 0);

			left.setNrElements(1);

			// left = new ClusterNode("Leaf_" + treeStructure[index].getLeft(),
			// treeStructure[index].getLeft(), 0, 0);
			tree.addChild(node, left);

		}
		else {
			int random = (int) ((Math.random() * Integer.MAX_VALUE) + 1);

			left =
				new ClusterNode("Node_" + (-(treeStructure[index].getLeft()) - 1), random,
					treeStructure[index].getCorrelation(), 0);
			tree.addChild(node, left);
			treeStructureToTree(left, treeStructure, -(treeStructure[index].getLeft()) - 1);
		}

		if (treeStructure[index].getRight() >= 0) {

			String NodeName; // = "Leaf_" + treeStructure[index].getLeft();

			NodeName = IDMappingHelper.get().getShortNameFromDavid(treeStructure[index].getRight() + 1);
			if (NodeName == null) {
				NodeName = "Unknown";
			}

			NodeName += " | ";
			NodeName +=
				IDMappingHelper.get().getRefSeqStringFromStorageIndex(treeStructure[index].getRight() + 1);

			right =
				new ClusterNode(NodeName, treeStructure[index].getRight(), treeStructure[index]
					.getCorrelation(), 0);

			right.setNrElements(1);

			// right = new ClusterNode("Leaf_" + treeStructure[index].getRight(),
			// treeStructure[index].getRight(), 0, 0);
			tree.addChild(node, right);

		}
		else {
			int random = (int) ((Math.random() * Integer.MAX_VALUE) + 1);

			right =
				new ClusterNode("Node_" + (-(treeStructure[index].getRight()) - 1), random,
					treeStructure[index].getCorrelation(), 0);
			tree.addChild(node, right);
			treeStructureToTree(right, treeStructure, -(treeStructure[index].getRight()) - 1);
		}

	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, EClustererType eClustererType) {

		Integer VAId = 0;

		determineSimilarities(set, idContent, idStorage, eClustererType);

		this.set = set;
		this.idContent = idContent;
		this.idStorage = idStorage;

		VAId = pmlcluster();
		// VAId = palcluster();

		return VAId;
	}
}
