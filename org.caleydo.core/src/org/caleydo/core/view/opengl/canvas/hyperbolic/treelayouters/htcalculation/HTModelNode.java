package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation;


import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;


public class HTModelNode {
    protected HTModel model = null; // tree model
    protected HTModelNodeComposite parent = null; // parent data
    protected HTCoordE z = null; // Euclidian coordinates
    protected float weight = 1.0f;  // part of space taken by this data
    private TreePath2<IDrawAbleNode> dataNodePath = null;
//    private Tree<IDrawAbleNode> tree = null;
    private IDrawAbleNode node = null;

 
    HTModelNode(Tree<IDrawAbleNode> tree, IDrawAbleNode node, HTModel model) {
        this(tree, node, null, model);
    }

  
    HTModelNode(Tree<IDrawAbleNode> tree, IDrawAbleNode data, HTModelNodeComposite parent, HTModel model) {
//        this.tree = tree;
    	this.node = data;
    	this.dataNodePath = (parent == null) ? new TreePath2<IDrawAbleNode>(data) : parent.getDataNodePath().pathByAddingChild(data);
        this.parent = parent;
        this.model = model;
        model.incrementNumberOfNodes();

        z = new HTCoordE();
    }

 
    IDrawAbleNode getNode() {
        return dataNodePath.getLastPathComponent();
    }

    public TreePath2<IDrawAbleNode> getDataNodePath() {
        return dataNodePath;
    }

 
    float getWeight() {
        return weight;
    }



    HTModelNodeComposite getParent() {
        return parent;
    }

 
    boolean isLeaf() {
        return true;
    }


    HTCoordE getCoordinates() {
        return z;
    }



    void layoutHyperbolicTree() {
//        this.layout(0.0, Math.PI, model.getLength());
    	this.layout(0.0f, (float)Math.PI, model.getLength());
    	node.setXCoord(0.0f);
    	node.setYCoord(0.0f);
    }


    void layout(float angle, float width, float length) {

        if (parent == null) {
            return;
        }

        HTCoordE zp = parent.getCoordinates();

 
        z.x = length * (float)Math.cos(angle);
        z.y = length * (float)Math.sin(angle);
//        
//        z.x = length * Math.cos(angle) * model.getScalingFactor();
//        z.y = length * Math.sin(angle) * model.getScalingFactor();


        z.translate(zp);
        
//        node.setXCoord((float)z.x * 3.0f);
//        node.setYCoord((float)z.y * 3.0f);
        node.setXCoord((float)z.x);
        node.setYCoord((float)z.y);
    }


 
}

