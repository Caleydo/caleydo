package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;

/**
 * Fixme: consolidate this and StorageData
 * 
 * @author alexsb
 */
public class ContentData {
	ContentVirtualArray contentVA;
	/** indices of examples (cluster centers) */
	ArrayList<Integer> contentSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> contentClusterSizes;

	Tree<ClusterNode> contentTree;

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;

	}

	public ArrayList<Integer> getContentSampleElements() {
		return contentSampleElements;
	}

	public ArrayList<Integer> getContentClusterSizes() {
		return contentClusterSizes;
	}

	public Tree<ClusterNode> getContentTree() {
		return contentTree;
	}

	void finish() {
		// calculate the group list based on contentClusterSizes (for example for affinity propagation
		if (contentVA != null && contentClusterSizes != null && contentSampleElements != null) {
			ContentGroupList contentGroupList = new ContentGroupList();

			int cnt = 0;
			// int iOffset = 0;
			contentTree = new Tree<ClusterNode>();
			contentTree.setUseDefaultComparator(false);
			int clusterNr = 0;
			ClusterNode root = new ClusterNode(contentTree, "Root", clusterNr++, true, -1);
			contentTree.setRootNode(root);
			ClusterNode node;
			int from = 0;
			int to = 0;
			for (Integer clusterSize : contentClusterSizes) {

				node = new ClusterNode(contentTree, "Group: " + clusterNr, clusterNr++, true, -1);
				Group temp =
					new Group(clusterSize, false, contentSampleElements.get(cnt), SelectionType.NORMAL, node);
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
	}

}
