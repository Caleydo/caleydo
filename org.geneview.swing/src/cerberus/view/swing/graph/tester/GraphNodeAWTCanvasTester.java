/**
 * 
 */
package cerberus.view.swing.graph.tester;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Canvas;
import java.awt.GraphicsConfiguration;

import cerberus.view.swing.graph.NodeInterface;
import cerberus.view.swing.graph.visitor.NodeVisitorRenderer;

/**
 * @author Michael Kalkusch
 *
 */
public class GraphNodeAWTCanvasTester extends Canvas {

	NodeVisitorRenderer visitor;
	
	NodeInterface rootNode;
	
	/**
	 * 
	 */
	public GraphNodeAWTCanvasTester() {
		super();
		
		this.setVisible( true );
	}

//	/**
//	 * @param arg0
//	 */
//	public GraphNodeAWTCanvasTester(GraphicsConfiguration arg0) {
//		super(arg0);
//		
//		this.setVisible( true );
//	}

	public void paint(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g; // cast to 2D
	    g2D.setBackground(Color.white); 
	    
		g.setColor( Color.RED );
		
		if (( rootNode != null )&&( visitor != null )) {
			visitor.renderGraphSwing( g, rootNode );
		}
	}
	
	public void setGraph(NodeVisitorRenderer visitor, NodeInterface rootNode) {
		this.visitor = visitor;
		this.rootNode = rootNode;
	}
}
