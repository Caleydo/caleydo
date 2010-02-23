package org.caleydo.view.datawindows;



import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.DefaultNode;
import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsDisk extends PoincareDisk{


    //Tree<PoincareNode> tree;

	
	
	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);
		
	}

	
	
	public void renderTree(GL gl, Point2D.Double offset){
	
		
		
		PoincareNode root=getTree().getRoot();
		
	    renderNode(root,gl);
	  
		
	}

	
	public boolean renderNode(PoincareNode node,GL gl){
	   
		drawNode(node, gl);
		if (node.getChildren()==null){
			return false;
		}
		
		ArrayList<PoincareNode> children = node.getChildren();
	    
		
	   for(int i=0; i<0; i++){
		   renderNode(children.get(i),gl);
	   }
		
	    return true;
	}
	
    public void drawNode(PoincareNode node,GL gl){
	  drawCircle(gl,0.05f,node.getProjectedPosition().getX()+2.5f,node.getProjectedPosition().getY()+2.5f);
	 // System.out.println("node drawn at"+node.getProjectedPosition().getX()+2.5f+"|"+node.getProjectedPosition().getY()+2.5f);
	}
	
	public void drawLine() {
		
	}
	
	public void drawBackground(){
		
	}
	
	private void drawCircle(GL gl,double radius,double k, double h) {
	    //code from http://www.swiftless.com/tutorials/opengl/circle.html //20.2.2010
		double circleX=0;
		double circleY=0;
		double i=0;
		
		
		gl.glBegin(GL.GL_LINES);
	    for (double counter = 0; counter < 360; counter++)
	    {
	    i=counter*Math.PI/180;
	   
	    circleX = radius * Math.cos(i);
	    circleY = radius * Math.sin(i) ;
	    gl.glVertex3d(circleX + k,circleY + h,0);
	    
	    circleX = radius * Math.cos(i + Math.PI/180) ;
	    circleY = radius * Math.sin(i + Math.PI/180) ;
	    gl.glVertex3d(circleX + k,circleY + h,0);
	    }
	    gl.glEnd();
	}
	

}
