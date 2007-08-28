package cerberus.view.gui.opengl.canvas.pathway;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraphItem;

import cerberus.data.collection.ISet;
import cerberus.data.graph.core.PathwayGraph;
import cerberus.data.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import cerberus.data.graph.item.vertex.EPathwayVertexShape;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItem;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.mapping.EnzymeToExpressionColorMapper;
import cerberus.view.gui.opengl.util.GLTextUtils;

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
	
	private HashMap<Integer, PathwayVertexGraphItemRep> hashPickID2VertexRep;
	
	private HashMap<Integer, Integer> hashPathwayId2VerticesDisplayListId;
	
	private HashMap<Integer, Integer> hashPathwayId2EdgesDisplayListId;	
	
	private EnzymeToExpressionColorMapper enzymeToExpressionColorMapper;
	
	private ArrayList<Integer> iAlSelectedElements;
	
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
			final ArrayList<Integer> iAlSelectedElements) {
		
		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		
		this.iAlSelectedElements = iAlSelectedElements;
		
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
		
		gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);	
		extractVertices(gl, refTmpPathway);
		gl.glEndList();
		
		gl.glNewList(iEdgesDisplayListId, GL.GL_COMPILE);	
		extractEdges(gl, refTmpPathway);
		gl.glEndList();
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
			if (iAlSelectedElements.contains(
					vertexRep.getPathwayVertexGraphItem().getId()))
			{
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
						tmpNodeColor.getGreen() / 255.0f, 
						tmpNodeColor.getBlue() / 255.0f, 1.0f);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
			}
			
			if (bEnableGeneMapping)
			{
				mapExpression(gl, vertexRep.getPathwayVertexGraphItem(), fNodeWidth);
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
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName()
							+ ": createEdge(): Edge has either no incoming or outcoming vertex.",
					LoggerType.VERBOSE);
			
			return;
		}
		
		Color tmpColor;
		float fReactionLineOffset = 0;
		
		// Check if edge is a reaction
		if (edgeRep.getClass().getName().equals(
				cerberus.data.graph.item.edge.PathwayReactionEdgeGraphItemRep.class.getName()))
		{
			tmpColor = refRenderStyle.getReactionEdgeColor();
			fReactionLineOffset = 0.01f;
		}
		// Check if edge is a relation
		else if (edgeRep.getClass().getName().equals(
				cerberus.data.graph.item.edge.PathwayRelationEdgeGraphItemRep.class.getName()))
		{
			tmpColor = refRenderStyle.getRelationEdgeColor();
		}
		else
		{
			tmpColor = Color.BLACK;
		}

		gl.glLineWidth(2.0f);
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
						0.05f);
				gl.glVertex3f(tmpTargetGraphItem.getXPosition() * SCALING_FACTOR_X + fReactionLineOffset,
						-tmpTargetGraphItem.getYPosition() * SCALING_FACTOR_Y + fReactionLineOffset,
						0.05f);		
			}
		}
		
		gl.glEnd();	
	}
	
	public void renderPathway(final GL gl, final int iPathwayID, boolean bRenderLabels) {
		
		int iTmpEdgesDisplayListID = hashPathwayId2EdgesDisplayListId.get(iPathwayID);
		gl.glCallList(iTmpEdgesDisplayListID);
		
		int iTmpVerticesDisplayListID = hashPathwayId2VerticesDisplayListId.get(iPathwayID);
		gl.glCallList(iTmpVerticesDisplayListID);
		
		if (bRenderLabels)
			renderLabels(gl, iPathwayID);
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
			final PathwayVertexGraphItem pathwayVertex, 
			final float fNodeWidth) {
		
		ArrayList<Color> alMappingColor;
		
		// Check if vertex is already mapped 
		if (hashElementId2MappingColorArray.containsKey(pathwayVertex.getId()))
		{
			// Load existing mapping
			alMappingColor = hashElementId2MappingColorArray.get(pathwayVertex.getId());
		}
		else
		{
			// Request mapping
			alMappingColor = enzymeToExpressionColorMapper.getMappingColorArrayByVertex(pathwayVertex);
			hashElementId2MappingColorArray.put((Integer)pathwayVertex.getId(), alMappingColor);
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
}
