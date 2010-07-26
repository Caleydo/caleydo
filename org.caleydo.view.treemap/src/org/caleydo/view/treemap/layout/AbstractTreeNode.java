package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.List;

public abstract class AbstractTreeNode {
	
	private float minX, minY, maxX, maxY;
	
	public float getSize(){
		List<AbstractTreeNode> list = getChildren();
		if(list==null||list.size()==0)
			return getSizeAttribute();
		float size=0;
		for(AbstractTreeNode node : list){
			size+=node.getSize();
		}
		return size;
	}
	
	public abstract Color getColorAttribute();
	
	public abstract float getSizeAttribute();
	
	public abstract List<AbstractTreeNode> getChildren();
	
	public abstract String getLabel();
	

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
