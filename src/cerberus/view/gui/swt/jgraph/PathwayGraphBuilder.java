package cerberus.view.gui.swt.jgraph;

import cerberus.data.pathway.Pathway;
import cerberus.manager.pathway.PathwayManager;
import cerberus.data.pathway.element.Vertex;
import cerberus.data.pathway.element.VertexRepresentation;
import cerberus.data.pathway.element.Edge;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
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
	
	private HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;
	/**
	 * Constructor
	 */
	public PathwayGraphBuilder()
	{		
		model = new DefaultGraphModel();
		view = new GraphLayoutCache(model, new DefaultCellViewFactory());

		pathwayGraph = new JGraph(model, view);
		
		vertexIdToCellLUT = new HashMap<Integer, DefaultGraphCell>();
	}
	
	public void setUpPathwayGraph()
	{
		HashMap<Integer, Pathway> pathwayLUT = PathwayManager.getInstance().getPathwayLUT();
			
		Pathway pathway;
		Iterator<Pathway> pathwayIterator = pathwayLUT.values().iterator();
	    
	    Vector<Vertex> vertexList;
	    Iterator<Vertex> vertexIterator;
	    Vertex vertex;
	    Vector<VertexRepresentation> vertexRepresentations;
	    Iterator<VertexRepresentation> vertexRepIterator;
	    VertexRepresentation vertexRep;
	    
	    Vector<Edge> edgeList;
	    Iterator<Edge> edgeIterator;
	    Edge edge;
	    
	    
	    while (pathwayIterator.hasNext()) 
	    {
	    	pathway = pathwayIterator.next();
	        vertexList = pathway.getVertexList();
	        
	        vertexIterator = vertexList.iterator();
	        while (vertexIterator.hasNext())
	        {
	        	vertex = vertexIterator.next();
	        	vertexRepresentations = vertex.getVertexRepresentations();
	        	vertexRepIterator = vertexRepresentations.iterator();
	        	while (vertexRepIterator.hasNext())
	        	{
	        		vertexRep = vertexRepIterator.next();
	        		createVertex(vertex, vertexRep.getSName(), 
	        				vertexRep.getIHeight(), vertexRep.getIWidth(), 
	        				vertexRep.getIXPosition(), vertexRep.getIYPosition(),
	        				vertex.getVertexType());
	        	}
	        }   
	        
	        edgeList = pathway.getEdgeList();
	        edgeIterator = edgeList.iterator();
	        while (edgeIterator.hasNext())
	        {
	        	edge = edgeIterator.next();
	        
	        	if (edge.getSType().equals("ECrel"))
	        	{
	        		if (edge.getICompoundId() == -1)
	        		{
		        		createEdge(edge.getIElementId1(), edge.getIElementId2());	        			
	        		}
	        		else 
	        		{
	        			createEdge(edge.getIElementId1(), edge.getICompoundId());
	        			createEdge(edge.getICompoundId(), edge.getIElementId2());
	        		}
	        	}
	        }   
	    }
	}
	
	public void createVertex(Vertex vertex, String sTitle, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, Vertex.VertexType vertexType)
	{	
		//create node
		cell = new DefaultGraphCell(sTitle);
		GraphConstants.setBounds(cell.getAttributes(), 
				new Rectangle2D.Double((int)(iXPosition * 1.4), (int)(iYPosition * 1.4), iWidth, iHeight));
		GraphConstants.setOpaque(cell.getAttributes(), true);
		GraphConstants.setAutoSize(cell.getAttributes(), true);
		
		//assign vertex color
		if (vertexType == Vertex.VertexType.enzyme)
			GraphConstants.setGradientColor(cell.getAttributes(), Color.orange);
		else if (vertexType == Vertex.VertexType.compound)
			GraphConstants.setGradientColor(cell.getAttributes(), Color.green);
		else if (vertexType == Vertex.VertexType.map)
			GraphConstants.setGradientColor(cell.getAttributes(), Color.yellow);
		
		
		pathwayGraph.getGraphLayoutCache().insert(cell);
		
		vertexIdToCellLUT.put(vertex.getIElementId(), cell);
	}
	
	public void createEdge(int iVertexId1, int iVertexId2)
	{
		DefaultPort port1 = new DefaultPort();
		DefaultGraphCell cell1 = vertexIdToCellLUT.get(iVertexId1);
		cell1.add(port1);
		
		DefaultPort port2 = new DefaultPort();
		DefaultGraphCell cell2 = vertexIdToCellLUT.get(iVertexId2);
		cell2.add(port2);
		
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(cell1.getChildAt(0));
		edge.setTarget(cell2.getChildAt(0));
		pathwayGraph.getGraphLayoutCache().insert(edge);
	}
	
	
	public void showPathwayGraph()
	{		
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(pathwayGraph));
		frame.pack();
		frame.setVisible(true);
	}
}
