package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.ArrayList;

public class DefaultTreeNode extends ATreeMapNode {

	float size;
	Color color;
	ArrayList<ATreeMapNode> children;
	String label="";
	
	
	public DefaultTreeNode(AbstractTree root, double size, Color color, ArrayList<ATreeMapNode> children, String label){
		super(root);
		this.size=(float) size;
		this.color=color;
		this.children=children;
		this.label=label;
	}
	
	public DefaultTreeNode(AbstractTree root, double size, Color color, ArrayList<ATreeMapNode> children){
		this(root, size, color, children, "");
	}
	
	@Override
	public float getSizeAttribute() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public Color getColorAttribute() {
		// TODO Auto-generated method stub
		return color;
	}

	@Override
	public ArrayList<ATreeMapNode> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}
	
	public String toString(){
		return label==null?"":label;
	}

}
