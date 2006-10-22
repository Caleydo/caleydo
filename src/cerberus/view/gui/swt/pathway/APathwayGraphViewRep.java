package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;
import cerberus.xml.parser.parameter.IParameterHandler;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView {

	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
	protected int iPathwayId = 0;
	
	protected int iHTMLBrowserId;
	
	protected PathwayRenderStyle refRenderStyle;
	
	protected Pathway refCurrentPathway;
	
	public APathwayGraphViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);

		refRenderStyle = new PathwayRenderStyle();
	}

	public void setPathwayId(int iPathwayId) {
		
		// TODO Auto-generated method stub
	}

	public void loadPathwayFromFile(String sFilePath) {
		
		// TODO Auto-generated method stub
	}

	public void initView() {
		
		// TODO Auto-generated method stub
	}

	public void drawView() {
		
		HashMap<Integer, Pathway> pathwayLUT = 		
			((IPathwayManager)refGeneralManager.getSingelton().
					getPathwayManager()).getPathwayLUT();
		
		// Take first in list if pathway ID is not set
		if (iPathwayId == 0)
		{
			Iterator<Pathway> iter = pathwayLUT.values().iterator();
			refCurrentPathway = iter.next();
		}
		else
		{
			refCurrentPathway = pathwayLUT.get(iPathwayId);
		}
	    
	    Vector<PathwayVertex> vertexList;
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    Vector<IPathwayVertexRep> vertexReps;
	    Iterator<IPathwayVertexRep> vertexRepIterator;
	    PathwayVertexRep vertexRep;
	    
	    Vector<APathwayEdge> edgeList;
	    Iterator<APathwayEdge> edgeIterator;
	    APathwayEdge edge;
	    PathwayRelationEdge relationEdge;
	    PathwayReactionEdge reactionEdge;
	    
        vertexList = refCurrentPathway.getVertexList();
        
        vertexIterator = vertexList.iterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexReps = vertex.getVertexReps();
        	vertexRepIterator = vertexReps.iterator();
        	
        	//FIXME: actually it is not right to interate over all vertex reps.
        	while (vertexRepIterator.hasNext())
        	{
        		vertexRep = (PathwayVertexRep) vertexRepIterator.next();
        		
        		createVertex(vertex,
        				vertexRep.getName(),
        				vertexRep.getHeight(), vertexRep.getWidth(), 
        				vertexRep.getXPosition(), vertexRep.getYPosition(),
        				vertexRep.getShapeType());        	
        	}
        }   
        
        edgeList = refCurrentPathway.getEdgeList();
        edgeIterator = edgeList.iterator();
        while (edgeIterator.hasNext())
        {
        	edge = edgeIterator.next();
        
        	// Process RELATION EDGES
        	if (edge.getClass().getName().equals("cerberus.data.pathway.element.PathwayRelationEdge"))
        	{
        		// Cast abstract edge to relation edge
        		relationEdge = (PathwayRelationEdge)edge;

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
        					== EdgeRelationType.maplink)
        			{
            			createEdge(relationEdge.getElementId2(),
            					relationEdge.getCompoundId(),
            					true,
            					relationEdge);
        			}
        			else
        			{
            			createEdge(relationEdge.getCompoundId(), 
            					relationEdge.getElementId2(), 
            					false,
            					relationEdge);
        			}
        		}
        	}// if (edge.getClass().getName().equals("PathwayRelationEdge"))
        	
        	
//        	else if (edge.getSype().equals("maplink"))
//        	{
//        		createEdge(edge.getICompoundId(), edge.getIElementId2());
//        		// Is the second connection needed or already drawn?
//        	}
        }   
        
        // Process pathway reactions
        vertexIterator = vertexList.iterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexReps = vertex.getVertexReps();
        	vertexRepIterator = vertexReps.iterator();
 
        	if (vertex.getVertexType() == PathwayVertexType.enzyme)
	    	{	
	    		IPathwayElementManager pathwayElementManager = 
	    			((IPathwayElementManager)refGeneralManager.getSingelton().
	    				getPathwayElementManager());
	    		
	    		System.out.println("Vertex title: " +vertex.getVertexReactionName());
	    		
	    		edge = pathwayElementManager.getEdgeLUT().
	    			get(pathwayElementManager.getReactionName2EdgeIdLUT().
	    				get(vertex.getVertexReactionName()));
	
	    		// FIXME: problem with multiple reactions per enzyme
	    		if (edge != null)
	    		{
	            	// Process REACTION EDGES
	            	if (edge.getClass().getName().equals("cerberus.data.pathway.element.PathwayReactionEdge"))
	            	{
	            		// Cast abstract edge to reaction edge
	            		reactionEdge = (PathwayReactionEdge)edge;
	
	            		//FIXME: interate over substrates and products
		        		createEdge(
		        				reactionEdge.getSubstrates().get(0), 
		        				vertex.getElementId(), 
		        				false,
		        				reactionEdge);	    
		        		
		        		createEdge(
		        				vertex.getElementId(),
		        				reactionEdge.getProducts().get(0), 
		        				true,
		        				reactionEdge);	  
	            	}	
	    		}// if (edge != null)
	    	}// if (vertex.getVertexType() == PathwayVertexType.enzyme)
        }
	}

	public void readInAttributes(IParameterHandler refParameterHandler) {
		
		// TODO Auto-generated method stub
	}

	public void setAttributes(Vector<String> attributes) {
		
		// TODO Auto-generated method stub
	}

	public void setParentContainerId(int iParentContainerId) {
		
		// TODO Auto-generated method stub
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
}
