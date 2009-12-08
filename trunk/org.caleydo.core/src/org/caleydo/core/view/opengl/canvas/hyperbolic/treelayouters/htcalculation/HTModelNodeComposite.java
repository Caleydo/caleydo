package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;



public class HTModelNodeComposite
    extends HTModelNode {

    private ArrayList<HTModelNode> children     = null; // children of this node

    private float globalWeight = 0.0f;  // sum of children weight



    HTModelNodeComposite(Tree<IDrawAbleNode> tree, IDrawAbleNode node, HTModel model) {
        this(tree, node, null, model);
    }

 
    HTModelNodeComposite(Tree<IDrawAbleNode> tree, IDrawAbleNode node, HTModelNodeComposite parent, HTModel model) {
        super(tree, node, parent, model);
        this.children = new ArrayList<HTModelNode>();

        
        for (IDrawAbleNode childNode : tree.getChildren(node)) {
        HTModelNode child;
            if (!tree.hasChildren(childNode)) {
                child = new HTModelNode(tree, childNode, this, model);
            } else {
                child = new HTModelNodeComposite(tree, childNode, this, model);
            }
            addChild(child);
        }
        
  
        computeWeight();
    }


 
    private void computeWeight() {
        HTModelNode child = null;
         
        for (Iterator i = children(); i.hasNext(); ) {
            child = (HTModelNode) i.next();
            globalWeight += child.getWeight();
        } 
        if (globalWeight != 0.0) {
            weight += Math.log(globalWeight);
        }
    }


 
    Iterator children() {
        return this.children.iterator();
    }

 
    void addChild(HTModelNode child) {
        children.add(child);
    }


    boolean isLeaf() {
        return false;
    }


 
   void layout(float angle, float width, float length) {
        super.layout(angle, width, length);   

        if (parent != null) {

            HTCoordE a = new HTCoordE((float)Math.cos(angle), (float)Math.sin(angle));
            HTCoordE nz = new HTCoordE(- z.x, - z.y);
            a.translate(parent.getCoordinates());
            a.translate(nz);
            angle = a.arg();


            float c = (float)Math.cos(width);
            float A = (1.0f + length * length);
            float B = 2.0f * length;
            width = (float)Math.acos((A * c - B) / (A - B * c));//* model.getScalingFactor() ;
        }

        HTModelNode child = null;
//        HTCoordE dump = new HTCoordE();

        int nbrChild = children.size();
        float l1 = (0.95f - model.getLength());
        float l2 = (float)Math.cos((20.0f * Math.PI) / (2.0f * nbrChild + 38.0f)); 
        length = (model.getLength() + (l1 * l2));// * model.getScalingFactor();

        float startAngle = angle - width;


        for (Iterator i = children(); i.hasNext(); ) {
            child = (HTModelNode) i.next();
            
            float percent = child.getWeight() / globalWeight;
            float childWidth = width * percent;
            float childAngle = startAngle + childWidth;
            child.layout(childAngle, childWidth, length);
            startAngle += 2.0f * childWidth;
        }

    }


}

