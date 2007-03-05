package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.view.swt.CmdViewLoadURLInHTMLBrowser;
//import cerberus.data.collection.IStorage;
//import cerberus.data.collection.StorageType;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayImageMap;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;

import cerberus.view.gui.swt.pathway.jgraph.PathwayGraphViewRep;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView, IMediatorSender, IMediatorReceiver {

	public static final String KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH = 
		"data/XML/imagemap/map01100.xml";
	
	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
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
	
	public APathwayGraphViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_PATHWAY_GRAPH);

		refRenderStyle = new PathwayRenderStyle();
		
//		bShowRelationEdges = true;
//		bShowReactionEdges = true;
	}
	
	public void drawView() {
		
		//Nothing to do here.
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
	
	public Pathway loadPathwayFromFile(int iNewPathwayId) {
		
		// Check if Pathway is already loaded and return it
		if (refGeneralManager.getSingelton().getPathwayManager().hasItem(iNewPathwayId))
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayFromFile(): Pathway is already loaded. No parsing needed.",
					LoggerType.MINOR_ERROR );
			
			return (Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iNewPathwayId);
		}
		
		String sPathwayFilePath = "";
		
		if (iNewPathwayId < 10)
		{
			sPathwayFilePath = "map0000" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 100 && iNewPathwayId >= 10)
		{
			sPathwayFilePath = "map000" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 1000 && iNewPathwayId >= 100)
		{
			sPathwayFilePath = "map00" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 10000 && iNewPathwayId >= 1000)
		{
			sPathwayFilePath = "map0" + Integer.toString(iNewPathwayId);
		}
		
		sPathwayFilePath = "data/XML/pathways/" + sPathwayFilePath +".xml";		
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
		
		return(refGeneralManager.getSingelton().getPathwayManager().getCurrentPathway());
	}
	
	/**
	 * Triggers the command to load the new URL
	 * in the HTML browser with a specific ID.
	 * 
	 * @param sUrl
	 */
	public void loadNodeInformationInBrowser(final String sUrl) {
		
		refEmbeddedFrameComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				CmdViewLoadURLInHTMLBrowser createdCmd = 
					(CmdViewLoadURLInHTMLBrowser)refGeneralManager.getSingelton().getCommandManager().
						createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

				createdCmd.setAttributes(iHTMLBrowserId, sUrl);
				createdCmd.doCommand();
			}
		});	
	}
	
	public void updateSelectionSet(int[] iArSelectionVertexId,
			int[] iArSelectionGroup,
			int[] iArNeighborVertices) {
	
		try {
			// Update selection SET data.
			alSetSelection.get(0).setAllSelectionDataArrays(
					iArSelectionVertexId, iArSelectionGroup, iArNeighborVertices);
			
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Set selection data and trigger update.",
					LoggerType.VERBOSE );
			
			int iTriggerID = 0;
			
			// The distinction is necessary because the JGraph 2D Pathway is embedded
			// and therefore the parent widget ID is needed for update.
			if (this.getClass().getSimpleName().equals(
					PathwayGraphViewRep.class.getSimpleName()))
				iTriggerID = iParentContainerId;
			else
				iTriggerID = iUniqueId;
			
	 		// Calls update with the ID of the PathwayViewRep
	 		((EventPublisher)refGeneralManager.getSingelton().
				getEventPublisher()).updateReceiver(refGeneralManager.
						getSingelton().getViewGLCanvasManager().
							getItem(iTriggerID), alSetSelection.get(0));
	 		
		} catch (Exception e)
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Problem during selection update triggering.",
					LoggerType.MINOR_ERROR );
	
			e.printStackTrace();
		}
	}
}
