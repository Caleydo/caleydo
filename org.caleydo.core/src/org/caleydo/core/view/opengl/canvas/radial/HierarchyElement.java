package org.caleydo.core.view.opengl.canvas.radial;

import java.util.Vector;

import org.caleydo.core.data.graph.tree.Tree;

public abstract class HierarchyElement<Node extends Comparable<Node>> {

	protected Tree<Node> partialDiscTree;

	protected int iElementID;
	protected int iHierarchyDepth;
	protected Vector<HierarchyElement<Node>> vecChildren;
	protected Node parent;

	public HierarchyElement(int iID) {
		iElementID = iID;
		iHierarchyDepth = 1;
		vecChildren = new Vector<HierarchyElement<Node>>();

	}

	public void addChild(HierarchyElement<Node> heChild) {
		vecChildren.add(heChild);
		increaseHierarchyDepth(heChild.getHierarchyDepth());
	}

	public int getHierarchyDepth() {
		return iHierarchyDepth;
	}

	private void increaseHierarchyDepth(int iChildDepth) {
		if (iHierarchyDepth <= iChildDepth) {
			iHierarchyDepth = iChildDepth + 1;
		}
	}

	public Node getMyParent() {
		return parent;
	}

	// public void calculateHierarchyLevels(int iLevel) {
	// // iHierarchyLevel = iLevel;
	// // ArrayList<PartialDisc> alChildren = partialDiscTree.getChildren(this);
	// //
	// if(alChildren == null) {
	// return;
	// }
	//		
	// for(PartialDisc pdChild : alChildren) {
	// pdChild.calculateHierarchyLevels(iLevel + 1);
	// }
	// }
}
