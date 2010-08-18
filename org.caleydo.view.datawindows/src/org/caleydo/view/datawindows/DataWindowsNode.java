package org.caleydo.view.datawindows;

import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsNode extends PoincareNode {

	public DataWindowsNode(Tree<PoincareNode> tree, String nodeName, int iComparableValue) {
		super(tree, nodeName, iComparableValue);
		// TODO Auto-generated constructor stub
	}

	private boolean highLighted;
	private int levelOfDetail;

	public void setHighLighted(boolean highLighted) {
		this.highLighted = highLighted;
	}

	public boolean isHighLighted() {
		return highLighted;
	}

	@Override
	public void setLevelOfDetail(int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	@Override
	public int getLevelOfDetail() {
		return levelOfDetail;
	}

}
