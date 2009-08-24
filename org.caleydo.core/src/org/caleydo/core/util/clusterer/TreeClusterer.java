package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Tree clusterer
 * 
 * @author Bernhard Schlegl
 */
public class TreeClusterer
	extends AClusterer {

	/**
	 * Helper class needed in tree cluster algorithm.
	 * 
	 * @author Bernhard Schlegl
	 */
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
	private int iVAIdContent = 0;
	private int iVAIdStorage = 0;

	private float[][] similarities = null;

	private int iNrSamples = 0;

	private Tree<ClusterNode> tree;

	private EDistanceMeasure eDistanceMeasure;

	/**
	 * Each node in the tree needs an unique number. Because of this we need a node counter
	 */
	private int iNodeCounter = (int) Math.floor(Integer.MAX_VALUE / 2);

	// Hash maps needed for determine cluster names. The name of a cluster has to be unique.
	HashMap<String, Integer> hashedNodeNames = new HashMap<String, Integer>();
	HashMap<String, Integer> duplicatedNodes = new HashMap<String, Integer>();

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
	 * Calculates the similarity matrix for a given set and given VAs
	 * 
	 * @param set
	 * @param eClustererType
	 * @return in case of error a negative value will be returned.
	 */
	private int determineSimilarities(ISet set, EClustererType eClustererType) {

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		IDistanceMeasure distanceMeasure;

		if (eDistanceMeasure == EDistanceMeasure.MANHATTAHN_DISTANCE)
			distanceMeasure = new ManhattanDistance();
		else if (eDistanceMeasure == EDistanceMeasure.CHEBYSHEV_DISTANCE)
			distanceMeasure = new ChebyshevDistance();
		else if (eDistanceMeasure == EDistanceMeasure.PEARSON_CORRELATION)
			distanceMeasure = new PearsonCorrelation();
		else
			distanceMeasure = new EuclideanDistance();

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

	/**
	 * Helper function providing unique node numbers for all nodes in the tree.
	 * 
	 * @return number of current node
	 */
	private int getNodeCounter() {
		return iNodeCounter++;
	}

	/**
	 * Function normalizes similarities between 0 and 1
	 */
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

	/**
	 * Function looks for the next closest pair in the distance matrix.
	 * 
	 * @param n
	 *            current size of the similarity matrix
	 * @param distmatrix
	 *            the similarity matrix
	 * @return the closest pair
	 */
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
	 * The palcluster routine performs clustering using single linking on the given distance matrix.
	 * 
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private IVirtualArray pslcluster(EClustererType eClustererType) {

		int nnodes = iNrSamples - 1;
		int[] vector = new int[nnodes];
		float[] temp = new float[nnodes];
		int[] index = new int[iNrSamples];
		Node[] result = null;

		float[][] distmatrix;

		try {
			result = new Node[iNrSamples];
			distmatrix = new float[iNrSamples][iNrSamples];
		}
		catch (OutOfMemoryError e) {
			return null;
		}

		distmatrix = similarities.clone();

		for (int i = 0; i < iNrSamples; i++) {
			result[i] = new Node();
			result[i].setCorrelation(Float.MAX_VALUE);
			for (int j = 0; j < i; j++)
				temp[j] = distmatrix[i][j];
			for (int j = 0; j < i; j++) {
				int k = vector[j];
				if (result[j].getCorrelation() >= temp[j]) {
					if (result[j].getCorrelation() < temp[k])
						temp[k] = result[j].getCorrelation();
					result[j].setCorrelation(temp[j]);
					vector[j] = i;
				}
				else if (temp[j] < temp[k])
					temp[k] = temp[j];
			}
			for (int j = 0; j < i; j++) {
				if (result[j].getCorrelation() >= result[vector[j]].getCorrelation())
					vector[j] = i;
			}
		}

		for (int i = 0; i < nnodes; i++)
			result[i].setLeft(i);

		for (int i = 0; i < iNrSamples; i++)
			index[i] = i;

		for (int i = 0; i < nnodes; i++) {
			int j = result[i].getLeft();
			int k = vector[j];
			result[i].setLeft(index[j]);
			result[i].setRight(index[k]);
			index[k] = -i - 1;
		}

		Node[] result2 = new Node[nnodes];
		for (int i = 0; i < nnodes; i++)
			result2[i] = result[i];

		// set cluster result in Set
		tree = new Tree<ClusterNode>();

		ClusterNode node = new ClusterNode("Root", getNodeCounter(), 0f, 0, true);
		tree.setRootNode(node);
		treeStructureToTree(node, result2, result2.length - 1, eClustererType);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		determineExpressionValue(eClustererType);

		ArrayList<Integer> alIndices = new ArrayList<Integer>();
		alIndices = ClusterHelper.getAl(tree);

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		IVirtualArray virtualArray = null;
		if (eClustererType == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), alIndices);
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), alIndices);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 50 + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	/**
	 * The palcluster routine performs clustering using pairwise average linking on the given distance matrix.
	 * 
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private IVirtualArray palcluster(EClustererType eClustererType) {

		int[] clusterid = new int[iNrSamples];
		int[] number = new int[iNrSamples];
		Node[] result = new Node[iNrSamples - 1];

		for (int j = 0; j < iNrSamples; j++) {
			number[j] = 1;
			clusterid[j] = j;
		}

		int j;

		ClosestPair pair = null;

		ArrayList<Integer> alIndices = new ArrayList<Integer>();

		float[][] distmatrix;

		try {
			distmatrix = new float[iNrSamples][iNrSamples];
		}
		catch (OutOfMemoryError e) {
			return null;
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
				return null;
			}
		}

		// set cluster result in Set
		tree = new Tree<ClusterNode>();

		ClusterNode node = new ClusterNode("Root", getNodeCounter(), 0f, 0, true);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		determineExpressionValue(eClustererType);

		alIndices = ClusterHelper.getAl(tree);

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		IVirtualArray virtualArray = null;
		if (eClustererType == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), alIndices);
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), alIndices);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 50 + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	/**
	 * The function is responsible for calculating the expression value in each node of the tree. To handle
	 * this an other recursive function which does the whole work is called.
	 * 
	 * @param eClustererType
	 */
	private void determineExpressionValue(EClustererType eClustererType) {

		determineExpressionValueRec(tree.getRoot(), eClustererType);
	}

	/**
	 * Recursive function which determines the expression value in each node of the tree.
	 * 
	 * @param tree
	 * @param node
	 *            current node
	 * @return depth of the current node
	 */
	private float[] determineExpressionValueRec(ClusterNode node, EClustererType eClustererType) {

		float[] fArExpressionValues;

		if (tree.hasChildren(node)) {

			int iNrNodes = tree.getChildren(node).size();
			int iNrElements = 0;
			float[][] fArTempValues;

			if (eClustererType == EClustererType.GENE_CLUSTERING) {
				IVirtualArray storageVA = set.getVA(iVAIdStorage);
				iNrElements = storageVA.size();
			}
			else {
				IVirtualArray contentVA = set.getVA(iVAIdContent);
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
				IVirtualArray storageVA = set.getVA(iVAIdStorage);
				fArExpressionValues = new float[storageVA.size()];

				int isto = 0;
				for (Integer iStorageIndex : storageVA) {
					fArExpressionValues[isto] =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, node.getClusterNr());
					isto++;
				}

			}
			else {
				IVirtualArray contentVA = set.getVA(iVAIdContent);
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
		// Setting an float array for the representative element in each node causes a very big xml-file when
		// exporting the tree
		// node.setRepresentativeElement(fArExpressionValues);
		node.setStandardDeviation(deviation);

		return fArExpressionValues;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given
	 * distance matrix.
	 * 
	 * @param eClustererType
	 * @return virtual array with ordered indexes
	 */
	private IVirtualArray pmlcluster(EClustererType eClustererType) {

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
			return null;
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
				return null;
			}
		}

		// set cluster result in Set
		tree = new Tree<ClusterNode>();

		ClusterNode node = new ClusterNode("Root", getNodeCounter(), 0f, 0, true);
		tree.setRootNode(node);
		treeStructureToTree(node, result, result.length - 1, eClustererType);

		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);

		determineExpressionValue(eClustererType);

		AlIndexes = ClusterHelper.getAl(tree);

		if (eClustererType == EClustererType.GENE_CLUSTERING)
			set.setClusteredTreeGenes(tree);
		else
			set.setClusteredTreeExps(tree);

		IVirtualArray virtualArray = null;
		if (eClustererType == EClustererType.GENE_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), AlIndexes);
		else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING)
			virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), AlIndexes);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new ClusterProgressEvent(iProgressBarMultiplier * 50 + iProgressBarOffsetValue, true));

		return virtualArray;
	}

	/**
	 * Function returns the name of the current node. Therefore we need an index of the gene/experiment in the
	 * VA. To avoid problems with the tree all nodes in the tree must have unique names. Therefore we need to
	 * take care of two hash maps holding the currently used names and their frequency of occurrence.
	 * 
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return name of the current node
	 */
	private String getNodeName(EClustererType eClustererType, int index) {
		String nodeName = null;

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {

				// FIXME: Due to new mapping system, a mapping involving expression index can return a Set of
				// values, depending on the IDType that has been specified when loading expression data.
				// Possibly a different handling of the Set is required.
				Set<String> setGeneSymbols =
					GeneralManager.get().getIDMappingManager().getIDAsSet(EIDType.EXPRESSION_INDEX,
						EIDType.GENE_SYMBOL, contentVA.get(index));

				if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
					nodeName = (String) setGeneSymbols.toArray()[0];
				}
				if (nodeName == null || nodeName.equals(""))
					nodeName = "Unkonwn Gene";
				String refSeq = null;
				// FIXME: Due to new mapping system, a mapping involving expression index can return a Set of
				// values, depending on the IDType that has been specified when loading expression data.
				// Possibly a different handling of the Set is required.
				Set<String> setRefSeqIDs =
					GeneralManager.get().getIDMappingManager().getIDAsSet(EIDType.EXPRESSION_INDEX,
						EIDType.REFSEQ_MRNA, contentVA.get(index));

				if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
					refSeq = (String) setRefSeqIDs.toArray()[0];
				}

				nodeName += " | ";
				nodeName += (refSeq == null) ? ("Unknown") : (refSeq);
			}
			else if (set.getSetType() == ESetType.UNSPECIFIED) {
				nodeName = "generalManager.getIDMappingManager().getID(" + contentVA.get(index) + " )";
			}
			else {
				throw new IllegalStateException("Label extraction for " + set.getSetType()
					+ " not implemented yet!");
			}
		}
		else {
			nodeName = set.get(storageVA.get(index)).getLabel();
		}

		// check if current node name was already used. If yes we add signs to make it unique.
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

	/**
	 * Function returns the number of the current node. Therefore we need an index of the gene/experiment in
	 * the VA.
	 * 
	 * @param eClustererType
	 *            either gene or expression clustering
	 * @param index
	 *            index of the current node in the VA
	 * @return number of the current node
	 */
	private int getNodeNr(EClustererType eClustererType, int index) {

		int nodeNr = 0;

		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		if (eClustererType == EClustererType.GENE_CLUSTERING) {
			nodeNr = contentVA.get(index);
		}
		else {
			nodeNr = storageVA.get(index);
		}

		return nodeNr;
	}

	/**
	 * Recursive function responsible for mapping tree structure to a tree
	 * 
	 * @param node
	 *            current node
	 * @param treeStructure
	 * @param index
	 *            current index in the tree structure
	 * @param eClustererType
	 */
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
	public IVirtualArray getSortedVA(ISet set, ClusterState clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier) {

		IVirtualArray virtualArray = null;

		eDistanceMeasure = clusterState.getDistanceMeasure();
		this.iProgressBarMultiplier = iProgressBarMultiplier;
		this.iProgressBarOffsetValue = iProgressBarOffsetValue;
		this.iVAIdContent = clusterState.getContentVaId();
		this.iVAIdStorage = clusterState.getStorageVaId();

		int iReturnValue = 0;

		iReturnValue = determineSimilarities(set, clusterState.getClustererType());

		if (iReturnValue < 0) {
			GeneralManager.get().getEventPublisher().triggerEvent(new ClusterProgressEvent(100, true));
			return null;
		}

		this.set = set;

		if (clusterState.getTreeClustererAlgo() == ETreeClustererAlgo.COMPLETE_LINKAGE)
			virtualArray = pmlcluster(clusterState.getClustererType());
		else if (clusterState.getTreeClustererAlgo() == ETreeClustererAlgo.AVERAGE_LINKAGE)
			virtualArray = palcluster(clusterState.getClustererType());
		else
			virtualArray = pslcluster(clusterState.getClustererType());

		return virtualArray;
	}
}
