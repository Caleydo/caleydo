package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.collection.ISet;
import cerberus.data.collection.selection.SelectionHandler;
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
import cerberus.manager.IViewManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView {

	public static final String KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH = 
		"data/XML/imagemap/map01100.xml";
	
	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
	/**
	 * @deprecated
	 */
	protected int iPathwaySetId = 0;
	
	protected ISet refPathwaySet;
	
	protected int iHTMLBrowserId;
	
	protected PathwayRenderStyle refRenderStyle;
	
	//protected Pathway refCurrentPathway;
	
	protected PathwayImageMap refCurrentPathwayImageMap;
	
	protected boolean bSelectionMediatorCreated = false;
	
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
	
	protected SelectionHandler refSelectionHandler;

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
		

	}

	public void retrieveGUIContainer() {
		
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
	
	protected void extractVertices(Pathway refPathwayToExtract) {
		
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    IPathwayVertexRep vertexRep;
		
        vertexIterator = refPathwayToExtract.getVertexListIterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexRep = vertex.getVertexRepByIndex(iVertexRepIndex);

        	if (vertexRep != null)
        	{
        		createVertex(vertexRep, refPathwayToExtract);        	
        	}
        }   
	}
	
	protected void extractEdges(Pathway refPathwayToExtract) {
		
		// Process relation edges
	    Iterator<PathwayRelationEdge> relationEdgeIterator;
        relationEdgeIterator = refPathwayToExtract.getRelationEdgeIterator();
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
		
        vertexIterator = refPathwayToExtract.getVertexListIterator();
	    
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
	
	public Pathway loadPathwayFromFile(String sFilePath) {
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sFilePath);
		
		
		return(refGeneralManager.getSingelton().getPathwayManager().getCurrentPathway());
	}
	
	public void loadNodeInformationInBrowser(final String sUrl) {
		
		// Load node information in browser
		final IViewManager tmpViewManager = refGeneralManager.getSingelton().
			getViewGLCanvasManager();					
    
		refEmbeddedFrameComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				((HTMLBrowserViewRep)tmpViewManager.
						getItem(iHTMLBrowserId)).setUrl(sUrl);
			}
		});	
	}
	
	public void createSelectionSet(int[] arSelectionVertexId,
			int[] arSelectionGroup,
			int[] arNeighborVertices) {
	
		// Selection handler will be iniitailly created.
		// If selection handler exists already it is not necessary to create it again.
		// Just the data needs to be set.
		if (refSelectionHandler == null)
		{
			refSelectionHandler = new SelectionHandler(refGeneralManager, 
					this.iParentContainerId, 
					arSelectionVertexId, 
					arSelectionGroup, 
					arNeighborVertices);
		}		
		else
		{
			refSelectionHandler.setSelectionIdArray(arSelectionVertexId);
			//refSelectionHandler.setGroupArray(arSelectionGroup);
			refSelectionHandler.setOptionalDataArray(arNeighborVertices);
		}
		
		// Calls update with the ID of the PathwayViewRep
		((EventPublisher)refGeneralManager.getSingelton().
				getEventPublisher()).updateSelection(refGeneralManager.
						getSingelton().getViewGLCanvasManager().
							getItem(iParentContainerId), 
								refGeneralManager.getSingelton().
									getSetManager().getItemSet(85101));
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#setPathwaySet(int)
	 */
	public void setPathwaySet(int iPathwaySetId) {
		
		this.iPathwaySetId = iPathwaySetId;
		
		refPathwaySet = refGeneralManager.getSingelton().
			getSetManager().getItemSet(iPathwaySetId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.AViewRep#getDataSetId()
	 */
	public int getDataSetId() {
		
		return iPathwaySetId;
	}
}
