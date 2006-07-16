package cerberus.pathways.graph;

import cerberus.pathways.Pathway;
import cerberus.pathways.PathwayManager;
import cerberus.pathways.element.Vertex;
import cerberus.pathways.element.VertexRepresentation;
//import cerberus.pathways.element.ElementManager;
//import cerberus.pathways.element.Edge;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
//import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

public class PathwayGraphBuilder 
{
	private GraphModel model;
	private GraphLayoutCache view;
	private JGraph pathwayGraph;
	private DefaultGraphCell cell;
	
	/**
	 * Constructor
	 */
	public PathwayGraphBuilder()
	{		
		model = new DefaultGraphModel();
		view = new GraphLayoutCache(model, new DefaultCellViewFactory());

		pathwayGraph = new JGraph(model, view);
	}
	
	public void setUpPathwayGraph()
	{
		HashMap<Integer, Pathway> pathwayLUT = PathwayManager.getInstance().getPathwayLUT();
		
		//iterator through the pathways
	    Iterator<Pathway> pathwayIterator = pathwayLUT.values().iterator();
	    Iterator<Vertex> vertexIterator;
	    Vector<VertexRepresentation> vertexRepresentations;
	    Iterator<VertexRepresentation> vertexRepIterator;
	    VertexRepresentation vertexRep;
	    
	    while (pathwayIterator.hasNext()) 
	    {
	        Vector<Vertex> vertexList = pathwayIterator.next().getVertexList();
	        vertexIterator = vertexList.iterator();
	        while (vertexIterator.hasNext())
	        {
	        	vertexRepresentations = vertexIterator.next().getVertexRepresentations();
	        	vertexRepIterator = vertexRepresentations.iterator();
	        	while (vertexRepIterator.hasNext())
	        	{
	        		vertexRep = vertexRepIterator.next();
	        		createCell(vertexRep.getSName(), vertexRep.getIHeight(),
	        				vertexRep.getIWidth(), vertexRep.getIXPosition(), vertexRep.getIYPosition());
	        	}
	        }   
	    }
		
//		HashMap<Integer, Vertex> vertexLUT = ElementManager.getInstance().getVertexLUT();
//		
//		for (int vertexIndex = 0; vertexIndex <= vertexLUT.size(); vertexIndex++)
//		{
//			vertexLUT.values()
//		}
	}
	
	public void createCell(String sTitle, int iHeight, int iWidth, 
			int iXPosition, int iYPosition)
	{	
		//create node
		cell = new DefaultGraphCell(sTitle);
		GraphConstants.setBounds(cell.getAttributes(), 
				new Rectangle2D.Double(iXPosition, iYPosition, iWidth, iHeight));
		GraphConstants.setGradientColor(cell.getAttributes(), Color.orange);
		GraphConstants.setOpaque(cell.getAttributes(), true);

		//create port
		DefaultPort port0 = new DefaultPort();
		cell.add(port0);	
		
		pathwayGraph.getGraphLayoutCache().insert(cell);
	}
	
	
	public void showPathwayGraph()
	{		
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(pathwayGraph));
		frame.pack();
		frame.setVisible(true);
	}
}
