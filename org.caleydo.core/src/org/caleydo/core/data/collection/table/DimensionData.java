package org.caleydo.core.data.collection.table;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Class that summarizes all information around a dimensionVA and its tree. No field is initialized by default.
 * 
 * @author Alexander Lex
 */
public class DimensionData
	implements Cloneable {

	DimensionVirtualArray dimensionVA;

	/** indices of examples (cluster centers) */
	ArrayList<Integer> dimensionSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> dimensionClusterSizes;

	ClusterTree dimensionTree;

	boolean isDefaultTree = true;

	/** Root node for dimension hierarchy which is only set in subDataTables */
	private ClusterNode dimensionTreeRoot = null;

	public DimensionVirtualArray getDimensionVA() {
		return dimensionVA;
	}

	public void setDimensionVA(DimensionVirtualArray dimensionVA) {
		this.dimensionVA = dimensionVA;
	}

	public ArrayList<Integer> getDimensionClusterSizes() {
		return dimensionClusterSizes;
	}

	public void setDimensionClusterSizes(ArrayList<Integer> dimensionClusterSizes) {
		this.dimensionClusterSizes = dimensionClusterSizes;
	}

	public ArrayList<Integer> getDimensionSampleElements() {
		return dimensionSampleElements;
	}

	public void setDimensionSampleElements(ArrayList<Integer> dimensionSampleElements) {
		this.dimensionSampleElements = dimensionSampleElements;
	}

	public ClusterTree getDimensionTree() {
		return dimensionTree;
	}

	public void setDimensionTree(ClusterTree dimensionTree) {
		this.dimensionTree = dimensionTree;
	}

	public void setDefaultTree(boolean isDefaultTree) {
		this.isDefaultTree = isDefaultTree;
	}

	public boolean isDefaultTree() {
		return isDefaultTree;
	}

	public ClusterNode getDimensionTreeRoot() {
		if (dimensionTreeRoot == null)
			return dimensionTree.getRoot();
		return dimensionTreeRoot;
	}

	public void setDimensionTreeRoot(ClusterNode dimensionTreeRoot) {
		this.dimensionTreeRoot = dimensionTreeRoot;
	}

	public void finish() {

		if (dimensionVA != null && dimensionClusterSizes != null && dimensionSampleElements != null) {
			DimensionGroupList dimensionGroupList = new DimensionGroupList();

			int cnt = 0;
			int iOffset = 0;
			for (Integer size : dimensionClusterSizes) {

				Group temp = new Group(size, dimensionSampleElements.get(cnt));
				dimensionGroupList.append(temp);
				cnt++;
				iOffset += size;
			}
			dimensionVA.setGroupList(dimensionGroupList);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public DimensionData clone() {
		DimensionData clone = null;
		try {
			clone = (DimensionData) super.clone();
		}
		catch (CloneNotSupportedException e) {
			// TODO: handle exception
		}
		if (dimensionClusterSizes != null)
			clone.dimensionClusterSizes = (ArrayList<Integer>) dimensionClusterSizes.clone();
		if (dimensionSampleElements != null)
			clone.dimensionSampleElements = (ArrayList<Integer>) dimensionSampleElements.clone();
		clone.dimensionVA = dimensionVA.clone();
		// FIXME this is a bad hack since it is not a clone
		clone.dimensionTree = dimensionTree;

		return clone;

	}

	public void reset() {
		dimensionVA = null;
		dimensionClusterSizes = null;
		dimensionSampleElements = null;
		dimensionTree = null;
		dimensionTreeRoot = null;
	}
}
