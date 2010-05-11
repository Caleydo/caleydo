package org.caleydo.view.datawindows;


import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;

public class PoincareNode extends AHierarchyElement<PoincareNode> {

	private float distanceFromOrigin;
	private float[] position;
	private float[] zoomedPosition;
	private float childrenAngleOffset;
	private PoincareNode openLink;
	private boolean linked = false;
	private int levelOfDetail = 0;
	
	public boolean markedToRemove = false;
	public boolean nonExistent = false;
	
	
	String nodeName;
	int iComparableValue;
	public boolean highLighted=false;

	public PoincareNode(Tree<PoincareNode> tree, String nodeName,
			int iComparableValue) {
		super(tree);
		
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
		this.id = iComparableValue;
		
		this.position = new float[2];
		                             
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

	public boolean isLinked() {
		return linked;
	}

	public void setDistanceFromOrigin(float distanceFromOrigin) {
		this.distanceFromOrigin = distanceFromOrigin;
	}

	public float getDistanceFromOrigin() {
		return distanceFromOrigin;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getPosition() {
		return position;
	}

	public void setOpenLink(PoincareNode openLink) {
		this.openLink = openLink;
	}

	public PoincareNode getOpenLink() {
		return openLink;
	}

	public void setLevelOfDetail(int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	public int getLevelOfDetail() {
		return levelOfDetail;
	}

	public void setZoomedPosition(float[] zoomedPosition) {
		this.zoomedPosition = zoomedPosition;
	}

	public float[] getZoomedPosition() {
		return zoomedPosition;
	}

	public void setChildrenAngleOffset(float childrenAngleOffset) {
		this.childrenAngleOffset = childrenAngleOffset;
	}

	public float getChildrenAngleOffset() {
		return childrenAngleOffset;
	}

}
