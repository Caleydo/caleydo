package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.IVirtualArray;

public class TreeClusterer {

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
	public void determineSimilarities(ISet set, Integer iVAIdContent, Integer iVAIdStorage) {
		IVirtualArray contentVA = set.getVA(iVAIdContent);
		IVirtualArray storageVA = set.getVA(iVAIdStorage);

		float[] dArInstance1 = new float[storageVA.size()];
		float[] dArInstance2 = new float[storageVA.size()];

		int icnt1 = 0, icnt2 = 0, isto = 0;

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

					similarities[icnt1][icnt2] = ClusterHelper.euclideanDistance(dArInstance1, dArInstance2);
				}
				icnt2++;
			}
			icnt1++;
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
	public Integer palcluster(Set set) {

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

			// result[iNrSamples-n].distance = find_closest_pair(n, distmatrix, &is, &js);

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

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		set.setTreeStructure(result);

		return clusteredVAId;
	}

	/**
	 * The pmlcluster routine performs clustering using pairwise maximum- (complete-) linking on the given
	 * distance matrix.
	 * 
	 * @param set
	 * @return index of virtual array
	 */
	public Integer pmlcluster(Set set) {

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

		int nNodes = iNrSamples - 1;

		double[] order = new double[iNrSamples];

		for (int i = 0; i < order.length; i++)
			order[i] = i;

		double[] nodeorder = new double[nNodes];
		int[] nodecounts = new int[nNodes];

		for (int i = 0; i < nNodes; i++) {
			int min1 = result[i].getLeft();
			int min2 = result[i].getRight();
			// min1 and min2 are the elements that are to be joined
			double order1;
			double order2;
			int counts1;
			int counts2;
			if (min1 < 0) {
				int index1 = -min1 - 1;
				order1 = nodeorder[index1];
				counts1 = nodecounts[index1];
				result[i].setCorrelation(Math
					.max(result[i].getCorrelation(), result[index1].getCorrelation()));
			}
			else {
				order1 = order[min1];
				counts1 = 1;
			}
			if (min2 < 0) {
				int index2 = -min2 - 1;
				order2 = nodeorder[index2];
				counts2 = nodecounts[index2];
				result[i].setCorrelation(Math
					.max(result[i].getCorrelation(), result[index2].getCorrelation()));
			}
			else {
				order2 = order[min2];
				counts2 = 1;
			}

			nodecounts[i] = counts1 + counts2;
			nodeorder[i] = (counts1 * order1 + counts2 * order2) / (counts1 + counts2);
		}

		AlIndexes = TreeSort(nNodes, order, nodeorder, nodecounts, result);

		// for (int i = 0; i < result.length; i++) {
		// if (result[i].getLeft() >= 0)
		// AlIndexes.add(result[i].getLeft());
		// if (result[i].getRight() >= 0)
		// AlIndexes.add(result[i].getRight());
		// }

		Integer clusteredVAId = set.createStorageVA(AlIndexes);

		set.setTreeStructure(result);

		return clusteredVAId;
	}

	public ArrayList<Integer> TreeSort(int nNodes, double[] order, double[] nodeorder, int[] nodecounts,
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

		// for (i = 0; i < tree.length; i++) {
		// if (tree[i].getLeft() >= 0)
		// indexes.add(tree[i].getLeft());
		// if (tree[i].getRight() >= 0)
		// indexes.add(tree[i].getRight());
		// }

		for (i = 0; i < iNrSamples; i++)
			indexes.add(i);

		sort(nElements, neworder, indexes);

		return indexes;
	}

	public void sort(int n, double data[], ArrayList<Integer> indexes) {

		int f, i;
		double temp;

		// for (int z = 0; z < indexes.size(); z++) {
		// System.out.print(indexes.get(z) + " ");
		// }
		// System.out.println("#: " + indexes.size());
		// for (int z = 0; z < indexes.size(); z++) {
		// if (indexes.contains(z) == false) {
		// System.out.println(z + "nicht enthalten");
		// }
		// }

		for (f = 1; f < n; f++) {
			if (data[f] > data[f - 1])
				continue;
			temp = data[f];
			i = f - 1;
			while ((i >= 0) && (data[i] > temp)) {
				data[i + 1] = data[i];
				indexes.set(i + 1, indexes.get(i));
				i--;
			}
			data[i + 1] = temp;
			indexes.set(i + 1, f);
		}

		// for (int z = 0; z < indexes.size(); z++) {
		// System.out.print(indexes.get(z) + " ");
		// }
		// System.out.println("#: " + indexes.size());
		// for (int z = 0; z < indexes.size(); z++) {
		// if (indexes.contains(z) == false) {
		// System.out.println(z + "nicht enthalten");
		// }
		// }

	}
}
