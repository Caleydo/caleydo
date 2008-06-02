package org.caleydo.core.view.swt.pathway.jgraph;

import gleem.linalg.Vec3f;

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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.data.collection.set.SetFlatThreadSimple;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.graph.item.vertex.EPathwayVertexShape;
import org.caleydo.core.data.graph.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.swt.pathway.APathwayGraphViewRep;
import org.caleydo.core.view.swt.widget.SWTEmbeddedGraphWidget;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
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

/**
 * In this class the real drawing of the Pathway happens. For the drawing the
 * JGraph package is used. We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 */

public class PathwayGraphViewRep 
extends APathwayGraphViewRep {

	/**
	 * Pathway element positions are read from XML files. The scaling factor can
	 * scale the positions to blow up or shrink the pathway.
	 */
	protected static final float SCALING_FACTOR = 1.15f;

	protected float fScalingFactor = 1.0f;
	
	protected PathwayGraph currentPathway;

	protected GraphModel graphModel;

	protected GraphLayoutCache graphLayoutCache;

	protected JGraph pathwayGraph;

	protected HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;

	protected boolean bGraphSet = false;

	protected boolean bShowBackgroundOverlay = true;

	protected GraphUndoManager undoManager;

	protected GPOverviewPanel overviewPanel;

	protected Vector<DefaultEdge> vecRelationEdges;

	protected Vector<DefaultEdge> vecReactionEdges;

	protected Vector<DefaultGraphCell> vecVertices;

	/**
	 * Holds the cell that was recently clicked by the user using the mouse. The
	 * variable is needed for updating the neighborhood distance in the menu on
	 * the fly without selecting a new cell.
	 */
	protected DefaultGraphCell lastClickedGraphCell;

	/**
	 * Integer storage of selected vertices Container is needed for selection
	 * updates.
	 */
	protected LinkedList<Integer> iLLSelectedVertices;

	/**
	 * Neighbor distance from currently selected vertex. Container is needed for
	 * selection updates.
	 */
	protected LinkedList<Integer> iLLNeighborDistance;

	/**
	 * Specifies how deep the neighborhood recursion should visualize
	 * surrounding elements. Default value is 0.
	 */
	protected int iNeighbourhoodDistance = 0;

	/**
	 * Counts how often the neighbour recursion is called. This is needed for
	 * the UNDO operation on the next selection.
	 */
	protected int iNeighbourhoodUndoCount = 0;

	/**
	 * Flag shows if the neighbours of a cell are currently displayed. Flag is
	 * needed for UNDO of previous neigbour highlighning.
	 */
	protected boolean bNeighbourhoodShown = false;

	protected HashSet<DefaultGraphCell> hashSetVisitedNeighbors;

	protected HashMap<PathwayVertexGraphItemRep, DefaultGraphCell> hashVertexRep2GraphCell;

	protected boolean bShowReactionEdges = false;

	protected boolean bShowRelationEdges = false;

	public PathwayGraphViewRep(IGeneralManager generalManager,
			int iParentContainerId) {

		super(generalManager, -1, iParentContainerId, "");

		vertexIdToCellLUT = new HashMap<Integer, DefaultGraphCell>();

		vecRelationEdges = new Vector<DefaultEdge>();
		vecReactionEdges = new Vector<DefaultEdge>();
		vecVertices = new Vector<DefaultGraphCell>();

		fScalingFactor = SCALING_FACTOR;

		iLLSelectedVertices = new LinkedList<Integer>();
		iLLNeighborDistance = new LinkedList<Integer>();

		hashVertexRep2GraphCell = new HashMap<PathwayVertexGraphItemRep, DefaultGraphCell>();

		hashSetVisitedNeighbors = new HashSet<DefaultGraphCell>();
	}

	/**
	 * Method uses the parent container ID to retrieve the GUI widget by calling
	 * the createWidget method from the SWT GUI Manager.
	 * 
	 * @see org.caleydo.core.view.AViewRep#retrieveGUIContainer()
	 * @see org.caleydo.core.view.IView#initView()
	 */
	public void initView() {

		SWTEmbeddedGraphWidget sWTEmbeddedGraphWidget = (SWTEmbeddedGraphWidget) generalManager
				.getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET,
						embeddedFrameComposite, iWidth, iHeight);

		sWTEmbeddedGraphWidget.createEmbeddedComposite();
		embeddedFrame = sWTEmbeddedGraphWidget.getEmbeddedFrame();
		
		extractCurrentPathwayFromSet();

		class PathwayMarqueeHandler extends BasicMarqueeHandler {

			public boolean isForceMarqueeEvent(MouseEvent event) {

				if (SwingUtilities.isLeftMouseButton(event)
						&& event.getClickCount() == 1)
					return true;
				else
					return super.isForceMarqueeEvent(event);
			}

			public void mousePressed(final MouseEvent event) {

				if (currentPathway != null)
				{
					DefaultGraphCell clickedCell = (DefaultGraphCell) pathwayGraph
							.getFirstCellForLocation(event.getX(), event.getY());

					// Do nothing when there no specific node was clicked.
					if (clickedCell == null)
						return;

					lastClickedGraphCell = clickedCell;

					// Check if cell has an user object attached
					if (lastClickedGraphCell.getUserObject() == null)
					{
						super.mousePressed(event);
						return;
					}

					if (!clickedCell.getUserObject().getClass().getSimpleName()
							.equals(PathwayVertexGraphItemRep.class.getSimpleName()))
					{
						super.mousePressed(event);
						return;
					}

					processSelectedCell();

					ArrayList<Integer> iAlSelectedVertices = new ArrayList<Integer>();
					ArrayList<Integer> iAlNeighborDistance = new ArrayList<Integer>();

					// Convert Link List to int[]
					Iterator<Integer> iter_I = iLLSelectedVertices.iterator();
					for (int i = 0; iter_I.hasNext(); i++)
					{
						iAlSelectedVertices.add(iter_I.next());//.intValue();
					}

					iter_I = iLLNeighborDistance.iterator();
					//iArNeighborDistance = new int[iLLNeighborDistance.size()];
					for (int i = 0; iter_I.hasNext(); i++)
					{
						iAlNeighborDistance.add(iter_I.next());//.intValue();
					}

					ArrayList<Integer> iAlPathway = new ArrayList<Integer>(1);
					iAlPathway.add(currentPathway.getId());
					alSetSelection.get(0).updateSelectionSet(iParentContainerId,
							iAlSelectedVertices, 
							iAlNeighborDistance,
							iAlPathway);

				}// if(currentPathway != 0)
				else if (currentPathwayImageMap != null)
				{
					String sLink = currentPathwayImageMap
							.processPoint(new Point(event.getX(), event.getY()));

					if (sLink == null || sLink.equals(""))
					{
//						generalManager.logMsg(
//							this.getClass().getSimpleName()
//							+ ":mousePressed(): No pathway link is available for that clicked point. Click ignored.",
//							LoggerType.VERBOSE);

						return;
					}

					// Append file path
					sLink = generalManager
								.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG)
									.getImageMapPath() + sLink;

					iPathwayLevel++;
					if (iPathwayLevel >= 3)
					{
						// fScalingFactor = SCALING_FACTOR;
						// bShowBackgroundOverlay = true;
						extractClickedPathway(sLink);
						// loadPathwayFromFile(sLink);
					} else
					{
						loadImageMapFromFile(sLink);
					}
				}
			}
		}

		graphModel = new DefaultGraphModel();

		graphLayoutCache = new GraphLayoutCache(graphModel,
				new GPCellViewFactory(), true);

		pathwayGraph = new JGraph(graphModel, graphLayoutCache);

		// // Set own cell view factory
		// pathwayGraph.getGraphLayoutCache().setFactory(
		// new GPCellViewFactory());

		// Control-drag should clone selection
		pathwayGraph.setCloneable(true);

		// Turn on anti-aliasing
		pathwayGraph.setAntiAliased(true);

		pathwayGraph.setMarqueeHandler(new PathwayMarqueeHandler());

		// Create and register Undo Manager
		undoManager = new GraphUndoManager();
		graphModel.addUndoableEditListener(undoManager);
	}

	protected void initViewSwtComposit(Composite swtContainer) {

		throw new RuntimeException("Class can not be called via RCP!");
	}

	protected void processSelectedCell() {

		// Remove old selected vertices
		iLLSelectedVertices.clear();
		iLLNeighborDistance.clear();

		// Check if a node or edge was hit.
		// If not undo neighborhood visualization and return.
		if (lastClickedGraphCell == null)
		{
			for (int iUndoCount = 0; iUndoCount < iNeighbourhoodUndoCount; iUndoCount++)
			{
				undoManager.undo(graphLayoutCache);
			}

			iNeighbourhoodUndoCount = 0;
			bNeighbourhoodShown = false;
			return;
		}

		// Check if clicked object is a cell
		if (!lastClickedGraphCell.getUserObject().getClass().equals(
				PathwayVertexGraphItemRep.class))
		{
			return;
		}

		final String sUrl = ((PathwayVertexGraphItemRep) lastClickedGraphCell
				.getUserObject()).getPathwayVertexGraphItem().getExternalLink();
		
		if(((PathwayVertexGraphItemRep) lastClickedGraphCell.getUserObject())
				.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.map))
		{
			extractClickedPathway(sUrl);
		}
		else
		{
			loadNodeInformationInBrowser(sUrl);

			// UNDO old neighborhood visualization
			if (bNeighbourhoodShown == true)
			{
				for (int iUndoCount = 0; iUndoCount < iNeighbourhoodUndoCount; iUndoCount++)
				{
					undoManager.undo(graphLayoutCache);
				}
				iNeighbourhoodUndoCount = 0;
				bNeighbourhoodShown = false;
			}

			// Highlight current cell
			highlightCell(lastClickedGraphCell, new Vec3f(1, 0, 0));
//			dataMappingTest(((PathwayVertexRep) lastClickedGraphCell
//					.getUserObject()));

			// The clicked vertex will be added with neighborhood distance of 0
			iLLNeighborDistance.add(0);

			bNeighbourhoodShown = true;
			iNeighbourhoodUndoCount++;
			
			// Handle multiple gene mapping in selection
			String sTmpGene = ((PathwayVertexGraphItemRep) lastClickedGraphCell.getUserObject()).getPathwayVertexGraphItem().getName();
			
//			if (!sTmpGene.contains("hsa:"))
//			{
//				// Add selected vertex itself because neighborhood algorithm
//				// only adds neighbor vertices.
//				iLLSelectedVertices.add(((PathwayVertexGraphItemRep) lastClickedGraphCell
//						.getUserObject()).getPathwayVertexGraphItem().getId());
//			}
			
			int iTmpGeneId = 0;
			while (sTmpGene.contains("hsa:"))
			{
				if (sTmpGene.contains(" "))
				{
					// Multiple genes
					iTmpGeneId = Integer.valueOf(sTmpGene.substring(
						sTmpGene.indexOf("hsa:") + 4, sTmpGene.indexOf(' ')));
					
					sTmpGene = sTmpGene.substring(sTmpGene.indexOf(' ') + 1);
				}
				else
				{
					// Only one gene is contained in string
					iTmpGeneId = Integer.valueOf(sTmpGene.substring(4));
					sTmpGene = "";
				}	

				//iLLSelectedVertices.add(iTmpGeneId);
			}
			
			iLLSelectedVertices.add(
					((PathwayVertexGraphItemRep) lastClickedGraphCell.getUserObject()).getId());

			if (iNeighbourhoodDistance != 0)
			{
				showNeighbourhoodBFS(iNeighbourhoodDistance);
				bNeighbourhoodShown = true;
			}
		}// if(sUrl.contains((CharSequence)sSearchPattern))
	}

	public void drawView() {

		// TODO: add try catch for pathway null object
		if (currentPathway != null)
		{
			extractVertices(currentPathway);
			//extractEdges(currentPathway);

			finishGraphBuilding();
			pathwayGraph.repaint();
		}
		// else if (iPathwayLevel == 1)
		// {
		// currentPathwayImageMap =
		// generalManager.getSingelton().getPathwayManager().getCurrentPathwayImageMap();
		//			
		// loadBackgroundOverlayImage(currentPathwayImageMap.getImageLink(),
		// currentPathway);
		// }

		// Check if graph is already added to the frame
		if (bGraphSet == false)
		{
			// final Dimension dimOverviewMap = new Dimension(200, 200);
			final Dimension dimPathway = new Dimension(iWidth, iHeight);

			JScrollPane scrollPane = new JScrollPane(pathwayGraph);
			scrollPane.setMinimumSize(dimPathway);
			scrollPane.setMaximumSize(dimPathway);
			scrollPane.setPreferredSize(dimPathway);
			scrollPane.setAlignmentX(0.5f);
			scrollPane.setAlignmentY(0.5f);
			embeddedFrame.add(scrollPane);

			overviewPanel = new GPOverviewPanel(pathwayGraph,
					scrollPane);

			// showOverviewMapInNewWindow(dimOverviewMap);

			bGraphSet = true;
		}
	}

	@SuppressWarnings("unchecked")
	public void createVertex(PathwayVertexGraphItemRep vertexRep,
			PathwayGraph containingPathway) {

		// create node
		DefaultGraphCell graphCell = new DefaultGraphCell(vertexRep);


		if(((PathwayVertexGraphItemRep) graphCell.getUserObject())
				.getPathwayVertexGraphItem().getType().equals(EPathwayVertexType.group))
		{
			// Ignore KEGG groups
			return;
		}
		
		hashVertexRep2GraphCell.put(vertexRep, graphCell);

		AttributeMap changedMap = graphCell.getAttributes();

		EPathwayVertexShape shape = vertexRep.getShapeType();

		Rectangle2D vertexRect = null;

		if (shape.equals(EPathwayVertexShape.roundrectangle))
		{
			vertexRect = new Rectangle2D.Double(
						(int) ((vertexRep.getXOrigin() - (vertexRep.getWidth() / 2)) * fScalingFactor),
						(int) ((vertexRep.getYOrigin() - (vertexRep.getHeight() / 2)) * fScalingFactor),
						vertexRep.getWidth(), vertexRep.getHeight());
			
			// Set vertex type to round rect
			GPCellViewFactory.setViewClass(graphCell.getAttributes(),
					"org.caleydo.core.view.swt.pathway.jgraph.JGraphMultilineView");

			Vec3f tmpColor = renderStyle.getPathwayNodeColor(false);
			GraphConstants.setBackground(changedMap, new Color(tmpColor.x(), tmpColor.y(), tmpColor.z()));
		} 
		else if (shape.equals(EPathwayVertexShape.circle))
		{
			vertexRect = new Rectangle2D.Double(
						(int) ((vertexRep.getXOrigin() - (vertexRep.getWidth() / 2)) * fScalingFactor),
						(int) ((vertexRep.getYOrigin() - (vertexRep.getHeight() / 2)) * fScalingFactor),
						vertexRep.getWidth(), vertexRep.getHeight());
			
			// Set vertex type to ellipse
			GPCellViewFactory.setViewClass(graphCell.getAttributes(),
					"org.caleydo.core.view.swt.pathway.jgraph.JGraphEllipseView");

			if (!bShowBackgroundOverlay)
			{
				GraphConstants.setAutoSize(changedMap, true);
			}

			Vec3f tmpColor = renderStyle.getCompoundNodeColor(false);
			GraphConstants.setBackground(changedMap, new Color(tmpColor.x(), tmpColor.y(), tmpColor.z()));
		} 
		else if (shape.equals(EPathwayVertexShape.rectangle))
		{
			vertexRect = new Rectangle2D.Double(
						(int) ((vertexRep.getXOrigin() - (vertexRep.getWidth() / 2)) * fScalingFactor),
						(int) ((vertexRep.getYOrigin() - (vertexRep.getHeight() / 2)) * fScalingFactor),
						renderStyle.getEnzymeNodeWidth(false), renderStyle.getEnzymeNodeHeight(false));
			
			Vec3f tmpColor = renderStyle.getEnzymeNodeColor(false);
			GraphConstants.setBackground(changedMap, new Color(tmpColor.x(), tmpColor.y(), tmpColor.z()));
		}
		else
		{
			// unhandled shape -> SKIP
			return;
		}
		
		GraphConstants.setBounds(changedMap, vertexRect);

		// Some global attributes
		GraphConstants.setOpaque(changedMap, true);
		GraphConstants.setSelectable(changedMap, false);
		GraphConstants.setFont(changedMap, new Font("Arial", Font.BOLD, 11));
		// GraphConstants.setAutoSize(graphCell.getAttributes(), true);

		vecVertices.add(graphCell);

		vertexIdToCellLUT.put(vertexRep.getPathwayVertexGraphItem().getId(),
				graphCell);
	}

//	public void createEdge(int iVertexId1, int iVertexId2, boolean bDrawArrow,
//			APathwayEdge pathwayEdge) {
//
//		DefaultPort port1 = new DefaultPort();
//		DefaultGraphCell cell1 = vertexIdToCellLUT.get(iVertexId1);
//
//		DefaultPort port2 = new DefaultPort();
//		DefaultGraphCell cell2 = vertexIdToCellLUT.get(iVertexId2);
//
//		if (cell1 == null || cell2 == null)
//		{
//			System.err.println("Unknown Error during creating edge! SKIP");
//			return;
//		}
//
//		cell1.add(port1);
//		cell2.add(port2);
//
//		DefaultEdge edge = new DefaultEdge(pathwayEdge);
//		edge.setSource(cell1.getChildAt(0));
//		edge.setTarget(cell2.getChildAt(0));
//
//		// Retrieve existing edges between nodes
//		Object[] existingEdges = DefaultGraphModel.getEdgesBetween(
//				graphModel, edge.getSource(), edge.getTarget(), false);
//
//		// Return if edge of same type between two nodes already exists
//		for (int iEdgeCount = 0; iEdgeCount < existingEdges.length; iEdgeCount++)
//		{
//			if (((APathwayEdge) ((DefaultEdge) existingEdges[iEdgeCount])
//					.getUserObject()).getEdgeType() == pathwayEdge
//					.getEdgeType())
//			{
//				return;
//			}
//		}
//
//		AttributeMap changedMap = edge.getAttributes();
//		EdgeLineStyle edgeLineStyle = null;
//		EdgeArrowHeadStyle edgeArrowHeadStyle = null;
//		Color edgeColor = null;
//
//		GraphConstants.setLineWidth(changedMap, 2);
//		GraphConstants.setSelectable(changedMap, false);
//		// GraphConstants.setRouting(changedMap,
//		// JGraphParallelRouter.getSharedInstance());
//		// GraphConstants.setRouting(edge.getAttributes(),
//		// GraphConstants.ROUTING_SIMPLE);
//
//		// Differentiate between Relations and Reactions
//		if (pathwayEdge.getEdgeType() == EdgeType.REACTION)
//		{
//			edgeLineStyle = renderStyle.getReactionEdgeLineStyle();
//			edgeArrowHeadStyle = renderStyle.getReactionEdgeArrowHeadStyle();
//			edgeColor = renderStyle.getReactionEdgeColor();
//
//			GraphConstants.setLineColor(changedMap, edgeColor);
//
//			vecReactionEdges.add(edge);
//		} else if (pathwayEdge.getEdgeType() == EdgeType.RELATION)
//		{
//			// In case when relations are maplinks
//			if (((PathwayRelationEdge) pathwayEdge).getEdgeRelationType() == EdgeRelationType.maplink)
//			{
//				edgeLineStyle = renderStyle.getMaplinkEdgeLineStyle();
//				edgeArrowHeadStyle = renderStyle
//						.getMaplinkEdgeArrowHeadStyle();
//				edgeColor = renderStyle.getMaplinkEdgeColor();
//			} else
//			{
//				edgeLineStyle = renderStyle.getRelationEdgeLineStyle();
//				edgeArrowHeadStyle = renderStyle
//						.getRelationEdgeArrowHeadStyle();
//				edgeColor = renderStyle.getRelationEdgeColor();
//			}
//
//			GraphConstants.setLineColor(changedMap, edgeColor);
//
//			vecRelationEdges.add(edge);
//
//		}// (pathwayEdge.getEdgeType() == EdgeType.RELATION)
//
//		// Assign render style
//		if (edgeLineStyle == EdgeLineStyle.DASHED)
//		{
//			GraphConstants.setDashPattern(changedMap, new float[]
//			{ 4, 4 });
//		}
//
//		// Draw arrow
//		if (bDrawArrow == true)
//		{
//			GraphConstants.setLineEnd(edge.getAttributes(),
//					GraphConstants.ARROW_TECHNICAL);
//		}
//
//		if (edgeArrowHeadStyle == EdgeArrowHeadStyle.FILLED)
//		{
//			GraphConstants.setEndFill(changedMap, true);
//		} else if (edgeArrowHeadStyle == EdgeArrowHeadStyle.EMPTY)
//		{
//			GraphConstants.setEndFill(changedMap, false);
//		}
//
//		pathwayGraph.getGraphLayoutCache().insert(edge);
//	}

	public void finishGraphBuilding() {

		try
		{
			pathwayGraph.getGraphLayoutCache().insert(vecVertices.toArray());
			// pathwayGraph.getGraphLayoutCache().insert(
			// vecRelationEdges.toArray());
			// pathwayGraph.getGraphLayoutCache().insert(
			// vecReactionEdges.toArray());

		} catch (NullPointerException npe)
		{
//			generalManager
//					.logMsg("Error while rendering JGraph part!",
//							LoggerType.ERROR);

			System.out.println("PathwayGraphViewRep.finishGraphBuilding() ERROR! : " + vecVertices.toString());

			npe.printStackTrace();
		}
	}

	public void loadPathwayFromFile(int iNewPathwayId) {

//		//Load pathway
//		boolean bLoadingOK = 
//			generalManager.getSingelton().getPathwayManager().loadPathwayById(iNewPathwayId);
//		
//		if (!bLoadingOK)
//			return;
//		
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArTmp = new int[1];
		iArTmp[0] = iNewPathwayId;
		tmpStorage.setArrayInt(iArTmp);
		
		// Clean up
		currentPathway = null;
		currentPathwayImageMap = null;
		lastClickedGraphCell = null;
		resetPathway();

		extractCurrentPathwayFromSet();

		pathwayGraph.setBackgroundImage(null);

		showBackgroundOverlay(bShowBackgroundOverlay);
	}

	public void loadImageMapFromFile(String sImageMapPath) {

		currentPathway = null;
		currentPathwayImageMap = null;
		resetPathway();

		generalManager.getXmlParserManager()
				.parseXmlFileByName(sImageMapPath);

		currentPathwayImageMap = generalManager
				.getPathwayManager().getCurrentPathwayImageMap();

		loadBackgroundOverlayImage(generalManager
				.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG)
						.getImageMapPath() + currentPathwayImageMap.getImageLink());

		pathwayGraph.repaint();
	}

	public void zoomOrig() {

		pathwayGraph.setScale(1.0);
	}

	public void zoomIn() {

		pathwayGraph.setScale(1.2 * pathwayGraph.getScale());
	}

	public void zoomOut() {

		pathwayGraph.setScale(pathwayGraph.getScale() / 1.2);
	}

	/**
	 * Method visualizes the neighborhood of a certain cell. The last clicked
	 * cell is stored in the lastClickedGraphCell member variable.
	 * 
	 * BFS Algorithm: for each vertex v in Q do for all edges e incident on v do
	 * if edge e is unexplored then let w be the other endpoint of e. if vertex
	 * w is unexpected then - mark e as a discovery edge - insert w into Q
	 * 
	 * @param iDistance
	 *            Neighborhood distance. because the method is called recursive.
	 */
	@SuppressWarnings("unchecked")
	public void showNeighbourhoodBFS(int iDistance) {

		Map<DefaultGraphCell, Map> nested = new Hashtable<DefaultGraphCell, Map>();
		Map attributeMap = new Hashtable();

		hashSetVisitedNeighbors.clear();
		// hashSetVisitedNeighbors.add(lastClickedGraphCell);

		ArrayList<DefaultGraphCell> queueBFS = new ArrayList<DefaultGraphCell>();
		ArrayList<DefaultGraphCell> queueBFSNext = new ArrayList<DefaultGraphCell>();
		queueBFS.add(lastClickedGraphCell);

		DefaultGraphCell tmpCell = null;
		DefaultEdge tmpEdge = null;
		List<DefaultGraphCell> neighbourCells = null;
		ArrayList<DefaultGraphCell> filteredNeighborCells = new ArrayList<DefaultGraphCell>();
		Iterator<DefaultGraphCell> iterCells = null;
		Color nodeColor = null;

		for (int iDistanceIndex = 0; iDistanceIndex <= iDistance; iDistanceIndex++)
		{
			iterCells = queueBFS.iterator();
			nested.clear();

			if (iDistanceIndex < PathwayRenderStyle.neighborhoodNodeColorArraysize)
			{
				Vec3f tmpColor = renderStyle.getNeighborhoodNodeColorByDepth(iDistanceIndex);
				nodeColor = new Color(tmpColor.x(), tmpColor.y(), tmpColor.z());
			} else
			{
				assert false : "can not find color for selection depth";
			}

			GraphConstants.setBackground(attributeMap, nodeColor);

			while (iterCells.hasNext())
			{
				tmpCell = iterCells.next();

				if (!hashSetVisitedNeighbors.contains(tmpCell))
				{
					hashSetVisitedNeighbors.add(tmpCell);

					neighbourCells = graphLayoutCache.getNeighbours(tmpCell,
							hashSetVisitedNeighbors, false, false);

					List<DefaultEdge> listEdges = graphLayoutCache
							.getOutgoingEdges(tmpCell, null, false, false);

					listEdges.addAll(graphLayoutCache.getIncomingEdges(
							tmpCell, null, false, false));

					for (int iEdgeIndex = 0; iEdgeIndex < listEdges.size(); iEdgeIndex++)
					{
						tmpEdge = listEdges.get(iEdgeIndex);

						// Add cells from neighbors that are
						// connected by a visible edge
						if (graphLayoutCache.isVisible(tmpEdge) || bShowBackgroundOverlay)
						{
							if (neighbourCells.contains(((DefaultPort) (tmpEdge.getSource())).getParent()))
							{
								filteredNeighborCells.add((DefaultGraphCell)((DefaultPort) (tmpEdge.getSource())).getParent());
							} 
							else if (neighbourCells.contains(((DefaultPort) (tmpEdge.getTarget())).getParent()))
							{
								filteredNeighborCells.add((DefaultGraphCell)((DefaultPort) (tmpEdge.getTarget())).getParent());								
							}
						}
					}

					queueBFSNext.addAll(filteredNeighborCells);

					// Mark cell
					nested.put(tmpCell, attributeMap);

					// // Add selected vertex to selection arrays
					iLLSelectedVertices.add(((PathwayVertexGraphItemRep) tmpCell
							.getUserObject()).getId());
					iLLNeighborDistance.add(iDistanceIndex);
				}
			}

			graphLayoutCache.edit(nested, null, null, null);
			iNeighbourhoodUndoCount++;
			queueBFS = (ArrayList<DefaultGraphCell>) queueBFSNext.clone();
			queueBFSNext.clear();
		}

		return;
	}

	public void highlightCell(final DefaultGraphCell cell, final Vec3f color) {

		Map<DefaultGraphCell, Map> nested = new Hashtable<DefaultGraphCell, Map>();
		Map attributeMap = new Hashtable();

		Color tmpColor = new Color(color.x(), color.y(), color.z());
		//GraphConstants.setBackground(attributeMap, color);
		GraphConstants.setBorder(attributeMap,
				BorderFactory.createLineBorder(tmpColor) );
		
		nested.put(cell, attributeMap);
		graphLayoutCache.edit(nested, null, null, null);
	}

	/**
	 * Methods puts the overview map in a new JFrame and displays the frame.
	 * 
	 * @param dim
	 */
	public void showOverviewMapInNewWindow(Dimension dim) {

		IViewGLCanvasManager viewCanvasMng = generalManager.getViewGLCanvasManager();
		JFrame workspaceFrame = viewCanvasMng.createWorkspace(
				ManagerObjectType.VIEW_NEW_FRAME, "");

		JFrame wnd = (JFrame) workspaceFrame;
		wnd.setLocation(800, 500);
		wnd.setSize(dim);
		wnd.setVisible(true);

		wnd.add(overviewPanel);
	}

	public void setNeighbourhoodDistance(int iNeighbourhoodDistance) {

		this.iNeighbourhoodDistance = iNeighbourhoodDistance;

		// Update neighborhood visualization on the fly
		processSelectedCell();

		ArrayList<Integer> iAlSelectedVertices = new ArrayList<Integer>();
		ArrayList<Integer> iAlNeighborDistance = new ArrayList<Integer>();

		// Convert Link List to int[]
		Iterator<Integer> iter_I = iLLSelectedVertices.iterator();
		//iArSelectedVertices = new int[iLLSelectedVertices.size()];
		for (int i = 0; iter_I.hasNext(); i++)
		{
			iAlSelectedVertices.add(iter_I.next());
		}

		iter_I = iLLNeighborDistance.iterator();
		//iAlNeighborDistance = new int[iLLNeighborDistance.size()];
		for (int i = 0; iter_I.hasNext(); i++)
		{
			iAlNeighborDistance.add(iter_I.next());//.intValue();
		}

		alSetSelection.get(0).updateSelectionSet(iParentContainerId,
				iAlSelectedVertices, null, iAlNeighborDistance);
	}

//	public void showHideEdgesByType(boolean bShowEdges, EdgeType edgeType) {
//
//		graphModel.removeUndoableEditListener(undoManager);
//
//		if (edgeType == EdgeType.REACTION)
//		{
//			graphLayoutCache.setVisible(vecRelationEdges.toArray(),
//					bShowEdges);
//
//			bShowReactionEdges = bShowEdges;
//		} else if (edgeType == EdgeType.RELATION)
//		{
//			graphLayoutCache.setVisible(vecReactionEdges.toArray(),
//					bShowEdges);
//
//			bShowRelationEdges = bShowEdges;
//		}
//
//		graphModel.addUndoableEditListener(undoManager);
//		
//		processSelectedCell();
//	}
//
//	public boolean getEdgeVisibilityStateByType(EdgeType edgeType) {
//
//		if (edgeType == EdgeType.REACTION)
//		{
//			return (bShowReactionEdges);
//		} else if (edgeType == EdgeType.RELATION)
//		{
//			return (bShowRelationEdges);
//		}
//
//		assert false : "Invalid edge type specified!";
//		return false;
//	}

	public void showBackgroundOverlay(boolean bTurnOn) {

		if (currentPathway == null)
			return;

		bShowBackgroundOverlay = bTurnOn;

		if (bShowBackgroundOverlay == true)
		{
			// Build current pathway file path of GIF
			String sPathwayImageFilePath = currentPathway.getName();
			sPathwayImageFilePath = sPathwayImageFilePath.substring(5);
			sPathwayImageFilePath = generalManager
					.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG)
							.getImagePath() + sPathwayImageFilePath + ".gif";

//			generalManager.logMsg(
//					"Load background pathway from file: "
//							+ sPathwayImageFilePath, LoggerType.VERBOSE);

			// Set background image
			if (this.getClass().getClassLoader().getResource(sPathwayImageFilePath) != null)
			{
				pathwayGraph.setBackgroundImage(new ImageIcon(
						this.getClass().getClassLoader().getResource(sPathwayImageFilePath)));
			}
			else
			{
				pathwayGraph.setBackgroundImage(new ImageIcon(
						sPathwayImageFilePath));
			}

			// Set scaling factor so that background image is a direct overlay
			fScalingFactor = 1.0f;
		} else
		{
			pathwayGraph.setBackgroundImage(null);
			fScalingFactor = SCALING_FACTOR;
		}

		resetPathway();
		hashVertexRep2GraphCell.clear();
		// extractCurrentPathwayFromSet();
		// Attention: Performance problem.
		drawView();

//		// Adapt edge visiblitly state
//		showHideEdgesByType(bShowReactionEdges, EdgeType.REACTION);
//		showHideEdgesByType(bShowRelationEdges, EdgeType.RELATION);

		if (lastClickedGraphCell != null)
		{
			// Check if selected cell is a vertex and if it is valid
			if (lastClickedGraphCell.getUserObject().getClass()
				.equals(PathwayVertexGraphItemRep.class))
			{
				// Map previously selected cell to new pathway JGraph.
				lastClickedGraphCell = hashVertexRep2GraphCell
						.get((PathwayVertexGraphItemRep) lastClickedGraphCell.getUserObject());

				// Rehighlight previously selected cells
				processSelectedCell();
			}
		}
	}

	public void resetPathway() {

		// currentPathway = null;
		// currentPathwayImageMap = null;

		graphModel = new DefaultGraphModel();
		pathwayGraph.setModel(graphModel);
		graphLayoutCache.setModel(graphModel);

		// Recreate and register Undo Manager
		// undoManager = new GraphUndoManager();
		graphModel.addUndoableEditListener(undoManager);

		vecVertices.removeAllElements();
		vecRelationEdges.removeAllElements();
		vecReactionEdges.removeAllElements();

		iNeighbourhoodUndoCount = 0;
		bNeighbourhoodShown = false;
	}

	public void loadBackgroundOverlayImage(String sPathwayImageFilePath) {

//		generalManager.logMsg(
//				"Load background pathway image from file: "
//						+ sPathwayImageFilePath, LoggerType.VERBOSE);

		// Set background image
		pathwayGraph.setBackgroundImage(new ImageIcon(sPathwayImageFilePath));

		// Set scaling factor so that background image is an direct overlay
		fScalingFactor = 1.0f;

		// // Set edges to visible
		// graphLayoutCache.setVisible(
		// vecReactionEdges.toArray(), false);
	}

	/**
	 * Method extracts the current pathway from the pathway storage and sets the
	 * local pathway.
	 * 
	 */
	protected void extractCurrentPathwayFromSet() {

		if (!alSetData.isEmpty())
		{
			// Assumes that the set consists of only one storage
			IStorage tmpStorage = ((SetFlatThreadSimple) alSetData.get(0))
				.getStorageByDimAndIndex(0, 0);

			// Assumes that the storage contains only one pathway item
//			//Load pathway
//			boolean bLoadingOK = generalManager.getPathwayManager().get(tmpStorage.getArrayInt()[0]);
//			
//			if (!bLoadingOK)
//				return;

			currentPathway = (PathwayGraph) generalManager
				.getPathwayManager().getItem(tmpStorage.getArrayInt()[0]);
			
			return;
		}

//		generalManager.logMsg("no valid Set",
//				LoggerType.ERROR);
	}

	/**
	 * Method checks is the clicked URL is a pathway. If it is it calls the
	 * extraction method.
	 * 
	 * @param sUrl
	 * @return TRUE if a contained pathway was clicked.
	 */
	protected boolean extractClickedPathway(String sUrl) {

		int iPathwayIdIndex = 0;

		// Extract clicked pathway ID
		if (sUrl.contains("map0"))
		{
			iPathwayIdIndex = sUrl.lastIndexOf("map0") + 4;
		}
		else if (sUrl.contains("hsa0"))
		{
			iPathwayIdIndex = sUrl.lastIndexOf("hsa0") + 4;			
		}
		else
		{
			// Do nothing if pathway is not reference and not HSA
			return false;
		}
		
		try {
			int iNewPathwayId = StringConversionTool.convertStringToInt(sUrl
					.substring(iPathwayIdIndex, sUrl.lastIndexOf('.')), 0);
			
			// Load pathway
			loadPathwayFromFile(iNewPathwayId);
			
			triggerPathwayUpdate(iNewPathwayId);
			
			return true;
			
		}catch (StringIndexOutOfBoundsException e) {
			
//			generalManager.logMsg(
//					this.getClass().getSimpleName() + ": ERROR! Can not convert String to Integer! " 
//					+ e.toString(), LoggerType.ERROR);
			
			try {
				embeddedFrameComposite.getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageBox messageBox = 
							new MessageBox(embeddedFrameComposite.getShell(), 
									SWT.ICON_WARNING | SWT.ABORT);
				        
				        messageBox.setText("Warning");
				        messageBox.setMessage("This pathway cannot be loaded!");
				        messageBox.open();
					}
				});
			}
				catch (SWTException swte) 
			{
//					generalManager.logMsg(
//							this.getClass().getSimpleName() + 
//							": error while setURL ["+sUrl + "]", 
//							LoggerType.STATUS );
			}
				
			return false;
		}
	}
	
	private void triggerPathwayUpdate(final int iPathwayId) 
	{
		// Trigger update with current pathway that dependent pathways 
		// know which pathway is currently under interaction
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		ArrayList<Integer> tmp = new ArrayList<Integer>(1);
		tmp.add(iPathwayId);
		int[] iArTmp = new int[1];
		iArTmp[0] = iPathwayId;
		tmpStorage.setArrayInt(iArTmp);
		alSetSelection.get(0).updateSelectionSet(iParentContainerId,
				null, null, tmp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

//		generalManager.logMsg(
//				"2D Pathway update called by "
//						+ eventTrigger.getClass().getSimpleName(),
//				LoggerType.VERBOSE);

		ISetSelection setSelection = (ISetSelection) updatedSet;

		setSelection.getReadToken();
		ArrayList<Integer> iAlOptional = setSelection.getOptionalDataArray();
		if (iAlOptional.size() != 0)
		{
			IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
			int[] iArOptional = new int[iAlOptional.size()];
			for(int iCount = 0; iCount < iAlOptional.size(); iCount++)
			{
				iArOptional[iCount] = iAlOptional.get(iCount);
			}
			                          
			tmpStorage.setArrayInt(iArOptional);
			loadPathwayFromFile(setSelection.getOptionalDataArray().get(0));
		}
		
		ArrayList<Integer> iAlSelectionId = setSelection.getSelectionIdArray();
		if (iAlSelectionId.size() != 0)
		{
			// Remove old selected vertices
			iLLSelectedVertices.clear();
			//iLLNeighborDistance.clear();
	
			for (int iSelectedVertexIndex = 0; iSelectedVertexIndex < ((IStorage) setSelection
					.getStorageByDimAndIndex(0, 0)).getSize(StorageType.INT); iSelectedVertexIndex++)
			{
	
				PathwayVertexGraphItemRep selectedVertex = 
					(PathwayVertexGraphItemRep) generalManager
						.getPathwayItemManager().getItem(
								iAlSelectionId.get(iSelectedVertexIndex));
				
				if ( selectedVertex != null ) {

					// Ignore vertex if is NOT in the current pathway!
					if (!currentPathway.equals(
							selectedVertex.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).get(0)))
					{
						continue;
					}
					
					// FIXME: name of the method is not good because inside
					// resetPathway() and drawPathway() are called.
					showBackgroundOverlay(bShowBackgroundOverlay);
		
					// //ATTENTION: Performance problem!
					// resetPathway();
					// drawView();
				
					iLLSelectedVertices.add(selectedVertex.getId());
					iLLNeighborDistance.add(0);
					
					highlightCell(hashVertexRep2GraphCell.get(selectedVertex), 
							renderStyle.getHighlightedNodeColor());
		
					bNeighbourhoodShown = true;
				}
				iNeighbourhoodUndoCount++;
	
			}
		}
	}
}
