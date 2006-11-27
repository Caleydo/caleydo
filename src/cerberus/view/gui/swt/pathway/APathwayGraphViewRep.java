package cerberus.view.gui.swt.pathway;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import cerberus.command.data.CmdDataCreateVirtualArray;
import cerberus.command.data.CmdDataCreateSet;
import cerberus.command.data.CmdDataCreateStorage;
import cerberus.command.event.CmdEventCreateMediator;
import cerberus.data.collection.IStorage;
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
import cerberus.manager.view.ViewJoglManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IGroupedSelection;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.ParameterHandler;

public abstract class APathwayGraphViewRep 
extends AViewRep
implements IPathwayGraphView, IGroupedSelection {

	public static final String KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH = 
		"data/XML/imagemap/map01100.xml";
	
	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
	protected int iPathwayId = 0;
	
	protected int iHTMLBrowserId;
	
	protected PathwayRenderStyle refRenderStyle;
	
	protected Pathway refCurrentPathway;
	
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
        		createVertex(vertexRep, false);        	
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
	
	public void loadPathwayFromFile(String sFilePath) {
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sFilePath);
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
	
	public void createSelectionSet(int[] arSelectedVertices,
			int[] arSelectedGroup,
			int[] arNeighborVertices) {
	
		int iSelectionMediatorId = 95201;
		int iSelectionSetId = 45101;
		int iSelectionVirtualArrayId = 45201;
		int iSelectionDataStorageId = 45301;
		int iSelectionNeighborStorageId = 55301;
		
//		refGeneralManager.getSingelton().getSetManager().deleteSet(45101);
//		refGeneralManager.getSingelton().getVirtualArrayManager().deleteSelection(45201);
//		refGeneralManager.getSingelton().getStorageManager().deleteStorage(45301);
		
		IParameterHandler phAttributes = new ParameterHandler();
				
		if (bSelectionMediatorCreated == false)
		{
		//TODO: retrieve new generated IDs instead of static ones
		
		// CREATE SELECTION VIRTUAL ARRAY
		// CmdId
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Virtual Array", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionVirtualArrayId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"3 0 0 1", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		new CmdDataCreateVirtualArray(refGeneralManager, phAttributes);
		
		phAttributes = null;
		phAttributes = new ParameterHandler();

		// CREATE SELECTION DATA STORAGE
		// CmdId
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Storage", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionDataStorageId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		new CmdDataCreateStorage(refGeneralManager, phAttributes, true);
		
		phAttributes = null;
		phAttributes = new ParameterHandler();
		
		// CREATE SELECTION NEIGHBOR STORAGE
		// CmdId
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Neighbor Storage", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionNeighborStorageId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		//FIXME: the attributes are overwritten afterwards. so they should get a  proper default value.
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"INT", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"123 123 123", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		new CmdDataCreateStorage(refGeneralManager, phAttributes, true);
		
		phAttributes = null;
		phAttributes = new ParameterHandler();
		
		// CREATE SELECTION SET
		// CmdId
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Set", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
				
		// TargetID (SetID)
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionSetId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());
		
		// SelectionID
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				"45201", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());
		
		// StorageIDs
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				"45301", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		// Detail
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				"CREATE_SET_PLANAR", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_DETAIL.getDefault());
		
		new CmdDataCreateSet(refGeneralManager, phAttributes, true);
		
		phAttributes = null;
		phAttributes = new ParameterHandler();
		
		ViewJoglManager viewManager = 
			(ViewJoglManager) refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		ArrayList<AViewRep> arDataExplorerViews = 
			viewManager.getViewByType(ViewType.DATA_EXPLORER);
		
		Iterator<AViewRep> iterDataExplorerViewRep = 
			arDataExplorerViews.iterator();
		
		String strDataExplorerConcatenation = ""; 
		
		while(iterDataExplorerViewRep.hasNext())
		{
			strDataExplorerConcatenation = strDataExplorerConcatenation.concat(
					Integer.toString(iterDataExplorerViewRep.next().getId()));
			
			//TODO: add space character
		}
		
		// CREATE SELECTION MEDIATOR
		
		// CmdId
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_CMD_ID.getXmlKey(), 
				"12345", 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_CMD_ID.getDefault());

		// Label
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
				"Pathway Selection Mediator", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_LABEL.getDefault());
		
		// TargetID
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(), 
				Integer.toString(iSelectionMediatorId), 
				IParameterHandler.ParameterHandlerType.INT, 
				CommandQueueSaxType.TAG_TARGET_ID.getDefault());		 
		
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
				Integer.toString(this.iParentContainerId), 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getDefault());

		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
				strDataExplorerConcatenation + " " +"79401", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getDefault());
		
		phAttributes.setValueAndTypeAndDefault(CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
				"SELECTION_MEDIATOR", 
				IParameterHandler.ParameterHandlerType.STRING, 
				CommandQueueSaxType.TAG_DETAIL.getDefault());
		

			CmdEventCreateMediator createdCommand =
				new CmdEventCreateMediator(refGeneralManager, phAttributes);	
		
			createdCommand.doCommand();
			bSelectionMediatorCreated = true;
		}
		
		// Set/update selection data storage
		((IStorage)refGeneralManager.getItem(iSelectionDataStorageId)).
			setArrayInt(arSelectedVertices);
		
		// Set/update selection neighbor storage
		((IStorage)refGeneralManager.getItem(iSelectionNeighborStorageId)).
		setArrayInt(arNeighborVertices);		
		
		// Calls update with the ID of the PathwayViewRep
		((EventPublisher)refGeneralManager.getSingelton().
				getEventPublisher()).updateSelection(refGeneralManager.
						getSingelton().getViewGLCanvasManager().
							getItem(iParentContainerId), 
							refGeneralManager.getSingelton().getSetManager().getItemSet(45101));
	}
}
