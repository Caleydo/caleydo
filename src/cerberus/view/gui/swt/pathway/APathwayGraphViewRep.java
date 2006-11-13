package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView {

	public static final String KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH = 
		"data/XML/imagemap/map01100.xml";
	
	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
	protected int iPathwayId = 0;
	
	protected int iHTMLBrowserId;
	
	protected PathwayRenderStyle refRenderStyle;
	
	protected Pathway refCurrentPathway;
	
	protected PathwayImageMap refCurrentPathwayImageMap;
	
	/*
	 * Specifies which vertex representation of a specific vertex
	 * will be drawn.
	 */
	protected int iVertexRepIndex = 0;
	
	/*
	 * Specifies which edge representation of a specific edge
	 * will be drawn.
	 */
	protected int iEdgeRepIndex = 0;
	
	/**
	 * Pathway abstraction level.
	 * Default value is 1 so that at the beginning the overview
	 * reference pathway map is loaded.  
	 */
	protected int iPathwayLevel = 1;

	public APathwayGraphViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);

		refRenderStyle = new PathwayRenderStyle();
		
//		bShowRelationEdges = true;
//		bShowReactionEdges = true;
	}

	public void drawView() {
		
		if (refGeneralManager.getSingelton().
				getPathwayManager().getCurrentPathway() != null)
		{
			if (iPathwayId != 0)
			{
				HashMap<Integer, Pathway> pathwayLUT = 		
					((IPathwayManager)refGeneralManager.getSingelton().
							getPathwayManager()).getPathwayLUT();
				
				refCurrentPathway = pathwayLUT.get(iPathwayId);
			}
			else
			{
				refCurrentPathway = refGeneralManager.getSingelton().
					getPathwayManager().getCurrentPathway();
			}
			
			extractVertices();
			extractEdges();

			finishGraphBuilding();
		}	    
		else if (iPathwayLevel == 1)
		{ 
			refCurrentPathwayImageMap = 
				refGeneralManager.getSingelton().getPathwayManager().getCurrentPathwayImageMap();
			
			loadBackgroundOverlayImage(refCurrentPathwayImageMap.getImageLink());
		}
	}

	public void retrieveGUIContainer() {
		
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = 
			(SWTEmbeddedGraphWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET,
						refEmbeddedFrameComposite,
						iWidth, 
						iHeight);

		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
	}
	
	public void setExternalGUIContainer(Composite refSWTContainer) {
		
		refEmbeddedFrameComposite = refSWTContainer;
	}
	
	public void setWidthAndHeight(int iWidth, int iHeight) {
		
		this.iWidth = iWidth;
		this.iHeight = iHeight;
	}

	public void setHTMLBrowserId(int iHTMLBrowserId) {
		
		this.iHTMLBrowserId = iHTMLBrowserId;
	}
	
	protected void extractVertices() {
		
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    IPathwayVertexRep vertexRep;
		
        vertexIterator = refCurrentPathway.getVertexListIterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexRep = vertex.getVertexRepByIndex(iVertexRepIndex);

        	if (vertexRep != null)
        	{
        		createVertex(vertexRep);        	
        	}
        }   
	}
	
	protected void extractEdges() {
		
		// Process relation edges
	    Iterator<PathwayRelationEdge> relationEdgeIterator;
        relationEdgeIterator = refCurrentPathway.getRelationEdgeIterator();
        while (relationEdgeIterator.hasNext())
        {
        	extractRelationEdges(relationEdgeIterator.next()); 		
        }
		
	    // Process reaction edges
        PathwayReactionEdge reactionEdge;
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
		IPathwayElementManager pathwayElementManager = 
			((IPathwayElementManager)refGeneralManager.getSingelton().
				getPathwayElementManager());
		
        vertexIterator = refCurrentPathway.getVertexListIterator();
	    
	    while (vertexIterator.hasNext())
	    {
	    	vertex = vertexIterator.next();	   
	
	    	if (vertex.getVertexType() == PathwayVertexType.enzyme)
	    	{	
//	    		System.out.println("Vertex title: " +vertex.getVertexReactionName());
	    		
	    		reactionEdge = (PathwayReactionEdge)pathwayElementManager.getEdgeLUT().
	    			get(pathwayElementManager.getReactionName2EdgeIdLUT().
	    				get(vertex.getVertexReactionName()));
	
	    		// FIXME: problem with multiple reactions per enzyme
	    		if (reactionEdge != null)
	    		{
	            	extractReactionEdges(reactionEdge, vertex);
	    		}// if (edge != null)
	    	}// if (vertex.getVertexType() == PathwayVertexType.enzyme)
	    }
	}
	
	protected void extractRelationEdges(PathwayRelationEdge relationEdge) {
		
		// Direct connection between nodes
		if (relationEdge.getCompoundId() == -1)
		{
			createEdge(relationEdge.getElementId1(), 
					relationEdge.getElementId2(), 
					false, 
					relationEdge);
		}
		// Edge is routed over a compound
		else 
		{
			createEdge(relationEdge.getElementId1(), 
					relationEdge.getCompoundId(), 
					false, 
					relationEdge);
			
			if (relationEdge.getEdgeRelationType() 
					== EdgeRelationType.ECrel)
			{
    			createEdge(relationEdge.getCompoundId(), 
    					relationEdge.getElementId2(), 
    					false,
    					relationEdge);
			}
			else
			{
    			createEdge(relationEdge.getElementId2(),
    					relationEdge.getCompoundId(),
    					true,
    					relationEdge);
			}

		}
	}
	
	protected void extractReactionEdges(PathwayReactionEdge reactionEdge, 
			PathwayVertex vertex) {
		
		if (!reactionEdge.getSubstrates().isEmpty())
		{
			//FIXME: interate over substrates and products
			createEdge(
					reactionEdge.getSubstrates().get(0), 
					vertex.getElementId(), 
					false,
					reactionEdge);	
		}
		
		if (!reactionEdge.getProducts().isEmpty())
		{
			createEdge(
					vertex.getElementId(),
					reactionEdge.getProducts().get(0), 
					true,
					reactionEdge);
		}	  
	}
	
	public void setPathwayLevel(int iPathwayLevel) {
	
		this.iPathwayLevel = iPathwayLevel;
	}
	
	public int getPathwayLevel() {
		
		return iPathwayLevel;
	}
}
