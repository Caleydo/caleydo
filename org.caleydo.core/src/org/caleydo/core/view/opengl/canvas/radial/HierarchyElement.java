package org.caleydo.core.view.opengl.canvas.radial;

import java.util.Vector;


public abstract class HierarchyElement {
	
	protected int iElementID;
	protected int iHierarchyDepth;
	protected Vector<HierarchyElement> vecChildren;
	protected HierarchyElement heParent;
	
	public HierarchyElement(int iID) {
		iElementID = iID;
		iHierarchyDepth = 1;
		vecChildren = new Vector<HierarchyElement>();
		heParent = null;
	}
	
	public void addChild(HierarchyElement heChild) {
		vecChildren.add(heChild);
		heChild.setParent(this);
		increaseHierarchyDepth(heChild.getHierarchyDepth());
	}
	
	public int getHierarchyDepth() {
		return iHierarchyDepth;
	}
	
	private void setParent(HierarchyElement heParent) {
		this.heParent = heParent;
	}
	
	private void increaseHierarchyDepth(int iChildDepth) {
		if(iHierarchyDepth <= iChildDepth) {
			iHierarchyDepth = iChildDepth + 1;
			if(heParent != null)
				heParent.increaseHierarchyDepth(iHierarchyDepth);
		}
	}
}
