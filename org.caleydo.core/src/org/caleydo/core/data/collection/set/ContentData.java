package org.caleydo.core.data.collection.set;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.group.Group;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Fixme: consolidate this and StorageData
 * 
 * @author Alexander Lex
 */
public class ContentData {
	ContentVirtualArray contentVA;
	/** indices of examples (cluster centers) */
	ArrayList<Integer> contentSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> contentClusterSizes;

	ClusterTree contentTree;

	boolean isPartitionallyClustered = false;

	IDType contentIDIdType;

	// public ContentData() {
	// }

	public ContentData(IDType contentIDType) {
		this.contentIDIdType = contentIDType;
	}

	public void setContentIDIdType(IDType contentIDIdType) {
		this.contentIDIdType = contentIDIdType;
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	public ArrayList<Integer> getContentSampleElements() {
		return contentSampleElements;
	}

	public void setContentSampleElements(ArrayList<Integer> contentSampleElements) {
		this.contentSampleElements = contentSampleElements;
	}

	public ArrayList<Integer> getContentClusterSizes() {
		return contentClusterSizes;
	}

	public void setContentClusterSizes(ArrayList<Integer> contentClusterSizes) {
		this.contentClusterSizes = contentClusterSizes;
	}

	public Tree<ClusterNode> getContentTree() {
		return contentTree;
	}

	public void setContentTree(ClusterTree contentTree) {
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
	public void setVADelta(ContentVADelta delta) {
		contentVA.setDelta(delta);
		if (contentTree != null)
			contentTree.destroy();
		contentTree = null;
		contentVA.setGroupList(null);
	}

	public void finish() {
		// calculate the group list based on contentClusterSizes (for example for affinity propagation
		if (contentVA != null && contentClusterSizes != null && contentSampleElements != null) {
			isPartitionallyClustered = true;
			ContentGroupList contentGroupList = new ContentGroupList();

			int cnt = 0;
			// int iOffset = 0;
			contentTree = new ClusterTree(contentIDIdType);
			contentTree.setUseDefaultComparator(false);
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
					Integer contentID = contentVA.get(vaIndex);
					leaf = new ClusterNode(contentTree, "Leaf: " + contentID, clusterNr++, true, contentID);
					contentTree.addChild(node, leaf);
				}
				from = to;

			}

			contentVA.setGroupList(contentGroupList);
		}
		// calculate the group list based on the tree's first level
		else if (contentVA != null && contentTree != null) {
			contentVA.buildNewGroupList(contentTree.getRoot().getChildren());
		}
		contentTree.setUseDefaultComparator(false);
	}

	public boolean isPartitionallyClustered() {
		return isPartitionallyClustered;
	}

}
