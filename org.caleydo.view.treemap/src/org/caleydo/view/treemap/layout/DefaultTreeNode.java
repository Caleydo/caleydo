package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.ArrayList;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.clusterer.ClusterNode;

public class DefaultTreeNode extends ATreeMapNode {
	
	public static ATreeMapNode createSampleTree(){
		DefaultTree tree = new DefaultTree();
		
		ArrayList<ATreeMapNode> children;
		DefaultTreeNode node;
		
		children = new ArrayList<ATreeMapNode>();
		children.add(new DefaultTreeNode(tree, 1.0/12, Color.ORANGE, new ArrayList<ATreeMapNode>(),"3.1",31));
		children.add(new DefaultTreeNode(tree, 1.0/12, Color.MAGENTA, new ArrayList<ATreeMapNode>(),"3.2",32));
		
		node=new DefaultTreeNode(tree, 1.0/6, null, children);
		children = new ArrayList<ATreeMapNode>();
		children.add(node);
		children.add(new DefaultTreeNode(tree, 1.0/6, Color.GRAY, new ArrayList<ATreeMapNode>(),"2.1",21));
		node = new DefaultTreeNode(tree, 1.0/3, null, children);
		
		ArrayList<ATreeMapNode> children2 = new ArrayList<ATreeMapNode>();
		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.RED, new ArrayList<ATreeMapNode>(),"2.2",22));
		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.GREEN, new ArrayList<ATreeMapNode>(),"2.3",23));
		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.BLUE, new ArrayList<ATreeMapNode>(),"2.4",24));
		
		ArrayList<ATreeMapNode> children3 = new ArrayList<ATreeMapNode>();
		children3.add(new DefaultTreeNode(tree, 1.0/3, Color.CYAN, new ArrayList<ATreeMapNode>(),"1.1",11));
		children3.add(node);
		children3.add(new DefaultTreeNode(tree, 1.0/3, null, children2));
		
		
		return new DefaultTreeNode(tree, 1, null, children3);
	}

	float size;
	Color color;
	ArrayList<ATreeMapNode> children;
	String label="";
	int id;
	
	ClusterNode clusterNode;
	
	
	public DefaultTreeNode(AbstractTree root, double size, Color color, ArrayList<ATreeMapNode> children, String label, int id){
		super(root);
		this.size=(float) size;
		this.color=color;
		this.children=children;
		this.label=label;
		this.id=id;
		
	}
	
	public DefaultTreeNode(AbstractTree root, double size, Color color, ArrayList<ATreeMapNode> children){
		this(root, size, color, children, "",0);
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

	@Override
	public int getPickingID() {
		// TODO Auto-generated method stub
		return id;
	}

}
