package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;

public class PoincareNode extends AHierarchyElement<PoincareNode> {

	private double distanceFromOrigin;
	private Point2D.Double position;
	private Point2D.Double projectedPosition;
	private PoincareNode openLink;
	private boolean linked = false;
	private int levelOfDetail = 0;
	
	
	
	String nodeName;
	int iComparableValue;
	public boolean highLighted=false;

	public PoincareNode(Tree<PoincareNode> tree, String nodeName,
			int iComparableValue) {
		super(tree);
		
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
		this.id = iComparableValue;
		
		
		                             
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

	public boolean isLinked() {
		return linked;
	}

	public void setDistanceFromOrigin(double distanceFromOrigin) {
		this.distanceFromOrigin = distanceFromOrigin;
	}

	public double getDistanceFromOrigin() {
		return distanceFromOrigin;
	}

	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	public Point2D.Double getPosition() {
		return position;
	}

	public void setProjectedPosition(Point2D.Double projectedPosition) {
		this.projectedPosition = projectedPosition;
	}

	public Point2D.Double getProjectedPosition() {
		return projectedPosition;
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

}
