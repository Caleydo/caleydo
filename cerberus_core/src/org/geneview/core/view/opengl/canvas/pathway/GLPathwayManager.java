package org.geneview.core.view.opengl.canvas.pathway;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.algorithm.GraphVisitorSearchBFS;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.edge.PathwayReactionEdgeGraphItemRep;
import org.geneview.core.data.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexShape;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.util.mapping.EnzymeToExpressionColorMapper;
import org.geneview.core.view.opengl.util.GLTextUtils;

/**
 * 
 * @author Marc Streit
 *
 */
public class GLPathwayManager {

	private IGeneralManager refGeneralManager;
	
	public static final float SCALING_FACTOR_X = 0.0025f;
	public static final float SCALING_FACTOR_Y = 0.0025f;
	
	private int iEnzymeNodeDisplayListId = -1;
	private int iCompoundNodeDisplayListId = -1;
	private int iHighlightedEnzymeNodeDisplayListId = -1;
	
	// First 200 IDs are reserved for picking of non pathway objects in the scene
	private int iUniqueObjectPickId = GLCanvasJukeboxPathway3D.FREE_PICKING_ID_RANGE_START;
	
	private PathwayRenderStyle refRenderStyle;

	private boolean bEnableGeneMapping = true;
	private boolean bEnableEdgeRendering = true;
	private boolean bEnableIdenticalNodeHighlighting = true;
	private boolean bEnableNeighborhood = false;
	private boolean bEnableAnnotation = true;
	
	private HashMap<Integer, PathwayVertexGraphItemRep> hashPickID2VertexRep;
	
	private HashMap<Integer, Integer> hashPathwayId2VerticesDisplayListId;
	
	private HashMap<Integer, Integer> hashPathwayId2EdgesDisplayListId;	
	
	private EnzymeToExpressionColorMapper enzymeToExpressionColorMapper;
	
	private ArrayList<SetSelection> alSetSelection;
	
	private HashMap<Integer, Integer> hashSelectedGraphItemRepId2Depth;
	
	private HashMap<Integer, ArrayList<Color>> hashElementId2MappingColorArray;
	
	/**
	 * Constructor.
	 */
	public GLPathwayManager(final IGeneralManager refGeneralManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		refRenderStyle = new PathwayRenderStyle();
		hashPickID2VertexRep = new HashMap<Integer, PathwayVertexGraphItemRep>();
		hashPathwayId2VerticesDisplayListId = new HashMap<Integer, Integer>();
		hashPathwayId2EdgesDisplayListId = new HashMap<Integer, Integer>();		
		hashElementId2MappingColorArray = new HashMap<Integer, ArrayList<Color>>();
	}
	
	public void init(final GL gl, 
			final ArrayList<ISet> alSetData,
			final ArrayList<SetSelection> alSetSelection) {
		
		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		
		this.alSetSelection = alSetSelection;
		hashSelectedGraphItemRepId2Depth = new HashMap<Integer, Integer>();
		
		enzymeToExpressionColorMapper =
			new EnzymeToExpressionColorMapper(refGeneralManager, alSetData);
	}
	
	public void buildPathwayDisplayList(final GL gl, final int iPathwayId) {

		PathwayGraph refTmpPathway = (PathwayGraph)refGeneralManager.getSingelton().getPathwayManager().
			getItem(iPathwayId);
		
		int iVerticesDisplayListId = -1;
		int iEdgesDisplayListId = -1;
		
		if (hashPathwayId2VerticesDisplayListId.containsKey(iPathwayId))
		{
			// Replace current display list if a display list exists
			iVerticesDisplayListId = hashPathwayId2VerticesDisplayListId.get(iPathwayId);
		}
		else
		{
			// Creating vertex display list for pathways
			iVerticesDisplayListId = gl.glGenLists(1);
			hashPathwayId2VerticesDisplayListId.put(iPathwayId, iVerticesDisplayListId);			
		}
		
		performIdenticalNodeHighlighting();
		
		gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);	
		extractVertices(gl, refTmpPathway);
		gl.glEndList();
		
		if (hashPathwayId2EdgesDisplayListId.containsKey(iPathwayId))
		{
			// Replace current display list if a display list exists
			iEdgesDisplayListId = hashPathwayId2EdgesDisplayListId.get(iPathwayId);
		}
		else
		{		
			// Creating edge display list for pathways
			iEdgesDisplayListId = gl.glGenLists(1);
			hashPathwayId2EdgesDisplayListId.put(iPathwayId, iEdgesDisplayListId);			
		}
		
		gl.glNewList(iEdgesDisplayListId, GL.GL_COMPILE);	
		extractEdges(gl, refTmpPathway);
		gl.glEndList();
	}

	private void performIdenticalNodeHighlighting() {
		
		hashSelectedGraphItemRepId2Depth.clear();
		
		alSetSelection.get(0).getReadToken();
		int[] iArTmpSelectedGraphItemIds = 
			alSetSelection.get(0).getSelectionIdArray();
		int[] iArTmpSelectedGraphItemDepth =
			alSetSelection.get(0).getGroupArray();
		alSetSelection.get(0).returnReadToken();
		
		if (iArTmpSelectedGraphItemIds.length == 0)
			return;
		
		// Copy selection IDs to array list object
		for(int iItemIndex = 0; iItemIndex < iArTmpSelectedGraphItemIds.length; iItemIndex++) 
		{
			hashSelectedGraphItemRepId2Depth.put(
					iArTmpSelectedGraphItemIds[iItemIndex],
					iArTmpSelectedGraphItemDepth[iItemIndex]);

			if (!bEnableIdenticalNodeHighlighting)
				continue;
			
			// Perform identical node highlighting only on nodes with depth 0
			if (iArTmpSelectedGraphItemDepth[iItemIndex] != 0)
				continue;
			
			Iterator<IGraphItem> iterGraphItems = 
				((IGraphItem) refGeneralManager.getSingelton().getPathwayItemManager()
						.getItem(iArTmpSelectedGraphItemIds[iItemIndex])).getAllItemsByProp(
								EGraphItemProperty.ALIAS_PARENT).iterator();
			Iterator<IGraphItem> iterIdenticalGraphItemReps;
			IGraphItem identicalNode;
			
			while(iterGraphItems.hasNext()) 
			{
				iterIdenticalGraphItemReps = 
					iterGraphItems.next().getAllItemsByProp(
							EGraphItemProperty.ALIAS_CHILD).iterator();

				while(iterIdenticalGraphItemReps.hasNext()) 
				{
					identicalNode = iterIdenticalGraphItemReps.next();
					
					hashSelectedGraphItemRepId2Depth.put(
							identicalNode.getId(), 0);
				
					performNeighborhoodAlgorithm(identicalNode);
				}
			}	
		}
		
		// FIXME: TURN ON AGAIN - Filter edges before - other views are only interested in vertices.
//		// Store currently selected vertices back to selection set
//		Set<Entry<Integer, Integer>> setAllSelectedVertices = hashSelectedGraphItemRepId2Depth.entrySet();
//		
//		int[] iArTmpGraphItemId = new int[setAllSelectedVertices.size()];  
//		int[] iArTmpGraphItemDepth = new int[setAllSelectedVertices.size()];
//		
//		Iterator<Entry<Integer, Integer>> iterAllSelectedVertices = setAllSelectedVertices.iterator();
//		
//		int iItemIndex = 0;
//		Entry<Integer, Integer> tmpEntry;
//		while (iterAllSelectedVertices.hasNext())
//		{
//			tmpEntry = iterAllSelectedVertices.next();
//			iArTmpGraphItemId[iItemIndex] = tmpEntry.getKey();
//			iArTmpGraphItemDepth[iItemIndex] = tmpEntry.getValue();
//			iItemIndex++;
//		}
//	
//		alSetSelection.get(0).getWriteToken();
//		alSetSelection.get(0).setSelectionIdArray(iArTmpGraphItemId);	
//		alSetSelection.get(0).setGroupArray(iArTmpGraphItemDepth);
//		alSetSelection.get(0).returnWriteToken();
	}
	
	private void performNeighborhoodAlgorithm(final IGraphItem selectedVertex) {
		
		GraphVisitorSearchBFS graphVisitorSearchBFS;
		
		if(bEnableNeighborhood)
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 4);
		else
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 0);
		
		graphVisitorSearchBFS.setProp(EGraphItemProperty.OUTGOING);
		graphVisitorSearchBFS.setGraph(selectedVertex.getAllGraphByType(
				EGraphItemHierarchy.GRAPH_PARENT).get(0));
		
		//List<IGraphItem> lGraphItems = graphVisitorSearchBFS.getSearchResult();
		graphVisitorSearchBFS.getSearchResult();
		
		List<List<IGraphItem>> lDepthSearchResult = graphVisitorSearchBFS.getSearchResultDepthOrdered();
		List<IGraphItem> lGraphItems = new ArrayList<IGraphItem>();
		
		for(int iDepthIndex = 0; iDepthIndex < lDepthSearchResult.size(); iDepthIndex++)
		{
			lGraphItems = lDepthSearchResult.get(iDepthIndex);
						
			for(int iItemIndex = 0; iItemIndex < lGraphItems.size(); iItemIndex++) 
			{
//				// Consider only vertices for now
//				if (lGraphItems.get(iItemIndex).getClass().equals
//						(cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep.class))
//				{			
					hashSelectedGraphItemRepId2Depth.put(
							lGraphItems.get(iItemIndex).getId(), (iDepthIndex + 1) / 2); // +1 / 2 because we want to ignore edge depth
//				}
			}
		}
	}
	
	private void buildEnzymeNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);
		
		float fNodeWidth = refRenderStyle.getEnzymeNodeWidth(true);
		float fNodeHeight = refRenderStyle.getEnzymeNodeHeight(true);
		
		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);		
	    gl.glEndList();
	}
	
	protected void buildHighlightedEnzymeNodeDisplayList(final GL gl) {
		
		// Creating display list for node cube objects
		iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);
		
		float fNodeWidth = refRenderStyle.getEnzymeNodeWidth(true);
		float fNodeHeight = refRenderStyle.getEnzymeNodeHeight(true);
		
		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);		
	    gl.glEndList();
	}
	
	protected void buildCompoundNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);
		
		float fNodeWidth = refRenderStyle.getCompoundNodeWidth(true);
		float fNodeHeight = refRenderStyle.getCompoundNodeHeight(true);
		
		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
        gl.glEndList();
	}
	
	private void fillNodeDisplayList(final GL gl, 
			final float fNodeWidth, final float fNodeHeight) {
		
		gl.glBegin(GL.GL_QUADS);
		
        // FRONT FACE
		gl.glNormal3f( 0.0f, 0.0f, 1.0f);	
		// Top Right Of The Quad (Front)
        gl.glVertex3f(-fNodeWidth , -fNodeHeight, 0.015f);		
        // Top Left Of The Quad (Front)
        gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.015f);			
        // Bottom Left Of The Quad (Front)
        gl.glVertex3f(fNodeWidth, fNodeHeight, 0.015f);
		// Bottom Right Of The Quad (Front)
        gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.015f);

        // BACK FACE
        gl.glNormal3f( 0.0f, 0.0f,-1.0f);
        // Bottom Left Of The Quad (Back)
        gl.glVertex3f(fNodeWidth, -fNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Back)
        gl.glVertex3f(-fNodeWidth, -fNodeHeight,-0.015f);	
        // Top Right Of The Quad (Back)
        gl.glVertex3f(-fNodeWidth, fNodeHeight,-0.015f);			
        // Top Left Of The Quad (Back)
        gl.glVertex3f(fNodeWidth, fNodeHeight,-0.015f);			

		// TOP FACE
        gl.glNormal3f( 0.0f, 1.0f, 0.0f);	
        // Top Right Of The Quad (Top)
        gl.glVertex3f(fNodeWidth, fNodeHeight,-0.015f);	
        // Top Left Of The Quad (Top)
        gl.glVertex3f(-fNodeWidth, fNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Top)
        gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Top)
        gl.glVertex3f(fNodeWidth, fNodeHeight, 0.015f);			

        // BOTTOM FACE
        gl.glNormal3f( 0.0f,-1.0f, 0.0f);	
        // Top Right Of The Quad (Bottom)
        gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.015f);
        // Top Left Of The Quad (Bottom)
        gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(-fNodeWidth, -fNodeHeight,-0.015f);
        // Bottom Right Of The Quad (Bottom)
        gl.glVertex3f(fNodeWidth, -fNodeHeight,-0.015f);			

        // RIGHT FACE
        gl.glNormal3f( 1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Right)
        gl.glVertex3f(fNodeWidth, fNodeHeight,-0.015f);
        // Top Left Of The Quad (Right)
        gl.glVertex3f(fNodeWidth, fNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Right)
        gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Right)
        gl.glVertex3f(fNodeWidth, -fNodeHeight,-0.015f);			
        
        // LEFT FACE
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Left)
        gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.015f);	
        // Top Left Of The Quad (Left)
        gl.glVertex3f(-fNodeWidth, fNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Left)
        gl.glVertex3f(-fNodeWidth, -fNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Left)
        gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.015f);	
        
        gl.glEnd();
	}
	
	protected void fillNodeDisplayListFrame(final GL gl,
			final float fNodeWidth, final float fNodeHeight) {
	
		gl.glLineWidth(3);
		
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.02f);
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.02f);		
        gl.glEnd();
	}
	
	private void extractVertices(final GL gl,
			PathwayGraph pathwayToExtract) {
		
	    Iterator<IGraphItem> vertexIterator =
	    	pathwayToExtract.getAllItemsByKind(EGraphItemKind.NODE).iterator();;
	    IGraphItem vertexRep;
		
        while (vertexIterator.hasNext())
        {
        	vertexRep = vertexIterator.next();

        	if (vertexRep != null)
        	{
        		createVertex(gl,
        				(PathwayVertexGraphItemRep)vertexRep, 
        				pathwayToExtract);        	
        	}
        }   
	}
	
	private void extractEdges(final GL gl,
			PathwayGraph pathwayToExtract) {
		
	    Iterator<IGraphItem> edgeIterator =
	    	pathwayToExtract.getAllItemsByKind(EGraphItemKind.EDGE).iterator();
      
	    IGraphItem edgeRep;
		
        while (edgeIterator.hasNext())
        {
        	edgeRep = edgeIterator.next();
        	
        	if (edgeRep != null)
        	{
            	// Render edge if it is contained in the minimum spanning tree of the neighborhoods
            	if (!hashSelectedGraphItemRepId2Depth.containsKey(edgeRep.getId()))
            		continue;
        		
        		createEdge(gl, edgeRep, pathwayToExtract);        	        			
        	}
        }   
	}
	
	private void createVertex(final GL gl, 
			PathwayVertexGraphItemRep vertexRep, 
			PathwayGraph refContainingPathway) {
		
		Color tmpNodeColor = null;
		
		float fCanvasXPos = (vertexRep.getXPosition() * SCALING_FACTOR_X);
		float fCanvasYPos =	(vertexRep.getYPosition() * SCALING_FACTOR_Y);
		
		float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
		float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
		
		// Create and store unique picking ID for that object
//		if (bPickingRendering)
//		{
			iUniqueObjectPickId++;
			gl.glLoadName(iUniqueObjectPickId);
			hashPickID2VertexRep.put(iUniqueObjectPickId, vertexRep);			
//		}
		
		EPathwayVertexShape shape = vertexRep.getShapeType();
		
		gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

		// Pathway link
		if (shape.equals(EPathwayVertexShape.roundrectangle))
		{		
			if (bEnableGeneMapping)
				tmpNodeColor = refRenderStyle.getPathwayNodeColor(true);
			else
				tmpNodeColor = refRenderStyle.getPathwayNodeColor(false);
				
			gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
					tmpNodeColor.getGreen() / 255.0f, 
					tmpNodeColor.getBlue() / 255.0f, 1.0f);

			fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		}
		// Compound
		else if (shape.equals(EPathwayVertexShape.circle))
		{				
			if (bEnableGeneMapping)
				tmpNodeColor = refRenderStyle.getCompoundNodeColor(true);
			else
				tmpNodeColor = refRenderStyle.getCompoundNodeColor(false);
			
			gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
					tmpNodeColor.getGreen() / 255.0f, 
					tmpNodeColor.getBlue() / 255.0f, 1.0f);
			gl.glCallList(iCompoundNodeDisplayListId);
		}	
		// Enzyme
		else if (shape.equals(EPathwayVertexShape.rectangle))
		{	
			// Handle selection highlighting of element
			if (hashSelectedGraphItemRepId2Depth.containsKey(vertexRep.getId()))
			{
				int iDepth = hashSelectedGraphItemRepId2Depth.get(vertexRep.getId());
				//tmpNodeColor = refRenderStyle.getNeighborhoodNodeColorByDepth(iDepth);
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				
				if (iDepth != 0)
				{
					gl.glEnable(GL.GL_LINE_STIPPLE);	
				}
				
				if(iDepth == 1)
				{
					gl.glLineStipple(4, (short) 0xAAAA);
				}
				else if(iDepth == 2)
				{
					gl.glLineStipple(2, (short) 0xAAAA);
				}
				else if(iDepth == 3)
				{
					gl.glLineStipple(1, (short) 0xAAAA);					
				}
			
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
						tmpNodeColor.getGreen() / 255.0f, 
						tmpNodeColor.getBlue() / 255.0f, 1.0f);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
				gl.glDisable(GL.GL_LINE_STIPPLE);
			}
			
			if (bEnableGeneMapping)
			{
				mapExpression(gl, vertexRep, fNodeWidth);
			}
			else
			{
				tmpNodeColor = refRenderStyle.getEnzymeNodeColor(false);
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
					tmpNodeColor.getGreen() / 255.0f, 
					tmpNodeColor.getBlue() / 255.0f, 1.0f);
				
				gl.glCallList(iEnzymeNodeDisplayListId);
			}
		}
		
		gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
	}
	
	private void createEdge(final GL gl, 
			IGraphItem edgeRep, 
			PathwayGraph refContainingPathway) {

		List<IGraphItem> listGraphItemsIn = edgeRep.getAllItemsByProp(EGraphItemProperty.INCOMING);
		List<IGraphItem> listGraphItemsOut = edgeRep.getAllItemsByProp(EGraphItemProperty.OUTGOING);
		
		if (listGraphItemsIn.isEmpty() || listGraphItemsOut.isEmpty())
		{
//			refGeneralManager.getSingelton().logMsg(
//					this.getClass().getSimpleName()
//							+ ": createEdge(): Edge has either no incoming or outcoming vertex.",
//					LoggerType.VERBOSE);
			
			return;
		}
		
		Color tmpColor;
		float fReactionLineOffset = 0;
		
		// Check if edge is a reaction
		if (edgeRep.getClass().equals(
				PathwayReactionEdgeGraphItemRep.class))
		{
			tmpColor = refRenderStyle.getReactionEdgeColor();
			fReactionLineOffset = 0.01f;
		}
		// Check if edge is a relation
		else if (edgeRep.getClass().equals(
				PathwayRelationEdgeGraphItemRep.class))
		{
			tmpColor = refRenderStyle.getRelationEdgeColor();
		}
		else
		{
			tmpColor = Color.BLACK;
		}

		gl.glLineWidth(3.0f);
		gl.glColor3f(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue());
		gl.glBegin(GL.GL_LINES);
		
		Iterator<IGraphItem> iterSourceGraphItem = 
			listGraphItemsIn.iterator();
		Iterator<IGraphItem> iterTargetGraphItem = 
			listGraphItemsOut.iterator();
		
		PathwayVertexGraphItemRep tmpSourceGraphItem;
		PathwayVertexGraphItemRep tmpTargetGraphItem;
		while(iterSourceGraphItem.hasNext()) {
			
			tmpSourceGraphItem = (PathwayVertexGraphItemRep)iterSourceGraphItem.next();
			
			while (iterTargetGraphItem.hasNext())
			{
				tmpTargetGraphItem = (PathwayVertexGraphItemRep)iterTargetGraphItem.next();
				
				gl.glVertex3f(tmpSourceGraphItem.getXPosition() * SCALING_FACTOR_X + fReactionLineOffset,
						-tmpSourceGraphItem.getYPosition() * SCALING_FACTOR_Y + fReactionLineOffset,
						0.02f);
				gl.glVertex3f(tmpTargetGraphItem.getXPosition() * SCALING_FACTOR_X + fReactionLineOffset,
						-tmpTargetGraphItem.getYPosition() * SCALING_FACTOR_Y + fReactionLineOffset,
						0.02f);		
			}
		}
		
		gl.glEnd();	
	}
	
	public void renderPathway(final GL gl, 
			final int iPathwayID, 
			boolean bRenderLabels) {
		
		if (bEnableEdgeRendering)
		{		
			int iTmpEdgesDisplayListID = hashPathwayId2EdgesDisplayListId.get(iPathwayID);
			gl.glCallList(iTmpEdgesDisplayListID);
		}
		
		Integer iTmpVerticesDisplayListID = hashPathwayId2VerticesDisplayListId.get(iPathwayID);
		
		if  (iTmpVerticesDisplayListID!=null) {
			gl.glCallList(iTmpVerticesDisplayListID);
			
			if (bRenderLabels && bEnableAnnotation)
				renderLabels(gl, iPathwayID);
		}
	}
	
	private void renderLabels(final GL gl, final int iPathwayID) {

	    PathwayVertexGraphItemRep vertexRep;
		PathwayGraph tmpPathway = (PathwayGraph)refGeneralManager.getSingelton().getPathwayManager().
			getItem(iPathwayID);
        
	    Iterator<IGraphItem> vertexRepIterator = tmpPathway.getAllItemsByKind(
				EGraphItemKind.NODE).iterator();

	    while (vertexRepIterator.hasNext())
        {
        	vertexRep = (PathwayVertexGraphItemRep) vertexRepIterator.next();

        	if (vertexRep != null)
        	{
        		float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
        		float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
        		float fCanvasXPos = (vertexRep.getXPosition() * SCALING_FACTOR_X);
        		float fCanvasYPos =	(vertexRep.getYPosition() * SCALING_FACTOR_Y);
 
        		gl.glTranslated(fCanvasXPos - fNodeWidth + 0.01f, -fCanvasYPos - 0.01f, 0);
        		gl.glColor3f(0, 0 , 0);
    			GLTextUtils.renderTextInRegion(gl, vertexRep.getName(), 10, 0, 0, 0.03f, fNodeWidth, fNodeHeight);
    			//GLTextUtils.renderText(gl, vertexRep.getName(), 0, 0, -0.03f);
    			gl.glTranslated(-fCanvasXPos + fNodeWidth - 0.01f, fCanvasYPos + 0.01f, 0);      	
        	}
        }  
	}
	
	public void mapExpression(final GL gl, 
			final PathwayVertexGraphItemRep pathwayVertexRep, 
			final float fNodeWidth) {
		
		ArrayList<Color> alMappingColor;
		
		// Check if vertex is already mapped 
		if (hashElementId2MappingColorArray.containsKey(pathwayVertexRep.getId()))
		{
			// Load existing mapping
			alMappingColor = hashElementId2MappingColorArray.get(pathwayVertexRep.getId());
		}
		else
		{
			// Request mapping
			alMappingColor = enzymeToExpressionColorMapper.getMappingColorArrayByVertexRep(pathwayVertexRep);
			hashElementId2MappingColorArray.put((Integer)pathwayVertexRep.getId(), alMappingColor);
		}
		
		drawMapping(gl, alMappingColor, fNodeWidth);
	}
	
	public void mapExpressionByGeneId(final GL gl, 
			String sGeneID, final float fNodeWidth) {

		drawMapping(gl, 
				enzymeToExpressionColorMapper.getMappingColorArrayByGeneID(sGeneID),
				fNodeWidth);
	}
	
	private void drawMapping(final GL gl,
			final ArrayList<Color> alMappingColor,
			final float fNodeWidth) {
		
		// Factor indicates how often the enzyme needs to be split
		// so that all genes can be mapped.
		int iSplitFactor = alMappingColor.size();
		Color tmpNodeColor = null;
		
		gl.glPushMatrix();
		
		if (iSplitFactor > 1)
		{
			gl.glTranslatef(-fNodeWidth, 0.0f, 0.0f);
			gl.glTranslatef(fNodeWidth * 2.0f / (iSplitFactor * 2.0f), 0.0f, 0.0f);
		}		
		for (int iSplitIndex = 0; iSplitIndex < iSplitFactor; iSplitIndex++)
		{
			tmpNodeColor = alMappingColor.get(iSplitIndex);
			
			// Check if the mapping gave back a valid color
			if (!tmpNodeColor.equals(Color.BLACK))
			{
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
						tmpNodeColor.getGreen() / 255.0f, 
						tmpNodeColor.getBlue() / 255.0f, 1.0f);
			
			}
			// Take the default color
			else
			{
				//gl.glColor4f(0.53f, 0.81f, 1.0f, 1.0f); // ligth blue
				tmpNodeColor = refRenderStyle.getEnzymeNodeColor(bEnableGeneMapping);
			}
			
			gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
					tmpNodeColor.getGreen() / 255.0f, 
					tmpNodeColor.getBlue() / 255.0f, 1.0f);
			
			gl.glScalef(1.0f / iSplitFactor, 1.0f, 1.0f);
			gl.glCallList(iEnzymeNodeDisplayListId);
			gl.glScalef(iSplitFactor, 1.0f, 1.0f);

			if (iSplitFactor > 1)
			{
				gl.glTranslatef(fNodeWidth * 2.0f / iSplitFactor, 0.0f, 0.0f);
			}
		}

		gl.glPopMatrix();

	}
	
	public PathwayVertexGraphItemRep getVertexRepByPickID(int iPickID) {
		
		return hashPickID2VertexRep.get(iPickID);
	}
	
	public void clearOldPickingIDs() {
		
		hashPickID2VertexRep.clear();
		iUniqueObjectPickId = GLCanvasJukeboxPathway3D.FREE_PICKING_ID_RANGE_START;
	}
	
	public void updateSelectionSet(final SetSelection setSelection) 
	{
		alSetSelection.add(0, setSelection);
	}
	
	public void enableEdgeRendering(final boolean bEnableEdgeRendering) {
		
		this.bEnableEdgeRendering = bEnableEdgeRendering;
	}
	
	public void enableGeneMapping(final boolean bEnableGeneMappging) {
		
		this.bEnableGeneMapping = bEnableGeneMappging;
	}
	
	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		
		this.bEnableIdenticalNodeHighlighting = bEnableIdenticalNodeHighlighting;
	}
	
	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		
		this.bEnableNeighborhood = bEnableNeighborhood;
	}
	
	public void enableAnnotation(final boolean bEnableAnnotation) {
		
		this.bEnableAnnotation = bEnableAnnotation;
	}
}
