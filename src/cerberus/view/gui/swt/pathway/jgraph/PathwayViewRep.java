package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.pathway.jgraph.GPCellViewFactory;

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
	
	protected Composite refEmbeddedFrameComposite;
	
	protected GraphModel refGraphModel;
	
	protected GraphLayoutCache refGraphLayoutCache;
	
	protected JGraph refPathwayGraph;
	
	protected DefaultGraphCell refGraphCell;
	
	protected HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;
	
	protected int iPathwayId = 0;
	
	protected int iHTMLBrowserId;
	
	protected boolean isGraphSet = false;
	
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

//				if (event.getClickCount() == 2)
//				{	
					if (clickedCell != null)
					{
						final String sUrl = ((PathwayVertex) clickedCell.getUserObject())
							.getVertexLink();
						
						String sSearchPattern = "pathway/map/";
						String sPathwayFilePath;
						
						// Check if clicked cell is another pathway
						if (sUrl.contains((CharSequence)sSearchPattern))
						{
							int iFilePathStartIndex = sUrl.lastIndexOf(sSearchPattern) + sSearchPattern.length();
							sPathwayFilePath = sUrl.substring(iFilePathStartIndex);
							sPathwayFilePath = sPathwayFilePath.replaceFirst("html", "xml");
							System.out.println("Load pathway from " +sPathwayFilePath);
							
							// Extract pathway clicked pathway ID
							int iPathwayIdIndex = sUrl.lastIndexOf("map00") + 5;
							System.out.println("Last index: " +iPathwayIdIndex);
							iPathwayId = StringConversionTool.
								convertStringToInt(sUrl.substring(iPathwayIdIndex, iPathwayIdIndex+3), 0);
	
							refGeneralManager.getSingelton().getLoggerManager().logMsg(
									"Load pathway with ID " +iPathwayId);
							
							// Load pathway
							loadPathwayFromFile("data/XML/pathways/" + sPathwayFilePath);	
						}
						// Load node information in browser
						else
						{
							final IViewManager tmpViewManager = refGeneralManager.getSingelton().
							getViewGLCanvasManager();					
					    
							refEmbeddedFrameComposite.getDisplay().asyncExec(new Runnable() {
								public void run() {
									((HTMLBrowserViewRep)tmpViewManager.
											getItem(iHTMLBrowserId)).setUrl(sUrl);
								}
							});	
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
		
		// Take first in list if pathway ID is not set
		if (iPathwayId == 0)
		{
			Iterator<Pathway> iter = pathwayLUT.values().iterator();
			pathway = iter.next();
		}
		else
		{
			pathway = pathwayLUT.get(iPathwayId);
		}
	    
	    Vector<PathwayVertex> vertexList;
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    Vector<IPathwayVertexRep> vertexReps;
	    Iterator<IPathwayVertexRep> vertexRepIterator;
	    PathwayVertexRep vertexRep;
	    
	    Vector<PathwayEdge> edgeList;
	    Iterator<PathwayEdge> edgeIterator;
	    PathwayEdge edge;
	    
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
        		
        		// FIXME: this is just a workaround.
        		// inconsitency between vertexRep and vertex "name"
        		vertex.setElementTitle(vertexRep.getSName());
        		
        		createVertex(vertex, 
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

        // Check if graph is already added to the frame
        if (isGraphSet == false)
        {
        	refEmbeddedFrame.add(new JScrollPane(refPathwayGraph), SWT.NONE);
        	isGraphSet = true;
        }
	}
	
	public void createVertex(PathwayVertex vertex, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, PathwayVertexType vertexType) {
		
		//create node
		refGraphCell = new DefaultGraphCell(vertex);
	
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
	
	public void setPathwayId(int iPathwayId) {
		this.iPathwayId = iPathwayId;
	}
	
	public void loadPathwayFromFile(String sFilePath) {
		
		refGraphModel = new DefaultGraphModel();
		refPathwayGraph.setModel(refGraphModel);
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sFilePath);
		
		//resetGraph();
		drawView();
		
		//refPathwayGraph.getGraphLayoutCache().reload();
		//refEmbeddedFrame.dispose();
	}
	
	public void resetGraph() {
		
		initView();
	}
	
	public void retrieveGUIContainer() {
		
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = (SWTEmbeddedGraphWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
		
		refEmbeddedFrameComposite = 
			refSWTEmbeddedGraphWidget.getEmbeddedFrameComposite();
	}
	
	/**
	 * Retrieves the HTML browser ID.
	 */
	public void extractAttributes() {
		
		iHTMLBrowserId = 
			refParameterHandler.getValueInt( "iHTMLBrowserId" );
	}
}
