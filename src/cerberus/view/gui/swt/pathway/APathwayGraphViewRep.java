package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.pathway.PathwayManager;
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
	
	public APathwayGraphViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		// TODO Auto-generated constructor stub
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
