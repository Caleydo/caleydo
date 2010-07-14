package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.List;

public interface AbstractTreeNode {
	
	public float getAreaSize();
	
	public Color getAreaColor();
	
	public List<AbstractTreeNode> getChildren();
}
