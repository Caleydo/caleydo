/**
 * 
 */
package cerberus.view.swing.graph.visitor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import javax.media.opengl.GL;

import cerberus.view.swing.graph.NodeAttributes;
import cerberus.view.swing.graph.NodeInterface;


/**
 * @author java
 *
 */
public class NodeVisitorRenderer {

	protected float fz = 0.0f;
	
	/**
	 * 
	 */
	public NodeVisitorRenderer() {
		
	}
	
	public void renderGraphGL( GL gl ,final NodeInterface rootNode ) {
				
		recursiveRenderingGL( gl, rootNode );
	}
	
	protected void recursiveRenderingGL( GL gl , final NodeInterface node ) {
		
		if ( ! node.hasNodeIndexData() ) {
			Point2D.Float parentVertex = node.getVertex();
			
			node.startChildIteration();
			for ( NodeInterface child = node.getNextChild();
				child != null;
				child = node.getNextChild() ) {
				Point2D.Float childVertex = 
					child.getVertex();
				
				gl.glBegin( GL.GL_LINE_STRIP );
				gl.glVertex3f(childVertex.x, childVertex.y,fz);
				gl.glVertex3f(childVertex.x,parentVertex.y,fz);
				gl.glVertex3f(parentVertex.x,parentVertex.y,fz);
				gl.glEnd();
			}
		}
	}
	
	public void renderGraphSwing( Graphics canvas ,final NodeInterface rootNode ) {
		
		NodeAttributes nAttrib = rootNode.getAttributes();
		
		if ( nAttrib != null ) {
			switch (nAttrib.renderStyle) {
			case 1:
				recursiveRenderingSwing_Block( canvas, rootNode );
				return;
			case 2:
				recursiveRenderingSwing_straight( canvas, rootNode );
				return;
			default:
				recursiveRenderingSwing( canvas, rootNode );
				return;
			}
		}
		
		recursiveRenderingSwing( canvas, rootNode );
	}
	
	protected void recursiveRenderingSwing(Graphics canvas , final NodeInterface node ) {
		
		if ( ! node.hasNodeIndexData() ) {
			Point2D.Float parentVertex = node.getVertex();
			
			canvas.drawString( node.getAttributes().label,
					((int) parentVertex.x)+2, 
					((int) parentVertex.y)-2);
			
			node.startChildIteration();
			for ( int i=0; i<2; i++ ) { 								
				
				NodeInterface child = node.getNextChild();
				if ( child != null ){
					
					Point2D.Float childVertex = 
						child.getVertex();
										
					canvas.drawLine( (int) childVertex.x, (int) parentVertex.y,
							(int) parentVertex.x,(int) parentVertex.y);
					canvas.drawLine( (int) childVertex.x,(int) parentVertex.y,
							(int) childVertex.x,(int) childVertex.y);
					
					recursiveRenderingSwing( canvas, child );
				}
			}
		}
		else {
			Point2D.Float parentVertex = node.getVertex();
			
			canvas.drawString( Integer.toString( node.getIndex() ),
					((int) parentVertex.x)+2, 
					(int) parentVertex.y);
		}
	}
	
	protected void recursiveRenderingSwing_Block(Graphics canvas , final NodeInterface node ) {
		
		int iBlockOffset= 11;
		
		if ( ! node.hasNodeIndexData() ) {
			Point2D.Float parentVertex = node.getVertex();
			
			NodeAttributes nAttrib =  node.getAttributes();
			if ( nAttrib != null ) {
				canvas.drawString( nAttrib.label,
					((int) parentVertex.x)+2, 
					((int) parentVertex.y)-2);
			}
			
			node.startChildIteration();
			for ( int i=0; i<2; i++ ) { 								
				
				NodeInterface child = node.getNextChild();
				if ( child != null ){
					
					Point2D.Float childVertex = 
						child.getVertex();
					
					canvas.drawLine( (int) childVertex.x, iBlockOffset +(int) parentVertex.y,
							(int) childVertex.x,(int) childVertex.y);
					canvas.drawLine( (int) childVertex.x, iBlockOffset +(int) parentVertex.y,
							(int) parentVertex.x,(int) parentVertex.y);
//					canvas.drawLine( (int) childVertex.x,(int) parentVertex.y,
//							(int) childVertex.x,(int) childVertex.y);
					
					recursiveRenderingSwing_Block( canvas, child );
				}
			}
		}
		else {
			Point2D.Float parentVertex = node.getVertex();
			
			canvas.drawString( Integer.toString( node.getIndex() ),
					((int) parentVertex.x)+2, 
					(int) parentVertex.y);
		}
	}
	
	protected void recursiveRenderingSwing_straight(Graphics canvas , final NodeInterface node ) {
		
		if ( ! node.hasNodeIndexData() ) {
			Point2D.Float parentVertex = node.getVertex();
			
			canvas.drawString( node.getAttributes().label,
					((int) parentVertex.x)+1, 
					((int) parentVertex.y)+1);
			
			node.startChildIteration();
			for ( int i=0; i<2; i++ ) { 								
				
				NodeInterface child = node.getNextChild();
				if ( child != null ){
					
					Point2D.Float childVertex = 
						child.getVertex();
					
					canvas.drawLine( (int) childVertex.x, (int) childVertex.y,
							(int) parentVertex.x,(int) parentVertex.y);
					
					recursiveRenderingSwing_straight( canvas, child );
				}
			}
		}
		else {
			Point2D.Float parentVertex = node.getVertex();
			
			canvas.drawString( "D:"+node.getIndex(),
					(int) parentVertex.x, 
					(int) parentVertex.y);
		}
	}
	
	public void calculatePoints( final NodeInterface node ) {
		
		//recursiveCalculatePoints(node,1,5,2.0f,200,10);
		
		recursiveCalculatePoints(node,1,30,25,50,80, 600 );
		
	}
	
	protected void recursiveCalculatePoints( final NodeInterface node,
			final int iCurrentLevel,
			float xNodeIncrementPerLevel,
			float yNodeIncrementPerLevel,
			final float xLeaveNodeMin,
			final float yLeaveNode,
			final float yMaxNode ) {
		
		if ( ! node.hasNodeIndexData() ) {
			
			Point2D.Float parentVertex = node.getVertex();
			
			/** 
			 * depth first search...
			 */
			
			node.startChildIteration();			
			NodeInterface childNode = node.getNextChild();
			
			float fX_Sum = 0;
			
			int icount = 0;
			for ( int i=0; i<2; i++) {
				if ( childNode != null ) {
					recursiveCalculatePoints( childNode,
							iCurrentLevel+1,
							xNodeIncrementPerLevel,
							yNodeIncrementPerLevel,							
							xLeaveNodeMin,
							yLeaveNode,
							yMaxNode );
					
					Point2D.Float childVertex = childNode.getVertex();
					fX_Sum += childVertex.x;
				
					childNode = node.getNextChild();
					icount++;
				}
			}
			
			fX_Sum *= 1/(float) icount;
			
			node.setVertex( new Point2D.Float(fX_Sum,
					yLeaveNode + (yNodeIncrementPerLevel * iCurrentLevel)) );
			
		} else {
			Point2D.Float childVertex = new Point2D.Float(
					(node.getIndex()-1) * xNodeIncrementPerLevel + xLeaveNodeMin,
					yMaxNode);
			
			node.setVertex( childVertex );
		}
	}

}
