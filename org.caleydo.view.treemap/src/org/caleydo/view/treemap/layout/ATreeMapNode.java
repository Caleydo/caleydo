package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.util.clusterer.ClusterNode;

// TODO make a AHierarchyElement, rename to ATreeMapNode
public abstract class ATreeMapNode extends AHierarchyElement<ATreeMapNode>{
	
	private AbstractTree root;
	private float minX, minY, maxX, maxY;
	
	public ATreeMapNode(AbstractTree root) {
		// TODO Auto-generated constructor stub
		this.root=root;
	}
	
	public float getSize(){
		List<ATreeMapNode> list = getChildren();
		if(list==null||list.size()==0)
			return getSizeAttribute();
		float size=0;
		for(ATreeMapNode node : list){
			size+=node.getSize();
		}
		return size;
	}
	
	public abstract Color getColorAttribute();
	
	public abstract float getSizeAttribute();
	
	public abstract ArrayList<ATreeMapNode> getChildren();
	
	public abstract String getLabel();
	

	public AbstractTree getRoot(){
		return root;
	}
	
	public float getMinX() {
		return minX;
	}

	public void setMinX(float minX) {
		this.minX = minX;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY(float minY) {
		this.minY = minY;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}
}
