package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;

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
		if (contentVA != null && contentClusterSizes != null && contentSampleElements != null) {
			ContentGroupList contentGroupList = new ContentGroupList();

			int cnt = 0;
			int iOffset = 0;
			for (Integer iter : contentClusterSizes) {

				Group temp = new Group(iter, false, contentSampleElements.get(cnt), SelectionType.NORMAL);
				contentGroupList.append(temp);
				cnt++;
				iOffset += iter;
			}
		}
	}

}
