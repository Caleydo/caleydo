package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;

public class TreeClusterer
	extends AClusterer {

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

	private Tree<ClusterNode> tree;

	private EDistanceMeasure eDistanceMeasure;

	public TreeClusterer(int iNrSamples) {
		try {
			this.iNrSamples = iNrSamples;
			this.similarities = new float[this.iNrSamples][this.iNrSamples];
		}
		catch (OutOfMemoryError e) {
			throw new OutOfMemoryError();
		}
	}

	/**
	 * Calculates the similarity matrix for a given set and VAs
	 * 
	 * @param set
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @return
	 */
	public int determineSimilarities(ISet set, Integer iVAIdContent, Integer iVAIdStorage,
		EClustererType eClustererType) {

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		IDistanceMeasure distanceMeasure;
		if (eDistanceMeasure == EDistanceMeasure.EUCLIDEAN_DISTANCE)
			distanceMeasure = new EuclideanDistance();
		else
			distanceMeasure = new PearsonCorrelation();

		int icnt1 = 0, icnt2 = 0, isto = 0;
		int iPercentage = 1;

		if (eClustererType == EClustererType.GENE_CLUSTERING) {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for gene clustering"));

			float[] dArInstance1 = new float[storageVA.size()];
			float[] dArInstance2 = new float[storageVA.size()];

			for (Integer iContentIndex1 : contentVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / contentVA.size() * 100);

					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer iStorageIndex1 : storageVA) {
						dArInstance1[isto] =
							set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer iContentIndex2 : contentVA) {
						processEvents();
						isto = 0;

						if (icnt2 < icnt1) {
							for (Integer iStorageIndex2 : storageVA) {
								dArInstance2[isto] =
									set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED,
										iContentIndex2);
								isto++;
							}

							similarities[contentVA.indexOf(iContentIndex1)][contentVA.indexOf(iContentIndex2)] =
								distanceMeasure.getMeasure(dArInstance1, dArInstance2);
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		}
		else {

			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Determine Similarities for experiment clustering"));

			float[] dArInstance1 = new float[contentVA.size()];
			float[] dArInstance2 = new float[contentVA.size()];

			for (Integer iStorageIndex1 : storageVA) {

				if (bClusteringCanceled == false) {
					int tempPercentage = (int) ((float) icnt1 / storageVA.size() * 100);
					if (iPercentage == tempPercentage) {
						GeneralManager.get().getEventPublisher().triggerEvent(
							new ClusterProgressEvent(iPercentage, false));
						iPercentage++;
					}

					isto = 0;
					for (Integer iContentIndex1 : contentVA) {
						dArInstance1[isto] =
							set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED, iContentIndex1);
						isto++;
					}

					icnt2 = 0;
					for (Integer iStorageIndex2 : storageVA) {
						isto = 0;

						if (icnt2 < icnt1) {
							for (Integer iContentIndex2 : contentVA) {
								dArInstance2[isto] =
									set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED,
										iContentIndex2);
								isto++;
							}

							similarities[storageVA.indexOf(iStorageIndex1)][storageVA.indexOf(iStorageIndex2)] =
								distanceMeasure.getMeasure(dArInstance1, dArInstance2);
						}
						icnt2++;
					}
					icnt1++;
					processEvents();
				}
				else {
					GeneralManager.get().getEventPublisher()
						.triggerEvent(new ClusterProgressEvent(100, true));
					return -2;
				}
			}
		}
		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 25 + iProgressBarOffsetValue, true));
		normalizeSimilarities();

		return 0;
	}

	private int iNodeCounter = (int) Math.floor(Integer.MAX_VALUE / 2);

	private int getNodeCounter() {
		return iNodeCounter++;
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
	private Integer palcluster(EClustererType eClustererType) {

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

		float[][] distmatrix;

		try {
			distmatrix = new float[iNrSamples][iNrSamples];
		}
		catch (OutOfMemoryError e) {
			return -1;
		}

		distmatrix = similarities.clone();

		int iPercentage = 1;

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Tree clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Tree clustering of experiments in progress"));

		for (int n = iNrSamples; n > 1; n--) {
			if (bClusteringCanceled == false) {
				int sum;
				int is = 1;
				int js = 0;

				int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
				if (iPercentage == tempPercentage) {
					GeneralManager.get().getEventPublisher().triggerEvent(
						new ClusterProgressEvent(iPercentage, false));
					iPercentage++;
				}

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
				processEvents();
			}
			else {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
				return -2;
			}
		}

		for (int i = 0; i < result.length; i++) {
			if (result[i].getLeft() >= 0)
				AlIndexes.add(result[i].getLeft());
			if (result[i].getRight() >= 0)
				AlIndexes.add(result[i].getRight());
		}

		// set cluster result in Set
		tree = new Tree<ClusterNode>();

		int random = (int) ((Math.random() * Integer.MAX_VALUE) + 1);

		ClusterNode node = new ClusterNode("Root", random, 0f, 0, true);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		determineExpressionValue(eClustererType);

		AlIndexes = getAl();

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 50 + iProgressBarOffsetValue, true));

		return clusteredVAId;
	}

	private void determineExpressionValue(EClustererType eClustererType) {

		determineExpressionValueRec(tree.getRoot(), eClustererType);
	}

	private float[] determineExpressionValueRec(ClusterNode node, EClustererType eClustererType) {

		float[] fArExpressionValues;

		if (tree.hasChildren(node)) {

			int iNrNodes = tree.getChildren(node).size();
			int iNrElements = 0;
			float[][] fArTempValues;

			if (eClustererType == EClustererType.GENE_CLUSTERING) {
				IVirtualArray storageVA = set.getVA(idStorage);
				iNrElements = storageVA.size();
			}
			else {
				IVirtualArray contentVA = set.getVA(idContent);
				iNrElements = contentVA.size();
			}

			fArTempValues = new float[iNrNodes][iNrElements];

			int cnt = 0;

			for (ClusterNode current : tree.getChildren(node)) {
				fArTempValues[cnt] = determineExpressionValueRec(current, eClustererType);
				cnt++;
			}

			fArExpressionValues = new float[iNrElements];

			for (int i = 0; i < iNrElements; i++) {
				float means = 0;

				for (int nodes = 0; nodes < iNrNodes; nodes++) {
					means += fArTempValues[nodes][i];
				}
				fArExpressionValues[i] = means / iNrNodes;
			}
		}
		// no children --> leaf node
		else {

			if (eClustererType == EClustererType.GENE_CLUSTERING) {
				IVirtualArray storageVA = set.getVA(idStorage);
				fArExpressionValues = new float[storageVA.size()];

				int isto = 0;
				for (Integer iStorageIndex : storageVA) {
					fArExpressionValues[isto] =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, node.getClusterNr());
					isto++;
				}

			}
			else {
				IVirtualArray contentVA = set.getVA(idContent);
				fArExpressionValues = new float[contentVA.size()];

				int icon = 0;
				for (Integer iContentIndex : contentVA) {
					fArExpressionValues[icon] =
						set.get(node.getClusterNr()).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
					icon++;
				}
			}
		}
		float averageExpressionvalue = ClusterHelper.arithmeticMean(fArExpressionValues);
		float deviation = ClusterHelper.standardDeviation(fArExpressionValues, averageExpressionvalue);
		node.setAverageExpressionValue(averageExpressionvalue);
		node.setStandardDeviation(deviation);

		return fArExpressionValues;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given
	 * distance matrix.
	 * 
	 * @param set
	 * @return index of virtual array
	 */
	private Integer pmlcluster(EClustererType eClustererType) {

		int[] clusterid = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		// Setup a list specifying to which cluster a gene belongs
		for (int j = 0; j < iNrSamples; j++)
			clusterid[j] = j;

		// Arraylist holding clustered indexes
		ArrayList<Integer> AlIndexes = new ArrayList<Integer>();

		int j;

		ClosestPair pair = null;

		float[][] distmatrix;

		try {
			distmatrix = new float[iNrSamples][iNrSamples];
		}
		catch (OutOfMemoryError e) {
			return -1;
		}

		distmatrix = similarities.clone();

		int iPercentage = 1;

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Tree clustering of genes in progress"));
		else
			GeneralManager.get().getEventPublisher().triggerEvent(
				new RenameProgressBarEvent("Tree clustering of experiments in progress"));

		for (int n = iNrSamples; n > 1; n--) {

			if (bClusteringCanceled == false) {
				int tempPercentage = (int) ((float) (iNrSamples - n) / iNrSamples * 100);
				if (iPercentage == tempPercentage) {
					GeneralManager.get().getEventPublisher().triggerEvent(
						new ClusterProgressEvent(iPercentage, false));
					iPercentage++;
				}

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
				processEvents();
			}
			else {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
				return -2;
			}
		}

		// set cluster result in Set
		tree = new Tree<ClusterNode>();

		int random = (int) ((Math.random() * Integer.MAX_VALUE) + 1);

		ClusterNode node = new ClusterNode("Root", random, 0f, 0, true);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		determineExpressionValue(eClustererType);

		AlIndexes = getAl();

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 50 + iProgressBarOffsetValue, true));

		return clusteredVAId;
	}

	private ArrayList<Integer> traverse(ArrayList<Integer> indexes, ClusterNode node) {

		if (tree.hasChildren(node) == false) {
			indexes.add(node.getClusterNr());
		}
		else {
			for (ClusterNode current : tree.getChildren(node)) {
				traverse(indexes, current);
			}
		}

		return indexes;
	}

	private ArrayList<Integer> getAl() {

		ArrayList<Integer> indexes = new ArrayList<Integer>();
		traverse(indexes, tree.getRoot());

		return indexes;
	}

	HashMap<String, Integer> hashedNodeNames = new HashMap<String, Integer>();
	HashMap<String, Integer> duplicatedNodes = new HashMap<String, Integer>();

	/**
	 * Returns name of the node. Therefore we need an index of the gene/experiment
	 * 
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return name of the current node
	 */
	private String getNodeName(EClustererType eClustererType, int index) {
		String nodeName = null;

		IVirtualArray contentVA = set.getVA(idContent);
		IVirtualArray storageVA = set.getVA(idStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
				nodeName = GeneticIDMappingHelper.get().getShortNameFromExpressionIndex(contentVA.get(index));

				nodeName += " | ";
				nodeName +=
					GeneticIDMappingHelper.get().getRefSeqStringFromStorageIndex(contentVA.get(index));// +
				// 1);
			}
			else if (set.getSetType() == ESetType.UNSPECIFIED) {
				nodeName = "generalManager.getIDMappingManager().getID(" + contentVA.get(index) + " )";
				// generalManager.getIDMappingManager().getID(EMappingType.EXPRESSION_INDEX_2_UNSPECIFIED,
				// treeStructure[index].getLeft() + 1);
			}
			else {
				throw new IllegalStateException("Label extraction for " + set.getSetType()
					+ " not implemented yet!");
			}
		}
		else {
			nodeName = set.get(storageVA.get(index)).getLabel();
		}

		if (hashedNodeNames.containsKey(nodeName)) {
			int iNr = 1;
			if (duplicatedNodes.containsKey(nodeName)) {
				iNr = duplicatedNodes.get(nodeName);
				duplicatedNodes.put(nodeName, ++iNr);
			}
			else
				duplicatedNodes.put(nodeName, iNr);

			nodeName = nodeName + "__" + iNr;
		}
		else
			hashedNodeNames.put(nodeName, 1);

		return nodeName;
	}

	private int getNodeNr(EClustererType eClustererType, int index) {

		int nodeNr = 0;

		IVirtualArray contentVA = set.getVA(idContent);
		IVirtualArray storageVA = set.getVA(idStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			nodeNr = contentVA.get(index);
		}
		else {
			nodeNr = storageVA.get(index);
		}

		return nodeNr;
	}

	private void treeStructureToTree(ClusterNode node, Node[] treeStructure, int index,
		EClustererType eClustererType) {

		ClusterNode left = null;
		ClusterNode right = null;

		if (treeStructure[index].getLeft() >= 0) {

			String NodeName = getNodeName(eClustererType, treeStructure[index].getLeft());
			int NodeNr = getNodeNr(eClustererType, treeStructure[index].getLeft());

			left = new ClusterNode(NodeName, NodeNr, treeStructure[index].getCorrelation(), 0, false);

			left.setNrElements(1);

			tree.addChild(node, left);

		}
		else {
			int random = getNodeCounter();

			left =
				new ClusterNode("Node_" + (-(treeStructure[index].getLeft()) - 1), random,
					treeStructure[index].getCorrelation(), 0, false);
			tree.addChild(node, left);
			treeStructureToTree(left, treeStructure, -(treeStructure[index].getLeft()) - 1, eClustererType);
		}

		if (treeStructure[index].getRight() >= 0) {

			String NodeName = getNodeName(eClustererType, treeStructure[index].getRight());
			int NodeNr = getNodeNr(eClustererType, treeStructure[index].getRight());

			right = new ClusterNode(NodeName, NodeNr, treeStructure[index].getCorrelation(), 0, false);

			right.setNrElements(1);

			tree.addChild(node, right);

		}
		else {
			int random = getNodeCounter();

			right =
				new ClusterNode("Node_" + (-(treeStructure[index].getRight()) - 1), random,
					treeStructure[index].getCorrelation(), 0, false);
			tree.addChild(node, right);
			treeStructureToTree(right, treeStructure, -(treeStructure[index].getRight()) - 1, eClustererType);
		}

	}

	@Override
	public Integer getSortedVAId(ISet set, Integer idContent, Integer idStorage, ClusterState clusterState,
		int iProgressBarOffsetValue, int iProgressBarMultiplier) {

		Integer VAId = 0;

		eDistanceMeasure = clusterState.getDistanceMeasure();
		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;

		int iReturnValue = 0;

		iReturnValue = determineSimilarities(set, idContent, idStorage, clusterState.getClustererType());

		if (iReturnValue == -1) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -1;
		}
		else if (iReturnValue == -2) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return -2;
		}

		this.set = set;
		this.idContent = idContent;
		this.idStorage = idStorage;

		// VAId = pmlcluster(clusterState.getClustererType());
		VAId = palcluster(clusterState.getClustererType());

		return VAId;
	}
}
