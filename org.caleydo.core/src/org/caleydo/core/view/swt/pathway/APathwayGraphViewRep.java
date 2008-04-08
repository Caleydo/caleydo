package org.caleydo.core.view.swt.pathway;

import java.awt.Frame;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.IGraphItem;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.ViewType;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView, IMediatorSender, IMediatorReceiver {

	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
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
	
	public void setExternalGUIContainer(Composite refSWTContainer) {
		
		refEmbeddedFrameComposite = refSWTContainer;
	}

	protected void extractVertices(PathwayGraph pathwayToExtract) {
		
	    Iterator<IGraphItem> vertexIterator;
	    IGraphItem vertexRep;
		
        vertexIterator = pathwayToExtract.getAllItemsByKind(EGraphItemKind.NODE).iterator();
        while (vertexIterator.hasNext())
        {
        	vertexRep = vertexIterator.next();

        	if (vertexRep != null)
        	{
        		createVertex((PathwayVertexGraphItemRep)vertexRep, pathwayToExtract);        	
        	}
        }   
	}
	
//	protected void extractEdges(Pathway refPathwayToExtract) {
//		
//		// Process relation edges
//	    Iterator<PathwayRelationEdge> relationEdgeIterator;
//        relationEdgeIterator = refPathwayToExtract.getRelationEdgeIterator();
//        while (relationEdgeIterator.hasNext())
//        {
//        	extractRelationEdges(relationEdgeIterator.next()); 		
//        }
//		
//	    // Process reaction edges
//        PathwayReactionEdge reactionEdge;
//	    Iterator<PathwayVertex> vertexIterator;
//	    PathwayVertex vertex;
//		IPathwayElementManager pathwayElementManager = 
//			((IPathwayElementManager)refGeneralManager.getSingelton().
//				getPathwayElementManager());
//		
//        vertexIterator = refPathwayToExtract.getVertexListIterator();
//	    
//	    while (vertexIterator.hasNext())
//	    {
//	    	vertex = vertexIterator.next();	   
//	
//	    	if (vertex.getVertexType() == PathwayVertexType.enzyme)
//	    	{	
////	    		System.out.println("Vertex title: " +vertex.getVertexReactionName());
//	    		
//	    		reactionEdge = (PathwayReactionEdge)pathwayElementManager.getEdgeLUT().
//	    			get(pathwayElementManager.getReactionName2EdgeIdLUT().
//	    				get(vertex.getVertexReactionName()));
//	
//	    		// FIXME: problem with multiple reactions per enzyme
//	    		if (reactionEdge != null)
//	    		{
//	            	extractReactionEdges(reactionEdge, vertex);
//	    		}// if (edge != null)
//	    	}// if (vertex.getVertexType() == PathwayVertexType.enzyme)
//	    }
//	}
	
//	protected void extractRelationEdges(PathwayRelationEdge relationEdge) {
//		
//		// Direct connection between nodes
//		if (relationEdge.getCompoundId() == -1)
//		{
//			createEdge(relationEdge.getElementId1(), 
//					relationEdge.getElementId2(), 
//					false, 
//					relationEdge);
//		}
//		// Edge is routed over a compound
//		else 
//		{
//			createEdge(relationEdge.getElementId1(), 
//					relationEdge.getCompoundId(), 
//					false, 
//					relationEdge);
//			
//			if (relationEdge.getEdgeRelationType() 
//					== EdgeRelationType.ECrel)
//			{
//    			createEdge(relationEdge.getCompoundId(), 
//    					relationEdge.getElementId2(), 
//    					false,
//    					relationEdge);
//			}
//			else
//			{
//    			createEdge(relationEdge.getElementId2(),
//    					relationEdge.getCompoundId(),
//    					true,
//    					relationEdge);
//			}
//
//		}
//	}
//	
//	protected void extractReactionEdges(PathwayReactionEdge reactionEdge, 
//			PathwayVertex vertex) {
//		
//		if (!reactionEdge.getSubstrates().isEmpty())
//		{
//			//FIXME: interate over substrates and products
//			createEdge(
//					reactionEdge.getSubstrates().get(0), 
//					vertex.getElementId(), 
//					false,
//					reactionEdge);	
//		}
//		
//		if (!reactionEdge.getProducts().isEmpty())
//		{
//			createEdge(
//					vertex.getElementId(),
//					reactionEdge.getProducts().get(0), 
//					true,
//					reactionEdge);
//		}	  
//	}
	
	public void setPathwayLevel(int iPathwayLevel) {
	
		this.iPathwayLevel = iPathwayLevel;
	}
	
	public int getPathwayLevel() {
		
		return iPathwayLevel;
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
					(CmdViewLoadURLInHTMLBrowser)generalManager.getSingelton().getCommandManager().
						createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

				createdCmd.setAttributes(sUrl);
				createdCmd.doCommand();
			}
		});	
	}
}
