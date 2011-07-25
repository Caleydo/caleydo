package org.caleydo.core.data.collection.table;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Fixme: consolidate this and DimensionData
 * 
 * @author Alexander Lex
 */
public class RecordData {
	RecordVirtualArray recordVA;
	/** indices of examples (cluster centers) */
	ArrayList<Integer> contentSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> contentClusterSizes;

	ClusterTree contentTree;

	boolean isPartitionallyClustered = false;

	IDType recordIDIdType;

	// public ContentData() {
	// }

	public RecordData(IDType recordIDType) {
		this.recordIDIdType = recordIDType;
	}

	public void setContentIDIdType(IDType recordIDIdType) {
		this.recordIDIdType = recordIDIdType;
	}

	public RecordVirtualArray getRecordVA() {
		return recordVA;
	}

	public void setRecordVA(RecordVirtualArray recordVA) {
		this.recordVA = recordVA;
	}

	public ArrayList<Integer> getContentSampleElements() {
		return contentSampleElements;
	}

	public void setRecordSampleElements(ArrayList<Integer> contentSampleElements) {
		this.contentSampleElements = contentSampleElements;
	}

	public ArrayList<Integer> getContentClusterSizes() {
		return contentClusterSizes;
	}

	public void setRecordClusterSizes(ArrayList<Integer> contentClusterSizes) {
		this.contentClusterSizes = contentClusterSizes;
	}

	public Tree<ClusterNode> getRecordTree() {
		return contentTree;
	}

	public void setRecordTree(ClusterTree contentTree) {
		this.contentTree = contentTree;
	}

	public void reset() {
		contentClusterSizes = null;
		contentSampleElements = null;
		if (contentTree != null)
			contentTree.destroy();
		contentTree = null;
	}

	/**
	 * Sets the delta to the virtual array and resets other related data (groups, trees) accordingly.
	 * 
	 * @param delta
	 */
	public void setVADelta(RecordVADelta delta) {
		recordVA.setDelta(delta);
		if (contentTree != null)
			contentTree.destroy();
		contentTree = null;
		recordVA.setGroupList(null);
	}

	public void finish() {
		// calculate the group list based on contentClusterSizes (for example for affinity propagation
		if (recordVA != null && contentClusterSizes != null && contentSampleElements != null) {
			isPartitionallyClustered = true;
			RecordGroupList contentGroupList = new RecordGroupList();

			int cnt = 0;
			// int iOffset = 0;
			contentTree = new ClusterTree(recordIDIdType);
			int clusterNr = 0;
			ClusterNode root = new ClusterNode(contentTree, "Root", clusterNr++, true, -1);
			contentTree.setRootNode(root);
			ClusterNode node;
			int from = 0;
			int to = 0;
			for (Integer clusterSize : contentClusterSizes) {

				node = new ClusterNode(contentTree, "Group: " + clusterNr, clusterNr++, true, -1);
				Group temp = new Group(clusterSize, contentSampleElements.get(cnt), node);
				contentTree.addChild(root, node);
				contentGroupList.append(temp);
				cnt++;
				// iOffset += iter;
				to += clusterSize;
				ClusterNode leaf;
				for (int vaIndex = from; vaIndex < to; vaIndex++) {
					Integer recordID = recordVA.get(vaIndex);
					leaf = new ClusterNode(contentTree, "Leaf: " + recordID, clusterNr++, true, recordID);
					contentTree.addChild(node, leaf);
				}
				from = to;

			}

			recordVA.setGroupList(contentGroupList);
		}
		// calculate the group list based on the tree's first level
		else if (recordVA != null && contentTree != null) {
			recordVA.buildNewGroupList(contentTree.getRoot().getChildren());
		}
	}

	public void updateVABasedOnSortingStrategy() {
//		ContentGroupList groupList = recordVA.getGroupList();

		recordVA = new RecordVirtualArray(DataTable.RECORD, contentTree.getRoot().getLeaveIds());
		recordVA.buildNewGroupList(contentTree.getRoot().getChildren());
//		recordVA.setGroupList(groupList);
	}

	public boolean isPartitionallyClustered() {
		return isPartitionallyClustered;
	}

}
