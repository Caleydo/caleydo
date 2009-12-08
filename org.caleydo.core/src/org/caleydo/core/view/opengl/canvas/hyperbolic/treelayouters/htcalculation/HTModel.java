package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;


public class HTModel {

    private HTModelNode root   = null; // the root of the tree's model 

   // private float      length = 0.3;  // distance between node and children
    private float      length = 0.1f;  // distance between node and children
    
//    protected float scalingfactor = 0.2f;
    // END PATCH Increase density of diagram
    private int         nodes  = 0;    // number of nodes
    
    private Tree<IDrawAbleNode> tree = null;
    private IDrawAbleNode rootNode = null;

//    private NodeInfo info;


 
//    HTModel(ADrawAbleNode root, NodeInfo info) {
    	public HTModel(Tree<IDrawAbleNode> tree, IDrawAbleNode rootNode) {
//        this.info = info;
    		
    		this.tree = tree;
    		this.rootNode = rootNode;
    		if (!tree.hasChildren(rootNode)) {
//        if (!root.getAllowsChildren()) {
            this.root = new HTModelNode(tree, rootNode, this);

        } else {
            this.root = new HTModelNodeComposite(tree, rootNode, this);
        }
//        info.init(root);
        this.root.layoutHyperbolicTree();
    }


    HTModelNode getRoot() {
        return root;
    }


    float getLength() {
        return length;
    }



    void incrementNumberOfNodes() {
        nodes++;
    }
    

    int getNumberOfNodes() {
        return nodes;
    }
    
//    public float getScalingFactor(){
//    	return scalingfactor;
//    }

}

