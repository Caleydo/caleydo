package org.caleydo.testing.applications.jgraph.kegg;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * Pathway graph builder
 * 
 * @author Marc Streit
 */
public class PathwayGraphBuilder
{
	private GraphModel model;
	private GraphLayoutCache view;
	private JGraph pathwayGraph;
	private DefaultGraphCell cell;

	/**
	 * Constructor.
	 */
	public PathwayGraphBuilder()
	{
		model = new DefaultGraphModel();
		view = new GraphLayoutCache(model, new DefaultCellViewFactory());

		pathwayGraph = new JGraph(model, view);
	}

	public void createCell(String sTitle, int iHeight, int iWidth, int iXPosition,
			int iYPosition)
	{
		// create node
		cell = new DefaultGraphCell(sTitle);
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(iXPosition,
				iYPosition, iWidth, iHeight));
		GraphConstants.setGradientColor(cell.getAttributes(), Color.orange);
		GraphConstants.setOpaque(cell.getAttributes(), true);

		// create port
		DefaultPort port0 = new DefaultPort();
		cell.add(port0);

		pathwayGraph.getGraphLayoutCache().insert(cell);
	}

	public void insertEdge()
	{
		// DefaultEdge edge = new DefaultEdge();
		// edge.setSource(cells[0].getChildAt(0));
		// edge.setTarget(cells[1].getChildAt(0));
		// cells[2] = edge;
		//
		// int arrow = GraphConstants.ARROW_CLASSIC;
		// GraphConstants.setLineEnd(edge.getAttributes(), arrow);
		// GraphConstants.setEndFill(edge.getAttributes(), true);
	}

	public void showPathwayGraph()
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(pathwayGraph));
		frame.pack();
		frame.setVisible(true);
	}
}
