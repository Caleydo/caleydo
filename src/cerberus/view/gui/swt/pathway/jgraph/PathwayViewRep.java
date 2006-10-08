package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.jgraph.example.mycellview.GPCellViewFactory;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.data.pathway.PathwayManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.event.mediator.IMediator;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.view.gui.swt.pathway.IPathwayView;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;

/**
 * In this class the real drawing of the Pathway happens.
 * For the drawing the JGraph package is used.
 * We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Marc Streit
 *
 */
public class PathwayViewRep
extends AViewRep 
implements IPathwayView{
		
	protected static final double SCALING_FACTOR = 1.4;
	
	protected Frame refEmbeddedFrame;
	
	protected GraphModel refGraphModel;
	
	protected GraphLayoutCache refGraphLayoutCache;
	
	protected JGraph refPathwayGraph;
	
	protected DefaultGraphCell refGraphCell;
	
	protected HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;
	
	protected int iHTMLBrowserId;
	
	public PathwayViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		vertexIdToCellLUT = new HashMap<Integer, DefaultGraphCell>();
	}

	public void initView() {
		
		class PathwayMarqueeHandler 
		extends BasicMarqueeHandler {

			public boolean isForceMarqueeEvent(MouseEvent event) {
				
		       if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2)
		          return true;
		       else
		          return super.isForceMarqueeEvent(event);
		    } 
			
		    public void mousePressed(final MouseEvent event) {

		    	DefaultGraphCell clickedCell = (DefaultGraphCell) refPathwayGraph
						.getFirstCellForLocation(event.getX(), event.getY());

				if (event.getClickCount() == 2)
				{	
					if (clickedCell != null)
					{
						final String sUrl = ((PathwayVertex) clickedCell.getUserObject())
							.getVertexLink();
						
						final IViewManager tmpViewManager = refGeneralManager.getSingelton().
							getViewGLCanvasManager();
						
						((HTMLBrowserViewRep)tmpViewManager.
								getItem(iHTMLBrowserId)).setUrl(sUrl);
						
//						if (SwingUtilities.isEventDispatchThread())
//							((HTMLBrowserViewRep)tmpViewManager.
//									getItem(iHTMLBrowserId)).setUrl(sUrl);
//						else
//						{
//							try {
//								SwingUtilities.invokeLater(new Runnable() {
//									public void run() {
//										((HTMLBrowserViewRep)tmpViewManager.
//												getItem(iHTMLBrowserId)).setUrl(sUrl);
//									}
//								});									
//							} catch(InterruptedException ie) {
//							} catch(InvocationTargetException ite) {}
//						}
					}
				}
				super.mousePressed(event);
			}
		}
		
		refGraphModel = new DefaultGraphModel();
		refGraphLayoutCache = 
			new GraphLayoutCache(refGraphModel, new DefaultCellViewFactory());

		refPathwayGraph = new JGraph(refGraphModel, refGraphLayoutCache);
		
		// Set own cell view factory
		refPathwayGraph.getGraphLayoutCache().setFactory(new GPCellViewFactory());

		// Control-drag should clone selection
		refPathwayGraph.setCloneable(true);
		
		refPathwayGraph.setMarqueeHandler(new PathwayMarqueeHandler());
	}
	
	public void drawView() {
		
		HashMap<Integer, Pathway> pathwayLUT = 		
			((PathwayManager)refGeneralManager.getManagerByBaseType(ManagerObjectType.PATHWAY)).
				getPathwayLUT();
		
		Pathway pathway;
		Iterator<Pathway> pathwayIterator = pathwayLUT.values().iterator();
	    
	    Vector<PathwayVertex> vertexList;
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    Vector<IPathwayVertexRep> vertexReps;
	    Iterator<IPathwayVertexRep> vertexRepIterator;
	    PathwayVertexRep vertexRep;
	    
	    Vector<PathwayEdge> edgeList;
	    Iterator<PathwayEdge> edgeIterator;
	    PathwayEdge edge;
	    
	    while (pathwayIterator.hasNext()) 
	    {
	    	pathway = pathwayIterator.next();
	        vertexList = pathway.getVertexList();
	        
	        vertexIterator = vertexList.iterator();
	        while (vertexIterator.hasNext())
	        {
	        	vertex = vertexIterator.next();
	        	vertexReps = vertex.getVertexReps();
	        	vertexRepIterator = vertexReps.iterator();
	        	while (vertexRepIterator.hasNext())
	        	{
	        		vertexRep = (PathwayVertexRep) vertexRepIterator.next();
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
		
		refEmbeddedFrame.add(new JScrollPane(refPathwayGraph), SWT.NONE);
	}
	
	public void createVertex(PathwayVertex vertex, String sTitle, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, PathwayVertexType vertexType) {
		
		//create node
		refGraphCell = new DefaultGraphCell(sTitle);
	
		GraphConstants.setOpaque(refGraphCell.getAttributes(), true);
		GraphConstants.setAutoSize(refGraphCell.getAttributes(), true);
		
		//assign vertex color
		if (vertexType == PathwayVertexType.enzyme)
		{
			// Set vertex type to ellipse
			GPCellViewFactory.setViewClass(
					refGraphCell.getAttributes(), 
					"com.jgraph.example.mycellview.RoundRectView");

			GraphConstants.setBounds(refGraphCell.getAttributes(), 
					new Rectangle2D.Double(
							(int)(iXPosition * SCALING_FACTOR), 
							(int)(iYPosition * SCALING_FACTOR), 
							iWidth, iHeight));
			GraphConstants.setGradientColor(refGraphCell.getAttributes(), Color.orange);
		}
		else if (vertexType == PathwayVertexType.compound)
		{
			// Set vertex type to ellipse
			GPCellViewFactory.setViewClass(
					refGraphCell.getAttributes(), 
					"com.jgraph.example.mycellview.JGraphEllipseView");
			
			GraphConstants.setBounds(refGraphCell.getAttributes(), 
					new Rectangle2D.Double(
							(int)(iXPosition * SCALING_FACTOR), 
							(int)(iYPosition * SCALING_FACTOR), 
							15, 15));
			GraphConstants.setGradientColor(refGraphCell.getAttributes(), Color.green);
		}	
		else if (vertexType == PathwayVertexType.map)
		{
			// Set vertex type to ellipse
			GPCellViewFactory.setViewClass(
					refGraphCell.getAttributes(), 
					"com.jgraph.example.mycellview.RoundRectView");

			GraphConstants.setBounds(refGraphCell.getAttributes(), 
					new Rectangle2D.Double(
							(int)(iXPosition * SCALING_FACTOR), 
							(int)(iYPosition * SCALING_FACTOR), 
							iWidth, iHeight));
			GraphConstants.setGradientColor(refGraphCell.getAttributes(), Color.yellow);
		}
		
		refPathwayGraph.getGraphLayoutCache().insert(refGraphCell);
		
		vertexIdToCellLUT.put(vertex.getIElementId(), refGraphCell);
	}
	
	public void createEdge(int iVertexId1, int iVertexId2) {
		
		DefaultPort port1 = new DefaultPort();
		DefaultGraphCell cell1 = vertexIdToCellLUT.get(iVertexId1);
		cell1.add(port1);
		
		DefaultPort port2 = new DefaultPort();
		DefaultGraphCell cell2 = vertexIdToCellLUT.get(iVertexId2);
		cell2.add(port2);
		
		DefaultEdge edge = new DefaultEdge();
		
//		GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_SIMPLE);
//		GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);

		edge.setSource(cell1.getChildAt(0));
		edge.setTarget(cell2.getChildAt(0));
		refPathwayGraph.getGraphLayoutCache().insert(edge);
	}
	
	public void retrieveGUIContainer() {
		
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = (SWTEmbeddedGraphWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
	}
	
	/**
	 * Retrieves the HTML browser ID.
	 */
	public void extractAttributes() {
		
		iHTMLBrowserId = 
			refParameterHandler.getValueInt( "iHTMLBrowserId" );
	}
}
