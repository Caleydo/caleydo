/*
 * @(#)HelloWorld.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2004, Gaudenz Alder All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  
 */
package cerberus.view.swing.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;


import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jgraph.JGraph;
import org.jgraph.graph.Edge;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.AttributeMap;

public class CerberusGraphViewer {

	public JFrame frame;
	
	protected GraphModel model;
	protected JGraph graph;
	protected JMenuBar menuBar;
	
	protected Vector<DefaultGraphCell> vecCell;
	protected Vector<DefaultGraphCell> vecCellEdge;
	
	public CerberusGraphViewer() {
//		 Construct Model and Graph
		model = new DefaultGraphModel();
		graph = new JGraph(model);

		// Control-drag should clone selection
		graph.setCloneable(true);

		// Enable edit without final RETURN keystroke
		graph.setInvokesStopCellEditing(true);

		// When over a cell, jump to its default port (we only have one, anyway)
		graph.setJumpToDefaultPort(true);

		// Insert all three cells in one call, so we need an array to store them
		DefaultGraphCell[] cells = new DefaultGraphCell[3];

		/*
		// Create Hello Vertex
		cells[0] = createVertex("Hello", 20, 20, 40, 20, null, false);

		// Create World Vertex
		cells[1] = createVertex("World", 140, 140, 40, 20, Color.ORANGE, true);

		// Create Edge
		DefaultEdge edge = new DefaultEdge();
		// Fetch the ports from the new vertices, and connect them with the edge
		edge.setSource(cells[0].getChildAt(0));
		edge.setTarget(cells[1].getChildAt(0));
		cells[2] = edge;
		*/

	

		// Show in Frame
		frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize( 700, 700 );
		//frame.pack();
		
		intiMenus();
		
		frame.setVisible( true );		
	}
	
	private void intiMenus() {
		menuBar = new JMenuBar();
		
		JMenu layoutMenu = new JMenu("layout");
		JMenuItem item = new JMenuItem("circle");
		item.addActionListener( new ActionListener () {
			public void actionPerformed(ActionEvent ae) {
				layoutGraph(0,40,20);
			}
		});
		layoutMenu.add( item );
		
		item = new JMenuItem("circle (min)");
		item.addActionListener( new ActionListener () {
			public void actionPerformed(ActionEvent ae) {
				layoutGraph(0,15,15);
			}
		});
		layoutMenu.add( item );
		
		item = new JMenuItem("components");
		item.addActionListener( new ActionListener () {
			public void actionPerformed(ActionEvent ae) {
				layoutGraph(1,40,20);
			}
		});
		layoutMenu.add( item );
		
		item = new JMenuItem("comp (min)");
		item.addActionListener( new ActionListener () {
			public void actionPerformed(ActionEvent ae) {
				layoutGraph(1,15,15);
			}
		});
		layoutMenu.add( item );
		
		menuBar.add( layoutMenu );
		
		frame.setJMenuBar( menuBar ); 
	}
	
	public void init( final int iSizeCells ) {
		vecCell = new Vector <DefaultGraphCell> (iSizeCells);
		vecCellEdge = new  Vector <DefaultGraphCell> (iSizeCells);
		
		/*
		createVertex("Hello", 20, 20, 40, 20, Color.CYAN, true);

		// Create World Vertex
		createVertex("World", 140, 140, 40, 20, Color.ORANGE, true);
		
		createVertex("A", 140, 140, 40, 20, Color.RED, true);
		createVertex("B", 140, 140, 40, 20, Color.GRAY, true);
		createVertex("C", 140, 140, 40, 20, Color.GREEN, true);		
		createVertex("D", 140, 140, 40, 20, Color.BLUE, true);
		
		createVertex("E", 140, 140, 40, 20, Color.BLUE, true);
		createVertex("F", 140, 140, 40, 20, Color.BLUE, true);
		
		createVertex("G", 140, 140, 40, 20, Color.BLUE, true);
		createVertex("H", 140, 140, 40, 20, Color.BLUE, true);
		
		createEdge( vecCell.get(0),  vecCell.get(1) );
		
		createEdge(1,2);
		createEdge(2,3);
		createEdge(0,3);
		createEdge(4,5);
		
		createEdge(6,7);
		createEdge(6,7);
		createEdge(6,9);
		createEdge(9,2);
		*/
	}
	
	
	public static void main(String[] args) {

		CerberusGraphViewer viewer = new CerberusGraphViewer();
		
		viewer.init( 10 );
		viewer.run();
	}
	
	public void run() {
		
		frame.setVisible(true);
		
		this.layoutGraph(0, 40, 20 );
		
	}
	
	public boolean cleanGraph() {
		Iterator <DefaultGraphCell> iterNode = this.vecCell.iterator();
		
		DefaultGraphCell[] cellArray = new DefaultGraphCell[1];
		
		while ( iterNode.hasNext() ) {				
			DefaultGraphCell cell = iterNode.next();
			cellArray[0] = cell;
			graph.getGraphLayoutCache().remove( cellArray, false, true );
		}
		
		graph.getGraphLayoutCache().reload();
		frame.repaint();
		
		return true;
	}
	
	public void layoutGraph( final int iLayoutStyle, final float fCellWidthX, final float fCellHeightY) {
		
				
		float fBorderX = 50;
		float fBorderY = 100;		
		float fRadius = 200;
		
		Dimension sizeFrame = frame.getSize();
		
		if ( sizeFrame.width < sizeFrame.height ) {
			fRadius = sizeFrame.width * 0.5f  - fBorderX;
		} else {
			fRadius = sizeFrame.height * 0.5f - fBorderY;
		}
		
		float fX = (fBorderX * 0.5f) + fRadius;
		float fY = (fBorderY * 0.5f) + fRadius;
		
		if ( iLayoutStyle == 0 ) {
			Iterator <DefaultGraphCell> iterNode = vecCell.iterator();
			int iItems = vecCell.size();
			
			System.out.println("  Layout " + Integer.toString(iItems) );
			
			layoutGraphIteratorCircular(iLayoutStyle,fCellWidthX,fCellHeightY,
					fX, fY, fRadius, 
					iItems, 
					0.0f,
					iterNode);
		
		} 
		else if ( iLayoutStyle == 1 ) {
			Iterator <DefaultGraphCell> iterNode = vecCell.iterator();
			int iItems = vecCell.size();
			
			int iBorderX = 45;	
			int iBorderY = 20;			
			int iBottum = sizeFrame.height - iBorderY;
			int iRightBorder = sizeFrame.width - iBorderX;
			
			layoutGraphIteratorLinear(iLayoutStyle,fCellWidthX,fCellHeightY,
					new Point2D.Float(iBorderX,iBorderY),
					new Point2D.Float(iRightBorder,iBorderY),
					10, 
					0.0f,
					iterNode);
			
			//iterNode = vecCell.iterator();
			
			layoutGraphIteratorLinear(iLayoutStyle,fCellWidthX,fCellHeightY,
					new Point2D.Float(iBorderX,iBottum),
					new Point2D.Float(iRightBorder,iBottum),
					iItems - 10, 
					0.0f,
					iterNode);
			
		}

		graph.getGraphLayoutCache().reload();		
		frame.repaint();
	}
	
	public void layoutGraphIteratorCircular( final int iLayoutStyle, 
			final float fCellWidthX, 
			final float fCellHeightY,
			final float fCircleX, 
			final float fCircleY,
			final float fCircleRadius,
			final int iItems,
			final float fAlphaStart,
			Iterator <DefaultGraphCell> iter) {
						
			float fIncAlpha = (float)((2 * Math.PI) /  (double) (iItems+1));
			float fAlpha = fAlphaStart;
			int iItemCounter = 0;
			
			while (( iter.hasNext() )&&(iItemCounter < iItems)) {
				DefaultGraphCell node = iter.next();
				
				AttributeMap map = node.getAttributes();
				
				Rectangle2D.Float cellBounds = new Rectangle2D.Float( fCircleX + fCircleRadius * (float) Math.sin( fAlpha ) , 
						fCircleY + fCircleRadius* (float) Math.cos( fAlpha ) ,
						fCellWidthX,
						fCellHeightY); 		
				
				map.put( GraphConstants.BOUNDS, cellBounds );
				
				//node.setAttributes( map );
		
				fAlpha += fIncAlpha;
				iItemCounter++;
			}
	}
	
	public void layoutGraphIteratorLinear( final int iLayoutStyle, 
			final float fCellWidthX, 
			final float fCellHeightY,
			final Point2D.Float from, 
			final Point2D.Float to,
			final int iItems,
			final float fAlphaStart,
			Iterator <DefaultGraphCell> iter) {
			
			float fInverseItems = 1 / (float) (iItems-1);
			float fIncX = (to.x - from.x) * fInverseItems;
			float fIncY = (to.y - from.y) * fInverseItems;
			
			Point2D.Float currentPoint = 
				new Point2D.Float( from.x, from.y );
			
			int iItemCounter = 0;
			
			while (( iter.hasNext() )&&(iItemCounter < iItems)) {
				DefaultGraphCell node = iter.next();
				
				
				
				Rectangle2D.Float cellBounds = new Rectangle2D.Float( currentPoint.x , 
						currentPoint.y ,
						fCellWidthX,
						fCellHeightY); 		
				
				
//				AttributeMap map = node.getAttributes();
//				map.put( GraphConstants.BOUNDS, cellBounds );
								
				node.getAttributes().put( GraphConstants.BOUNDS, cellBounds );				
		
				currentPoint.x += fIncX;
				currentPoint.y += fIncY;
				
				iItemCounter++;
			}
	}


	public Edge createEdge(int iIndexSource, int iIndexTarget) {
		return createEdge( vecCell.get(iIndexSource),  vecCell.get(iIndexTarget) );
	}
	
	public Edge createEdge(DefaultGraphCell source, DefaultGraphCell target) {
		
		//Create Edge
		DefaultEdge edge = new DefaultEdge();
		// Fetch the ports from the new vertices, and connect them with the edge
		edge.setSource(source.getChildAt(0));
		edge.setTarget(target.getChildAt(0));
		
		//ISet Arrow Style for edge
		GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setEndFill(edge.getAttributes(), true);
		
		vecCellEdge.add( edge );
		
		// Insert the cells via the cache, so they get selected
		graph.getGraphLayoutCache().insert(edge);
				
		return edge;		
	}


	public DefaultGraphCell createVertex(String name, double x,
			double y, double w, double h, Color bg, boolean raised) {

		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(name);

		// ISet bounds
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		// ISet fill color
		if (bg != null) {
			GraphConstants.setGradientColor(cell.getAttributes(), Color.orange);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		// ISet raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory
					.createRaisedBevelBorder());
		else
			// ISet black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

		// Add a Floating Port
		cell.addPort();
		
		vecCell.addElement( cell );
		
		graph.getGraphLayoutCache().insert( cell );

		return cell;
	}

}