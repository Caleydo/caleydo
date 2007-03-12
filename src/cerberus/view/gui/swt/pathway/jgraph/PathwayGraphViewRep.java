package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.set.selection.ISetSelection;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle.EdgeArrowHeadStyle;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle.EdgeLineStyle;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.net.dwt.swing.WorkspaceSwingFrame;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;

/**
 * In this class the real drawing of the Pathway happens.
 * For the drawing the JGraph package is used.
 * We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */

public class PathwayGraphViewRep
extends APathwayGraphViewRep {
	
	/**
	 * Pathway element positions are read from XML files.
	 * The scaling factor can scale the positions to blow up or
	 * shrink the pathway.
	 */
	protected static final float SCALING_FACTOR = 1.15f;
	
	protected Pathway refCurrentPathway;
	
	protected GraphModel refGraphModel;
	
	protected GraphLayoutCache refGraphLayoutCache;
	
	protected JGraph refPathwayGraph;
	
	protected DefaultGraphCell refGraphCell;
	
	protected HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;
	
	protected boolean bGraphSet = false;
	
	protected boolean bShowBackgroundOverlay = false;
	
	protected GraphUndoManager refUndoManager;
	
	protected GPOverviewPanel refOverviewPanel;
	
	protected Vector<DefaultEdge> vecRelationEdges;
	
	protected Vector<DefaultEdge> vecReactionEdges;
	
	protected Vector<DefaultGraphCell> vecVertices;
	
	protected ArrayList<DefaultGraphCell> arSelectedVertices;
 	
	/**
	 * Integer storage of selected vertices
	 * Container is needed for selection updates.
	 */
	protected LinkedList<Integer> iLLSelectedVertices;
	
	/**
	 * Neighbor distance from currently selected vertex.
	 * Container is needed for selection updates.
	 */
	protected LinkedList<Integer> iLLNeighborDistance;
	
	/**
	 * Specifies how deep the neighborhood recursion
	 * should visualize surrounding elements.
	 * Default value is 0.
	 */
	protected int iNeighbourhoodDistance = 0;
	
	protected float fScalingFactor;
	
	/**
	 * Counts how often the neighbour recursion is called.
	 * This is needed for the UNDO operation on the next selection.
	 */
	protected int iNeighbourhoodUndoCount = 0;
	
	/**
	 * Flag shows if the neighbours of a cell
	 * are currently displayed. Flag is needed
	 * for UNDO of previous neigbour highlighning.
	 */
	protected boolean bNeighbourhoodShown = false;
	
	protected HashSet<DefaultGraphCell> hashSetVisitedNeighbors;
	
	protected HashMap<IPathwayVertexRep, DefaultGraphCell> hashVertexRep2GraphCell;
	
	public PathwayGraphViewRep(IGeneralManager refGeneralManager, 
			int iParentContainerId) {
		
		super(refGeneralManager, -1, iParentContainerId, "");
		
		vertexIdToCellLUT = new HashMap<Integer, DefaultGraphCell>();
		
		vecRelationEdges = new Vector<DefaultEdge>();
		vecReactionEdges = new Vector<DefaultEdge>();
		vecVertices = new Vector<DefaultGraphCell>();
		
		fScalingFactor = SCALING_FACTOR;
		
		iLLSelectedVertices = new LinkedList<Integer>();
		iLLNeighborDistance = new LinkedList<Integer>();
		
		hashVertexRep2GraphCell = new HashMap<IPathwayVertexRep, DefaultGraphCell>();
	
		hashSetVisitedNeighbors = new HashSet<DefaultGraphCell>();
		
		arSelectedVertices = new ArrayList<DefaultGraphCell>();
	}

	/**
	 * Method uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * 
	 */
	protected void retrieveGUIContainer() {
		
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = 
			(SWTEmbeddedGraphWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET,
						refEmbeddedFrameComposite,
						iWidth, 
						iHeight);

		refSWTEmbeddedGraphWidget.createEmbeddedComposite();
		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
	}
	
	
	public void initView() {
		
		retrieveGUIContainer();
		extractCurrentPathwayFromSet();
		
		class PathwayMarqueeHandler 
		extends BasicMarqueeHandler {

			public boolean isForceMarqueeEvent(MouseEvent event) {
				
		       if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1)
		          return true;
		       else
		          return super.isForceMarqueeEvent(event);
		    } 
						
		    public void mousePressed(final MouseEvent event) {

		    	if (refCurrentPathway != null) 
		    	{		    	
			    	DefaultGraphCell clickedCell = (DefaultGraphCell) refPathwayGraph
							.getFirstCellForLocation(event.getX(), event.getY());
	
			    	// Remove old selected vertices
			    	iLLSelectedVertices.clear();
			    	iLLNeighborDistance.clear();
			    	arSelectedVertices.clear();
			    	
			    	// Check if a node or edge was hit.
			    	// If not undo neighborhood visualization and return.
			    	if (clickedCell == null)
			    	{
						for (int iUndoCount = 0; iUndoCount < iNeighbourhoodUndoCount; iUndoCount++)
						{
							refUndoManager.undo(refGraphLayoutCache);
						}
						
						iNeighbourhoodUndoCount = 0;
						bNeighbourhoodShown = false;
			    		return;
			    	}
			    	
	//				if (event.getClickCount() == 2)
	//				{	
			    		// Check if cell has an user object attached
					if (clickedCell.getUserObject() == null)
					{
						super.mousePressed(event);
						return;						
					}
	
					if (!clickedCell.getUserObject().getClass().getSimpleName()
							.equals(PathwayVertexRep.class.getSimpleName()))
					{
						super.mousePressed(event);
						return;
					}
					
					final String sUrl = 
						((PathwayVertexRep)clickedCell.getUserObject()).
							getVertex().getVertexLink();
					
					if (sUrl == "") 
					{
						super.mousePressed(event);
						return;
					}
					
					if (extractClickedPathway(sUrl) == false);
					{
						loadNodeInformationInBrowser(sUrl);
	
						// UNDO old neighborhood visualization
						if (bNeighbourhoodShown == true)
						{
							for (int iUndoCount = 0; iUndoCount < iNeighbourhoodUndoCount; iUndoCount++)
							{
								refUndoManager.undo(refGraphLayoutCache);
							}
							iNeighbourhoodUndoCount = 0;
							bNeighbourhoodShown = false;
						}
						
						// Highlight current cell
						highlightCell(clickedCell, Color.RED);
						
						// The clicked vertex will be added with neighborhood distance of 0
						iLLNeighborDistance.add(0);
						
						bNeighbourhoodShown = true;
						iNeighbourhoodUndoCount++;
	
						// Add selected vertex itself because neighborhood algorithm
						// only adds neighbor vertices.
						iLLSelectedVertices.add(((PathwayVertexRep)clickedCell.
								getUserObject()).getVertex().getElementId());
						
						arSelectedVertices.add(clickedCell);
						
						if (iNeighbourhoodDistance != 0)
						{	
							hashSetVisitedNeighbors.clear();
							
							showNeighbourhoodBFS(clickedCell, 
								iNeighbourhoodDistance);
					
							bNeighbourhoodShown = true;
						}
						
						int[] iArSelectedVertices = null;
						int[] iArNeighborDistance = null;

						// Convert Link List to int[]
					    Iterator<Integer> iter_I = iLLSelectedVertices.iterator();		    
					    iArSelectedVertices = new int[iLLSelectedVertices.size()];		    
					    for ( int i=0; iter_I.hasNext() ;i++ ) {
					    	iArSelectedVertices[i] = iter_I.next().intValue();
					    }
					    
					    iter_I = iLLNeighborDistance.iterator();		    
					    iArNeighborDistance = new int[iLLNeighborDistance.size()];		    
					    for ( int i=0; iter_I.hasNext() ;i++ ) {
					    	iArNeighborDistance[i] = iter_I.next().intValue();
					    }
						
						updateSelectionSet(iArSelectedVertices, 
								new int[0], iArNeighborDistance);
						
					}// if(sUrl.contains((CharSequence)sSearchPattern))
				}// if(refCurrentPathway != 0) 
		    	else if (refCurrentPathwayImageMap != null)
		    	{
		    		String sLink = refCurrentPathwayImageMap.processPoint(
		    				new Point(event.getX(), event.getY()));
		    		
		    		if (sLink == null || sLink.equals(""))
		    		{
		    			refGeneralManager.getSingelton().logMsg(
		    					this.getClass().getSimpleName() +
		    					":mousePressed(): No pathway link is available for that clicked point. Click ignored.",
		    					LoggerType.VERBOSE);

		    			return;
		    		}
		    		
		    		iPathwayLevel++;
		    		if (iPathwayLevel >= 3)
		    		{
		    			//fScalingFactor = SCALING_FACTOR;
		    			//bShowBackgroundOverlay = true;
		    			extractClickedPathway(sLink);
		    			//loadPathwayFromFile(sLink);
		    		}	
		    		else
		    		{
		    			loadImageMapFromFile(sLink);
		    		}
		    	}
		    		
				//super.mousePressed(event);
		    }
		}
		
		refGraphModel = new DefaultGraphModel();
		
		refGraphLayoutCache = 
			new GraphLayoutCache(refGraphModel, new GPCellViewFactory(),
					true);

		refPathwayGraph = new JGraph(refGraphModel, refGraphLayoutCache);
		
//		// Set own cell view factory
//		refPathwayGraph.getGraphLayoutCache().setFactory(
//				new GPCellViewFactory());

		// Control-drag should clone selection
		refPathwayGraph.setCloneable(true);
		
		// Turn on anti-aliasing
		//refPathwayGraph.setAntiAliased(true);
		
		refPathwayGraph.setMarqueeHandler(new PathwayMarqueeHandler());
		
		// Create and register Undo Manager
		refUndoManager = new GraphUndoManager();
		refGraphModel.addUndoableEditListener(refUndoManager);
	}
	
	public void drawView() {

		//TODO: add try catch for pathway null object
		if (refCurrentPathway != null)
		{			
			extractVertices(refCurrentPathway);
			extractEdges(refCurrentPathway);

			finishGraphBuilding();
		}	    
//		else if (iPathwayLevel == 1)
//		{ 
//			refCurrentPathwayImageMap = 
//				refGeneralManager.getSingelton().getPathwayManager().getCurrentPathwayImageMap();
//			
//			loadBackgroundOverlayImage(refCurrentPathwayImageMap.getImageLink(),
//					refCurrentPathway);
//		}
		
        // Check if graph is already added to the frame
        if (bGraphSet == false)
        {
            //final Dimension dimOverviewMap = new Dimension(200, 200);
            final Dimension dimPathway = new Dimension(iWidth, iHeight);

        	JScrollPane refScrollPane = new JScrollPane(refPathwayGraph);
        	refScrollPane.setMinimumSize(dimPathway);
        	refScrollPane.setMaximumSize(dimPathway);
        	refScrollPane.setPreferredSize(dimPathway);
        	refScrollPane.setAlignmentX(0.5f);
        	refScrollPane.setAlignmentY(0.5f);
        	refEmbeddedFrame.add(refScrollPane);

    		refOverviewPanel = 
    			new GPOverviewPanel(refPathwayGraph, refScrollPane);
    		
    		//showOverviewMapInNewWindow(dimOverviewMap);
    		
        	bGraphSet = true;
        }
	}
	
	public void createVertex(IPathwayVertexRep vertexRep, Pathway refContainingPathway) {
		
		//create node
		refGraphCell = new DefaultGraphCell(vertexRep);
		
		hashVertexRep2GraphCell.put(vertexRep, refGraphCell);
		
		AttributeMap changedMap = refGraphCell.getAttributes(); 
			
		String sShapeType = vertexRep.getShapeType();
		
		Rectangle2D vertexRect = new Rectangle2D.Double(
				(int)((vertexRep.getXPosition() - (vertexRep.getWidth() / 2))  * fScalingFactor), 
				(int)((vertexRep.getYPosition() - (vertexRep.getHeight() / 2))  * fScalingFactor), 
				vertexRep.getWidth(), vertexRep.getHeight());
		
		if (sShapeType.equals("roundrectangle"))
		{				
			// Set vertex type to round rect
			GPCellViewFactory.setViewClass(
					refGraphCell.getAttributes(), 
					"cerberus.view.gui.swt.pathway.jgraph.JGraphMultilineView");
//					"cerberus.view.gui.swt.pathway.jgraph.JGraphRoundRectView");
 
			GraphConstants.setBackground(changedMap, Color.MAGENTA);
		}
		else if (sShapeType.equals("circle"))
		{	
			// Set vertex type to ellipse
			GPCellViewFactory.setViewClass(
					refGraphCell.getAttributes(), 
					"cerberus.view.gui.swt.pathway.jgraph.JGraphEllipseView");

			if (!bShowBackgroundOverlay)
			{	
				GraphConstants.setAutoSize(changedMap, true);
			}

			GraphConstants.setBackground(changedMap, Color.green);
		}	
		else if (sShapeType.equals("rectangle"))
		{	
			GraphConstants.setBackground(changedMap, new Color(0.53f, 0.81f, 1.0f)); // ligth blue
		}

		GraphConstants.setBounds(changedMap, vertexRect);
		
		// Some global attributes
		GraphConstants.setOpaque(changedMap, true);
		GraphConstants.setSelectable(changedMap, false);
		GraphConstants.setFont(changedMap, new Font("Arial", Font.BOLD, 11));
		//GraphConstants.setAutoSize(refGraphCell.getAttributes(), true);
		
		vecVertices.add(refGraphCell);
		
		vertexIdToCellLUT.put(
				vertexRep.getVertex().getElementId(), refGraphCell);
	}
	
	public void createEdge(int iVertexId1, 
			int iVertexId2, 
			boolean bDrawArrow,
			APathwayEdge refPathwayEdge) {
		
		DefaultPort port1 = new DefaultPort();
		DefaultGraphCell cell1 = vertexIdToCellLUT.get(iVertexId1);
		
		DefaultPort port2 = new DefaultPort();
		DefaultGraphCell cell2 = vertexIdToCellLUT.get(iVertexId2);
		
		if (cell1 == null || cell2 == null)
		{
			System.err.println("Unknown Error during creating edge! SKIP");
			return;
		}
		
		cell1.add(port1);
		cell2.add(port2);
	
		DefaultEdge edge = new DefaultEdge(refPathwayEdge);
		edge.setSource(cell1.getChildAt(0));
		edge.setTarget(cell2.getChildAt(0));
		
		// Retrieve existing edges between nodes
		Object[] existingEdges  = 
			DefaultGraphModel.getEdgesBetween(
					refGraphModel, edge.getSource() , edge.getTarget() ,false); 
		
		// Return if edge of same type between two nodes already exists
		for (int iEdgeCount = 0; iEdgeCount < existingEdges.length; iEdgeCount++)
		{
			if (((APathwayEdge)((DefaultEdge)existingEdges[iEdgeCount]).
					getUserObject()).getEdgeType() == refPathwayEdge.getEdgeType())
			{
				return;
			}
		}
		
		AttributeMap changedMap = edge.getAttributes(); 	
		EdgeLineStyle edgeLineStyle = null;
		EdgeArrowHeadStyle edgeArrowHeadStyle = null;
		Color edgeColor = null;
	    
		GraphConstants.setLineWidth(changedMap, 2);
		GraphConstants.setSelectable(changedMap, false);
//		GraphConstants.setRouting(changedMap, JGraphParallelRouter.getSharedInstance());
//		GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_SIMPLE);
		
		// Differentiate between Relations and Reactions
		if (refPathwayEdge.getEdgeType() == EdgeType.REACTION)
		{
			edgeLineStyle = refRenderStyle.getReactionEdgeLineStyle();
			edgeArrowHeadStyle = refRenderStyle.getReactionEdgeArrowHeadStyle();
			edgeColor = refRenderStyle.getReactionEdgeColor();
			
			GraphConstants.setLineColor(changedMap, edgeColor);
			
			vecReactionEdges.add(edge);
		}
		else if (refPathwayEdge.getEdgeType() == EdgeType.RELATION)
		{
			// In case when relations are maplinks
			if (((PathwayRelationEdge)refPathwayEdge).getEdgeRelationType() 
					== EdgeRelationType.maplink)
			{
				edgeLineStyle = refRenderStyle.getMaplinkEdgeLineStyle();
				edgeArrowHeadStyle = refRenderStyle.getMaplinkEdgeArrowHeadStyle();
				edgeColor = refRenderStyle.getMaplinkEdgeColor();
			}
			else 
			{
				edgeLineStyle = refRenderStyle.getRelationEdgeLineStyle();
				edgeArrowHeadStyle = refRenderStyle.getRelationEdgeArrowHeadStyle();
				edgeColor = refRenderStyle.getRelationEdgeColor();
			}
			
			GraphConstants.setLineColor(changedMap, edgeColor);
			
			vecRelationEdges.add(edge);
			
		}// (refPathwayEdge.getEdgeType() == EdgeType.RELATION)
		
		// Assign render style
	    if (edgeLineStyle == EdgeLineStyle.DASHED)
	    {
	    	GraphConstants.setDashPattern(changedMap, new float[]{4,4});
	    }
	    	
		// Draw arrow
		if (bDrawArrow == true)
		{
			GraphConstants.setLineEnd(
					edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
		}
	    
	    if (edgeArrowHeadStyle == EdgeArrowHeadStyle.FILLED)
	    {
			GraphConstants.setEndFill(changedMap, true);
	    }
		else if (edgeArrowHeadStyle == EdgeArrowHeadStyle.EMPTY)
		{
			GraphConstants.setEndFill(changedMap, false);
		}
		
		refPathwayGraph.getGraphLayoutCache().insert(edge);
	}
	
	public void finishGraphBuilding() {
		
		refPathwayGraph.getGraphLayoutCache().insert(
				vecVertices.toArray());
//		refPathwayGraph.getGraphLayoutCache().insert(
//				vecRelationEdges.toArray());
//		refPathwayGraph.getGraphLayoutCache().insert(
//				vecReactionEdges.toArray());
	}
	
	public void loadPathwayFromFile(int iNewPathwayId) {
		
		super.loadPathwayFromFile(iNewPathwayId);
		
		refCurrentPathway = null;
		refCurrentPathwayImageMap = null;
		resetPathway();

		extractCurrentPathwayFromSet();
		
		refPathwayGraph.setBackgroundImage(null);
		
		showBackgroundOverlay(bShowBackgroundOverlay);
		
		//refGraphLayoutCache.reload();
	}
	
	public void loadImageMapFromFile(String sImageMapPath) {
		
		refCurrentPathway = null;
		refCurrentPathwayImageMap = null;
		resetPathway();
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sImageMapPath);
		
		refCurrentPathwayImageMap = 
			refGeneralManager.getSingelton().getPathwayManager().getCurrentPathwayImageMap();
		
		loadBackgroundOverlayImage(refCurrentPathwayImageMap.getImageLink(), null);
	}
	
	public void zoomOrig() {
		refPathwayGraph.setScale(1.0);
	}
	
	public void zoomIn() {
		
		refPathwayGraph.setScale(1.2 * refPathwayGraph.getScale());
	}
	
	public void zoomOut() {
		
		refPathwayGraph.setScale(refPathwayGraph.getScale() / 1.2);
	}
	
	/**
	 * Method visualizes the neighborhood of a certain cell.
	 * 
	 * BFS Algorithm:
	 * for each vertex v in Q do
	 * for all edges e incident on v do
	 * if edge e is unexplored then
	 * let w be the other endpoint of e.
	 * if vertex w is unexpected then
	 * - mark e as a discovery edge
	 * - insert w into Q
	 * 
	 * @param cell Cell around the neighborhood is shown.
	 * @param iDistance Neighborhood distance.
	 * because the method is called recursive.
	 */
	protected void showNeighbourhoodBFS(DefaultGraphCell cell,
			int iDistance) {
				
		Map<DefaultGraphCell, Map> nested = 
			new Hashtable<DefaultGraphCell, Map>();
		Map attributeMap = new Hashtable();
		
//		// Color mapping will start with red (neigborhood = 1)
//		// For graph parts far away the color will turn to yellow.
//		float fGreenPortion = 1.0f - (iDistance / 10.0f * 3.0f) + 0.3f;
//		
//		if (fGreenPortion < 0.0)
//			fGreenPortion = 0.0f;
//		else if (fGreenPortion > 1.0f)
//			fGreenPortion = 1.0f;
//		GraphConstants.setBackground(
//				attributeMap, new Color(1.0f, fGreenPortion, 0.0f));
		
		ArrayList<DefaultGraphCell> queueBFS = new ArrayList<DefaultGraphCell>();
		ArrayList<DefaultGraphCell> queueBFSNext = new ArrayList<DefaultGraphCell>();
		queueBFS.add(cell);
		
		DefaultGraphCell tmpCell = null;
		List<DefaultGraphCell> neighbourCells = null;
		Iterator<DefaultGraphCell> iterCells = null;
		Color nodeColor = null;
		
		for (int iDistanceIndex = 0; iDistanceIndex <= iDistance; iDistanceIndex++)
		{	
			iterCells = queueBFS.iterator();
			nested.clear();
		
			if (iDistanceIndex == 0)
				nodeColor = refRenderStyle.getHighlightedNodeColor();
			if (iDistanceIndex == 1)
				nodeColor = refRenderStyle.getNeighborhoodNodeColor_1();
			else if (iDistanceIndex == 2)
				nodeColor = refRenderStyle.getNeighborhoodNodeColor_2();
			else if (iDistanceIndex == 3)
				nodeColor = refRenderStyle.getNeighborhoodNodeColor_3();

			GraphConstants.setBackground(attributeMap, nodeColor);
			
			while (iterCells.hasNext())
			{
				tmpCell = iterCells.next();

				if (!hashSetVisitedNeighbors.contains(tmpCell))
				{
					hashSetVisitedNeighbors.add(tmpCell);
					
					neighbourCells = refGraphLayoutCache
						.getNeighbours(tmpCell, hashSetVisitedNeighbors, 
								false, false);

					queueBFSNext.addAll(neighbourCells);

					// Mark cell
					nested.put(tmpCell, attributeMap);
					
					// // Add selected vertex to selection arrays
					iLLSelectedVertices.add(((PathwayVertexRep)tmpCell.
							getUserObject()).getVertex().getElementId());
					iLLNeighborDistance.add(iDistance);
				}
			}
			
			refGraphLayoutCache.edit(nested, null, null, null);
			iNeighbourhoodUndoCount++;
			queueBFS = (ArrayList<DefaultGraphCell>) queueBFSNext.clone();
			queueBFSNext.clear();
		}

		return;
	}
	
	public void highlightCell(final DefaultGraphCell refCell, 
			final Color color) {
		
		Map<DefaultGraphCell, Map> nested = 
			new Hashtable<DefaultGraphCell, Map>();
		Map attributeMap = new Hashtable();
		
		GraphConstants.setBackground(attributeMap, color);
		
		nested.put(refCell, attributeMap);
		refGraphLayoutCache.edit(nested, null, null, null);
	}
	
	/**
	 * Methods puts the overview map in a new JFrame and
	 * displays the frame.
	 * 
	 * @param dim
	 */
	public void showOverviewMapInNewWindow(Dimension dim) {
		
		IViewGLCanvasManager refViewCanvasMng = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		WorkspaceSwingFrame workspaceFrame = 
			refViewCanvasMng.createWorkspace(ManagerObjectType.VIEW_NEW_FRAME, "");
		
        JFrame wnd = (JFrame) workspaceFrame;
        wnd.setLocation(800, 500);
        wnd.setSize(dim);
        wnd.setVisible(true);
        
		wnd.add(refOverviewPanel);
	}

	public void setNeighbourhoodDistance(int iNeighbourhoodDistance) {
	
		this.iNeighbourhoodDistance = iNeighbourhoodDistance;
		
//		//Apply newly selected distance to current selection
//		Iterator<DefaultGraphCell> iterSelectedCells = arSelectedVertices.iterator();
//		while (iterSelectedCells.hasNext())
//		{
//			showNeighbourhoodBFS(iterSelectedCells.next(), iNeighbourhoodDistance);
//		}
	}
	
	public void showHideEdgesByType(boolean bShowEdges, EdgeType edgeType) {

		if (edgeType == EdgeType.REACTION)
		{
			refGraphLayoutCache.setVisible(
					vecRelationEdges.toArray(), bShowEdges);			
		}
		else if (edgeType == EdgeType.RELATION)
		{
			refGraphLayoutCache.setVisible(
					vecReactionEdges.toArray(), bShowEdges);
		}	
	}
	
	public void showBackgroundOverlay(boolean bTurnOn) {
		
		bShowBackgroundOverlay = bTurnOn;
		
		if (bShowBackgroundOverlay == true)
		{		
			// Build current pathway file path of GIF
			String sPathwayImageFilePath = refCurrentPathway.getTitle();
			sPathwayImageFilePath = sPathwayImageFilePath.substring(5);
			sPathwayImageFilePath = "data/images/pathways/" +sPathwayImageFilePath +".gif";

			refGeneralManager.getSingelton().logMsg(
					"Load background pathway from file: " +sPathwayImageFilePath,
					LoggerType.VERBOSE);
			
			// Set background image
			refPathwayGraph.setBackgroundImage(
					new ImageIcon(sPathwayImageFilePath));

			// Set scaling factor so that background image is an direct overlay
			fScalingFactor = 1.0f;
		}
		else
		{
			refPathwayGraph.setBackgroundImage(null);
			fScalingFactor = SCALING_FACTOR;
		}
		
		resetPathway();
		extractCurrentPathwayFromSet();
		// Attention: Performance problem.
		drawView();
		
		// Set edges to visible
		refGraphLayoutCache.setVisible(
				vecReactionEdges.toArray(), !bTurnOn);
		refGraphLayoutCache.setVisible(
				vecRelationEdges.toArray(), !bTurnOn);

	}
	
	public void resetPathway() {
		
		//refCurrentPathway = null;
		//refCurrentPathwayImageMap = null;
		
		refGraphModel = new DefaultGraphModel();
		refPathwayGraph.setModel(refGraphModel);
		refGraphLayoutCache.setModel(refGraphModel);
		refGraphModel.addUndoableEditListener(refUndoManager);
		
		vecVertices.removeAllElements();
		vecRelationEdges.removeAllElements();
		vecReactionEdges.removeAllElements();
		
		iNeighbourhoodUndoCount = 0;
		bNeighbourhoodShown = false;
	}
	
	public void loadBackgroundOverlayImage(String sPathwayImageFilePath, 
			Pathway refCurrentPathway) {
		
		refGeneralManager.getSingelton().logMsg(
				"Load background pathway image from file: " 
				+sPathwayImageFilePath, 
				LoggerType.VERBOSE);
		
		// Set background image
		refPathwayGraph.setBackgroundImage(
				new ImageIcon(sPathwayImageFilePath));

		// Set scaling factor so that background image is an direct overlay
		fScalingFactor = 1.0f;
		
		// Set edges to visible
		refGraphLayoutCache.setVisible(
				vecReactionEdges.toArray(), false);
	}
	
	/**
	 * Method extracts the current pathway from the pathway storage 
	 * and sets the local pathway.
	 * 
	 */
	protected void extractCurrentPathwayFromSet() {
		
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = ((SetFlatThreadSimple)alSetData.get(0)).
			getStorageByDimAndIndex(0, 0);
		
		// Assumes that the storage contains only one pathway item
		refCurrentPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
			getItem(tmpStorage.getArrayInt()[0]);
	}
	
	/**
	 * Method checks is the clicked URL is a pathway.
	 * If it is it calls the extraction method.
	 * 
	 * @param sUrl
	 * @return TRUE if a contained pathway was clicked.
	 */
	protected boolean extractClickedPathway(String sUrl) {
		
		String sSearchPattern1 = "pathway/map/";
		String sSearchPattern2 = "XML/pathways/";
		
		// Check if clicked cell is another pathway
		if (!sUrl.contains((CharSequence)sSearchPattern1) && 
				!sUrl.contains((CharSequence)sSearchPattern2))
		{
			return false;
		}
		
		// Extract pathway clicked pathway ID
		int iPathwayIdIndex = sUrl.lastIndexOf("map00") + 5;
		int iNewPathwayId = StringConversionTool.
			convertStringToInt(sUrl.substring(iPathwayIdIndex, iPathwayIdIndex+3), 0);
		
		IStorage refTmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		//int[] iArPathwayIDs = refTmpStorage.getArrayInt();
		
		int[] tmp = new int[1];
		tmp[0] = iNewPathwayId;
		refTmpStorage.setArrayInt(tmp);
		
//		// Pathway needs to be added (if user selects pathway in upper layer)
//		if (sUrl.contains((CharSequence)sSearchPattern2))
//		{
//			int[] tmp = new int[iArPathwayIDs.length + 1];
//			for(int i = 0; i < iArPathwayIDs.length; i++)
//				tmp[i] = iArPathwayIDs[i];
//			tmp[iArPathwayIDs.length] = iNewPathwayId;
//		}
//		// Current pathway needs to be replaced
//		else
//		{
//			//Replace old pathway ID with new ID
//			for (int index = 0; index < iArPathwayIDs.length; index++)
//			{
//				if (iArPathwayIDs[index] == refCurrentPathway.getPathwayID())
//				{
//					iArPathwayIDs[index] = iNewPathwayId;
//					break;
//				}
//			}	
//		}
		
		//refTmpStorage.setArrayInt(iArPathwayIDs);
		
		// Load pathway
		loadPathwayFromFile(iNewPathwayId);	
	
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;
		
		refGeneralManager.getSingelton().logMsg(
				"2D Pathway update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);

    	// Remove old selected vertices
    	iLLSelectedVertices.clear();
    	//iLLNeighborDistance.clear();
		
		// Read selected vertex IDs
		int[] iArSelectedElements = refSetSelection.getSelectionIdArray();
		
		// Read neighbor data
		//int[] iArSelectionNeighborDistance = selectionSet.getOptionalDataArray();
		
		for (int iSelectedVertexIndex = 0; 
			iSelectedVertexIndex < ((IStorage)refSetSelection.getStorageByDimAndIndex(0, 0)).getSize(StorageType.INT);
			iSelectedVertexIndex++)
		{			
	    	
			PathwayVertex selectedVertex = refGeneralManager.getSingelton().getPathwayElementManager().
				getVertexLUT().get(iArSelectedElements[iSelectedVertexIndex]);
			
	    	// FIXME: name of the method is not good because inside 
			// resetPathway() and drawPathway() are called.
			showBackgroundOverlay(bShowBackgroundOverlay);
			
//	    	//ATTENTION: Performance problem!
//	    	resetPathway();
//	    	drawView();
			
			// Ignore vertex if is NOT in the current pathway!
	    	if (!refCurrentPathway.isVertexInPathway(selectedVertex))
	    		return;
			
	    	iLLSelectedVertices.add(selectedVertex.getElementId());
	    	    		
			highlightCell(hashVertexRep2GraphCell.get(
					selectedVertex.getVertexRepByIndex(0)), Color.RED);
			
			bNeighbourhoodShown = true;
			iNeighbourhoodUndoCount++;
			
		}
	}
}
