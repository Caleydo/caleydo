package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;

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
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.pad.resources.Translator;

import cerberus.util.system.StringConversionTool;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;
import cerberus.view.gui.swt.pathway.jgraph.GPCellViewFactory;
import cerberus.view.gui.swt.pathway.jgraph.GPOverviewPanel;

/**
 * In this class the real drawing of the Pathway happens.
 * For the drawing the JGraph package is used.
 * We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Marc Streit
 *
 */
public class PathwayGraphViewRep
extends APathwayGraphViewRep {
		
	protected static final double SCALING_FACTOR = 1.4;
	
	protected GraphModel refGraphModel;
	
	protected GraphLayoutCache refGraphLayoutCache;
	
	protected JGraph refPathwayGraph;
	
	protected DefaultGraphCell refGraphCell;
	
	protected HashMap<Integer, DefaultGraphCell> vertexIdToCellLUT;
	
	protected boolean isGraphSet = false;
	
	protected GraphUndoManager refUndoManager;
	
	protected GPOverviewPanel refOverviewPanel;
	
	protected int iNeighbourhoodDistance = 1;
	
	public PathwayGraphViewRep(IGeneralManager refGeneralManager, int iParentContainerId) {
		
		super(refGeneralManager, -1, iParentContainerId, "");
		
		vertexIdToCellLUT = new HashMap<Integer, DefaultGraphCell>();
	}

	public void initView() {
		
		class PathwayMarqueeHandler 
		extends BasicMarqueeHandler {

			public boolean isForceMarqueeEvent(MouseEvent event) {
				
		       if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1)
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
						else
						{
							// Load node information in browser
							final IViewManager tmpViewManager = refGeneralManager.getSingelton().
							getViewGLCanvasManager();					
					    
							refEmbeddedFrameComposite.getDisplay().asyncExec(new Runnable() {
								public void run() {
									((HTMLBrowserViewRep)tmpViewManager.
											getItem(iHTMLBrowserId)).setUrl(sUrl);
								}
							});	
							
							showNeighbourhood(clickedCell, iNeighbourhoodDistance);
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
		
		// Turn on anti-aliasing
		//refPathwayGraph.setAntiAliased(true);
		
		refPathwayGraph.setMarqueeHandler(new PathwayMarqueeHandler());
		
		// Create and register Undo Manager
		refUndoManager = new GraphUndoManager();
		refGraphModel.addUndoableEditListener(refUndoManager);
	}
	
	public void drawView() {
		
		super.drawView();
		
        // Check if graph is already added to the frame
        if (isGraphSet == false)
        {
        	final OverlayLayout overlayLayout = new OverlayLayout(refEmbeddedFrame);
        	refEmbeddedFrame.setLayout(overlayLayout);
        	
            final Dimension dimOverviewMap = new Dimension(200, 200);
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
		refGraphLayoutCache.setModel(refGraphModel);
		refGraphModel.addUndoableEditListener(refUndoManager);
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sFilePath);
		
		drawView();
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
	
	protected void showNeighbourhood(DefaultGraphCell cell, int iDistance) {
	
		refUndoManager.undo(refGraphLayoutCache);
		
		List<DefaultGraphCell> neighbourCells = 
			refGraphLayoutCache.getNeighbours(cell, null, false, false);
	
		Iterator<DefaultGraphCell> cellIter = neighbourCells.iterator();
		
		Map<DefaultGraphCell, Map> nested = new Hashtable<DefaultGraphCell, Map>();
		Map attributeMap = new Hashtable();
		
		GraphConstants.setGradientColor(
				attributeMap, Color.red);
		
		DefaultGraphCell tmpCell;
		
		while (cellIter.hasNext())
		{
			tmpCell = cellIter.next();

			nested.put(tmpCell, attributeMap);
			
//			for (int iDistanceCount = 1; iDistanceCount < iDistance; iDistanceCount++)
//			{
//				showNeighbourhood(tmpCell, iDistance-1);
//			}
		}
		
		refGraphLayoutCache.edit(nested, null, null, null);
	}
	
	/**
	 * Methods puts the overview map in a new JFrame and
	 * displays the frame.
	 * 
	 * @param dim
	 */
	public void showOverviewMapInNewWindow(Dimension dim) {
		
        JFrame wnd = new JFrame();
        wnd.setLocation(800, 500);
        wnd.setSize(dim);
        wnd.setVisible(true);
        
		wnd.add(refOverviewPanel);
	}

	public void setNeighbourhoodDistance(int iNeighbourhoodDistance) {
		
		this.iNeighbourhoodDistance = iNeighbourhoodDistance;
	}
}
