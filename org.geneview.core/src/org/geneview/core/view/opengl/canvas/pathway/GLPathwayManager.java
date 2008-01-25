package org.geneview.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemKind;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraphItem;
import org.geneview.util.graph.algorithm.GraphVisitorSearchBFS;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.edge.PathwayReactionEdgeGraphItemRep;
import org.geneview.core.data.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexShape;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.util.mapping.AGenomeMapper;
import org.geneview.core.util.mapping.EGenomeMappingCascadeType;
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
	private int iHighlightedCompoundNodeDisplayListId = -1;
	
	// First 200 IDs are reserved for picking of non pathway objects in the scene
	private int iUniqueObjectPickId = GLCanvasJukeboxPathway3D.FREE_PICKING_ID_RANGE_START;
	
	private PathwayRenderStyle refRenderStyle;

	private boolean bEnableGeneMapping = true;
	private boolean bEnableEdgeRendering = false;
	private boolean bEnableIdenticalNodeHighlighting = true;
	private boolean bEnableNeighborhood = false;
	private boolean bEnableAnnotation = true;
	
	private HashMap<Integer, PathwayVertexGraphItemRep> hashPickID2VertexRep;
	
	private HashMap<Integer, Integer> hashPathwayId2VerticesDisplayListId;
	
	private HashMap<Integer, Integer> hashPathwayId2EdgesDisplayListId;	
	
	private AGenomeMapper genomeMapper;
	
	private ArrayList<SetSelection> alSetSelection;
	
	private HashMap<Integer, Integer> hashSelectedVertexRepId2Depth;
	private ArrayList<Integer> iArSelectedEdgeRepId;
	
	private HashMap<Integer, ArrayList<Vec3f>> hashElementId2MappingColorArray;
	
	/**
	 * Constructor.
	 */
	public GLPathwayManager(final IGeneralManager refGeneralManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		refRenderStyle = new PathwayRenderStyle();
		hashPickID2VertexRep = new HashMap<Integer, PathwayVertexGraphItemRep>();
		hashPathwayId2VerticesDisplayListId = new HashMap<Integer, Integer>();
		hashPathwayId2EdgesDisplayListId = new HashMap<Integer, Integer>();		
		hashElementId2MappingColorArray = new HashMap<Integer, ArrayList<Vec3f>>();
	}
	
	public void init(final GL gl, 
			final ArrayList<ISet> alSetData,
			final ArrayList<SetSelection> alSetSelection) {
		
		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);
		
		this.alSetSelection = alSetSelection;
		hashSelectedVertexRepId2Depth = new HashMap<Integer, Integer>();
		iArSelectedEdgeRepId = new ArrayList<Integer>();
		
		// Initialize genome mapper
		genomeMapper = refGeneralManager.getSingelton().getGenomeIdManager()
			.getGenomeMapperByMappingCascadeType(
					EGenomeMappingCascadeType.ENZYME_2_NCBI_GENEID_2_ACCESSION_2_MICROARRAY_EXPRESSION_STORAGE_INDEX);
		genomeMapper.setMappingData(alSetData);
	}
	
	public void buildPathwayDisplayList(final GL gl, final int iPathwayId) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
				+ ": buildPathwayDisplayList(): Build display list for pathway: "+iPathwayId,
				LoggerType.VERBOSE);
		
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
		
//		performIdenticalNodeHighlighting();
		
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

	public void performIdenticalNodeHighlighting() {
		
		hashSelectedVertexRepId2Depth.clear();
		iArSelectedEdgeRepId.clear();
		
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
			hashSelectedVertexRepId2Depth.put(
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
					
					hashSelectedVertexRepId2Depth.put(
							identicalNode.getId(), 0);
				
					performNeighborhoodAlgorithm(identicalNode);
				}
			}	
		}
	
		// Store currently selected vertices back to selection set
		Set<Entry<Integer, Integer>> setAllSelectedVertices = hashSelectedVertexRepId2Depth.entrySet();
		
		int[] iArTmpGraphItemId = new int[setAllSelectedVertices.size()];  
		int[] iArTmpGraphItemDepth = new int[setAllSelectedVertices.size()];
		
		Iterator<Entry<Integer, Integer>> iterAllSelectedVertices = setAllSelectedVertices.iterator();
		
		int iItemIndex = 0;
		Entry<Integer, Integer> tmpEntry;
		while (iterAllSelectedVertices.hasNext())
		{
			tmpEntry = iterAllSelectedVertices.next();
			
			iArTmpGraphItemId[iItemIndex] = tmpEntry.getKey();
			iArTmpGraphItemDepth[iItemIndex] = tmpEntry.getValue();
			iItemIndex++;
		}
	
		alSetSelection.get(0).getWriteToken();
		alSetSelection.get(0).setSelectionIdArray(iArTmpGraphItemId);	
		alSetSelection.get(0).setGroupArray(iArTmpGraphItemDepth);
		alSetSelection.get(0).returnWriteToken();
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
				// Check if selected item is a vertex
				if (lGraphItems.get(iItemIndex).getClass().equals
						(org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep.class))
				{			
					hashSelectedVertexRepId2Depth.put(
							lGraphItems.get(iItemIndex).getId(), (iDepthIndex+1) / 2); // consider only vertices for depth
				}
				else
				{
					iArSelectedEdgeRepId.add(lGraphItems.get(iItemIndex).getId());
				}
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
	
	protected void buildHighlightedCompoundNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		iHighlightedCompoundNodeDisplayListId = gl.glGenLists(1);
		
		float fNodeWidth = refRenderStyle.getCompoundNodeWidth(true);
		float fNodeHeight = refRenderStyle.getCompoundNodeHeight(true);
		
		gl.glNewList(iHighlightedCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
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
        		if (bEnableEdgeRendering)
        		{
        			createEdge(gl, edgeRep, pathwayToExtract);
        		}
        		// Render edge if it is contained in the minimum spanning tree of the neighborhoods
        		else if(iArSelectedEdgeRepId.contains(edgeRep.getId()))
                {
            		createEdge(gl, edgeRep, pathwayToExtract); 
                }       	        			
        	}
        }   
	}
	
	private void createVertex(final GL gl, 
			PathwayVertexGraphItemRep vertexRep, 
			PathwayGraph refContainingPathway) {
		
		Vec3f tmpNodeColor = null;
		
		// Create and store unique picking ID for that object
//		if (bPickingRendering)
//		{
			iUniqueObjectPickId++;
			gl.glLoadName(iUniqueObjectPickId);
			hashPickID2VertexRep.put(iUniqueObjectPickId, vertexRep);			
//		}
		
		EPathwayVertexShape shape = vertexRep.getShapeType();

		EPathwayVertexType vertexType = 
			((PathwayVertexGraphItem)vertexRep.getPathwayVertexGraphItem()).getType();
		
		// Pathway link
		//if (shape.equals(EPathwayVertexShape.roundrectangle))
		if (vertexType.equals(EPathwayVertexType.map))
		{	
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos =	(vertexRep.getYOrigin() * SCALING_FACTOR_Y);		
			float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
			
			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);
			
			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				
				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			}
			
			if (bEnableGeneMapping)
				tmpNodeColor = refRenderStyle.getPathwayNodeColor(true);
			else
				tmpNodeColor = refRenderStyle.getPathwayNodeColor(false);
				
			gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);

			fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
			
			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		// Compound
		//else if (shape.equals(EPathwayVertexShape.circle))
		else if (vertexType.equals(EPathwayVertexType.compound))
		{		
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos =	(vertexRep.getYOrigin() * SCALING_FACTOR_Y);		
			
			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);
			
			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
							
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			}
			
			if (bEnableGeneMapping)
				tmpNodeColor = refRenderStyle.getCompoundNodeColor(true);
			else
				tmpNodeColor = refRenderStyle.getCompoundNodeColor(false);
			
			gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
			gl.glCallList(iCompoundNodeDisplayListId);
			
			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}	
		else if (shape.equals(EPathwayVertexShape.poly)) // BIOCARTA
		{			
			gl.glColor4f(0,0,0,0);
			
			short[][] shArCoords = vertexRep.getCoords();
			
			gl.glBegin(GL.GL_POLYGON);			
			for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++)
			{
				gl.glVertex3f(shArCoords[iPointIndex][0] * SCALING_FACTOR_X, 
						-shArCoords[iPointIndex][1] * SCALING_FACTOR_Y, 0.015f);					
			}			
			gl.glEnd();
		
			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
			}
			else
				gl.glColor4f(1, 1, 0, 1);

			
			gl.glBegin(GL.GL_LINE_STRIP);			
			for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++)
			{
				gl.glVertex3f(shArCoords[iPointIndex][0] * SCALING_FACTOR_X, 
						-shArCoords[iPointIndex][1] * SCALING_FACTOR_Y, 0.015f);					
			}			
			gl.glEnd();
		}
		// Enzyme
		//else if (shape.equals(EPathwayVertexShape.rectangle)
		//		|| shape.equals(EPathwayVertexShape.rect))
		else if (vertexType.equals(EPathwayVertexType.gene)
				|| vertexType.equals(EPathwayVertexType.enzyme))
		{	
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos =	(vertexRep.getYOrigin() * SCALING_FACTOR_Y);		
			float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
			
			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);
			
			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				int iDepth = hashSelectedVertexRepId2Depth.get(vertexRep.getId());
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
			
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
				gl.glDisable(GL.GL_LINE_STIPPLE);
			}
			
			if (bEnableGeneMapping && vertexType.equals(EPathwayVertexType.gene))
			{
				mapExpression(gl, vertexRep, fNodeWidth, fNodeHeight);
			}
			else
			{
				tmpNodeColor = refRenderStyle.getEnzymeNodeColor(bEnableGeneMapping);
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				
				gl.glCallList(iEnzymeNodeDisplayListId);
			}
			
			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
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
		
		Vec3f tmpColor;
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
			tmpColor = new Vec3f(0,0,0);
		}

		gl.glLineWidth(3.0f);
		gl.glColor4f(tmpColor.x(), tmpColor.y(), tmpColor.z(), 1);
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
				
				gl.glVertex3f(tmpSourceGraphItem.getXOrigin() * SCALING_FACTOR_X + fReactionLineOffset,
						-tmpSourceGraphItem.getYOrigin() * SCALING_FACTOR_Y + fReactionLineOffset,
						0.02f);
				gl.glVertex3f(tmpTargetGraphItem.getXOrigin() * SCALING_FACTOR_X + fReactionLineOffset,
						-tmpTargetGraphItem.getYOrigin() * SCALING_FACTOR_Y + fReactionLineOffset,
						0.02f);		
			}
		}
		
		gl.glEnd();	
	}
	
	public void renderPathway(final GL gl, 
			final int iPathwayID, 
			boolean bRenderLabels) {
		
		if (bEnableEdgeRendering || !iArSelectedEdgeRepId.isEmpty())
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
		
		// Don't annotate BioCarta pathways - because of good texture annotation
		if (tmpPathway.getType().equals(EPathwayDatabaseType.BIOCARTA))
			return;
        
	    Iterator<IGraphItem> vertexRepIterator = tmpPathway.getAllItemsByKind(
				EGraphItemKind.NODE).iterator();

	    while (vertexRepIterator.hasNext())
        {
        	vertexRep = (PathwayVertexGraphItemRep) vertexRepIterator.next();

        	if (vertexRep != null)
        	{
        		float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
        		float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
        		float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
        		float fCanvasYPos =	(vertexRep.getYOrigin() * SCALING_FACTOR_Y);
 
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
			final float fNodeWidth,
			final float fNodeHeight) {
		
		ArrayList<Vec3f> alMappingColor;
		
		// Check if vertex is already mapped 
		if (hashElementId2MappingColorArray.containsKey(pathwayVertexRep.getId()))
		{
			// Load existing mapping
			alMappingColor = hashElementId2MappingColorArray.get(pathwayVertexRep.getId());
		}
		else
		{
			// Request mapping
			alMappingColor = genomeMapper.getMappingColorArrayByVertexRep(pathwayVertexRep);
			hashElementId2MappingColorArray.put((Integer)pathwayVertexRep.getId(), alMappingColor);
		}
		
		drawMapping(gl, alMappingColor, fNodeWidth, fNodeHeight);
	}
	
	public void mapExpressionByGeneId(final GL gl, 
			String sGeneID, 
			final float fNodeWidth,
			final float fNodeHeight) {

		drawMapping(gl, genomeMapper.getMappingColorArrayByGeneID(sGeneID), 
				fNodeWidth, fNodeHeight);
	}
	
	private void drawMapping(final GL gl,
			final ArrayList<Vec3f> alMappingColor,
			final float fNodeWidth,
			final float fNodeHeight) {
		
		// Factor indicates how often the enzyme needs to be split
		// so that all genes can be mapped.
		int iRowCount = 2;
		int iColumnCount = (int)Math.ceil((float)alMappingColor.size() / (float)iRowCount);
		
		Vec3f tmpNodeColor = null;
		
		gl.glPushMatrix();
		
		// If no mapping is available - render whole node in one color
		if (alMappingColor.size() == 1)
		{
			tmpNodeColor = alMappingColor.get(0);
			
			// Check if the mapping gave back a valid color
			if (tmpNodeColor.x() == -1)
				tmpNodeColor = refRenderStyle.getEnzymeNodeColor(true);

		
			gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());		
			gl.glCallList(iEnzymeNodeDisplayListId);
		}
		else
		{
			gl.glTranslatef(-fNodeWidth + fNodeWidth / iColumnCount, 
					-fNodeHeight + fNodeHeight / iRowCount, 0.0f);

			for (int iRowIndex = 0; iRowIndex < iRowCount; iRowIndex++)
			{
				for (int iColumnIndex = 0; iColumnIndex < iColumnCount; iColumnIndex++)
				{
					int iCurrentElement = iRowIndex * iRowCount + iColumnIndex;

					if (iCurrentElement < alMappingColor.size())
						tmpNodeColor = alMappingColor.get(iRowIndex+1 * iColumnIndex);
					else
						continue;
					
					// Check if the mapping gave back a valid color
					if (tmpNodeColor.x() != -1)
					{
						gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());
						gl.glScalef(1.0f / iColumnCount, 1.0f / iRowCount, 1.0f);
						gl.glCallList(iEnzymeNodeDisplayListId);
						gl.glScalef(iColumnCount, iRowCount, 1.0f);
					}

					gl.glTranslatef(fNodeWidth * 2.0f / iColumnCount, 0.0f, 0.0f);
				}
		
				gl.glTranslatef(-2.0f * fNodeWidth , 
						2.0f * fNodeHeight / iRowCount, 0.0f);
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
