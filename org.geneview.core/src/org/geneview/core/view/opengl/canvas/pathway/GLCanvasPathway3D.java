package org.geneview.core.view.opengl.canvas.pathway;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.util.GLInfoAreaRenderer;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

import com.sun.opengl.util.BufferUtil;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 */
public class GLCanvasPathway3D 
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {

	private int iPathwayID = -1;
	
	private float fLastMouseMovedTimeStamp = 0;
	
	private boolean bRebuildVisiblePathwayDisplayLists = false;
	private boolean bIsMouseOverPickingEvent = false;
	private boolean bEnablePathwayTexture = true;
	private boolean bSelectionChanged = false;
	private boolean bUpdateReceived = false;

	private int iMouseOverPickedPathwayId = -1;

	private IPathwayManager pathwayManager;
	
	private GLPathwayManager refGLPathwayManager;

	private GLPathwayTextureManager refGLPathwayTextureManager;

	private PickingJoglMouseListener pickingTriggerMouseAdapter;

	private PathwayVertexGraphItemRep selectedVertex;

	private GLInfoAreaRenderer infoAreaRenderer;

	/**
	 * Hash map stores which pathways contain the currently selected vertex and
	 * how often this vertex is contained.
	 */
	private HashMap<Integer, Integer> refHashPathwayContainingSelectedVertex2VertexCount;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasPathway3D(final IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, null, iViewId, iParentContainerId, "");

		this.refViewCamera.setCaller(this);

		pathwayManager = refGeneralManager.getSingelton().getPathwayManager();
		
		refGLPathwayManager = new GLPathwayManager(refGeneralManager);
		refGLPathwayTextureManager = new GLPathwayTextureManager(
				refGeneralManager);
		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();

		pickingTriggerMouseAdapter = (PickingJoglMouseListener) openGLCanvasDirector
				.getJoglCanvasForwarder().getJoglMouseListener();

		infoAreaRenderer = new GLInfoAreaRenderer(refGeneralManager,
				refGLPathwayManager);
		infoAreaRenderer.enableColorMappingArea(true);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.view.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas(GL gl) {

		// Clearing window and set background to WHITE
		// is already set inside JoglCanvasForwarder
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);

		initPathwayData(gl);

		setInitGLDone();
	}

	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, alSetSelection);
		loadAllPathways(gl);
//		buildPathwayDisplayList(gl);
	}

	public void renderPart(GL gl) {
		
		handlePicking(gl);
				
		if (bRebuildVisiblePathwayDisplayLists)
			rebuildPathwayDisplayList(gl);

		renderScene(gl);
		renderInfoArea(gl);
	}

	public void renderScene(final GL gl) {
		
		renderPathwayById(gl, iPathwayID);
	}


	private void loadAllPathways(final GL gl) {
		
		// Check if pathways are already loaded
		if (!pathwayManager.getRootPathway().getAllGraphByType(
				EGraphItemHierarchy.GRAPH_CHILDREN).isEmpty()) 
		{
			return;
		}
		
		// Load KEGG pathways
		pathwayManager.loadAllPathwaysByType(EPathwayDatabaseType.KEGG);

		// Load BioCarta pathways
		pathwayManager.loadAllPathwaysByType(EPathwayDatabaseType.BIOCARTA);
		
		Iterator<IGraph> iterPathwayGraphs = pathwayManager
			.getRootPathway().getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN).iterator();

		while(iterPathwayGraphs.hasNext())
		{
			iterPathwayGraphs.next();
			iPathwayID = iterPathwayGraphs.next().getId();
			break;
		}
	}

	private void renderPathwayById(final GL gl,
			final int iPathwayId) {
		
		gl.glPushMatrix();
		
		if (bEnablePathwayTexture)
		{
			refGLPathwayTextureManager.renderPathway(gl, iPathwayId, 1.0f, false);
		}

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * 
			((PathwayGraph)pathwayManager.getItem(iPathwayId)).getHeight();
		
		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);
		refGLPathwayManager.renderPathway(gl, iPathwayId, true);
		gl.glTranslatef(0, -tmp, 0);
		
		gl.glPopMatrix();
		
	}
	
	private void renderInfoArea(final GL gl) {

		if (selectedVertex != null && infoAreaRenderer.isPositionValid())
		{
			infoAreaRenderer.renderInfoArea(gl, selectedVertex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.geneview.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

	}

	public void updateReceiver(Object eventTrigger) {

	}

	private void handlePicking(final GL gl) {

		Point pickPoint = null;

		boolean bMouseReleased =
			pickingTriggerMouseAdapter.wasMouseReleased();
		
		if (pickingTriggerMouseAdapter.wasMousePressed()
				|| bMouseReleased)
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			bIsMouseOverPickingEvent = false;
		}

		if (pickingTriggerMouseAdapter.wasMouseMoved())
		{
			// Restart timer
			fLastMouseMovedTimeStamp = System.nanoTime();
			bIsMouseOverPickingEvent = true;
		} 
		else if (bIsMouseOverPickingEvent == true
				&& System.nanoTime() - fLastMouseMovedTimeStamp >= 0)// 1e9)
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			fLastMouseMovedTimeStamp = System.nanoTime();
		}
		else if (pickingTriggerMouseAdapter.wasMouseDragged())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
		}

		// Check if an object was picked
		if (pickPoint != null)
		{	
			pickObjects(gl, pickPoint);
			bIsMouseOverPickingEvent = false;
		}
	}

	private void pickObjects(final GL gl, Point pickPoint) {

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		//gl.glPushName(0);

		/* create 1x1 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 1.0, viewport, 0); // pick width and height is set to 1
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -4*h, 4*h, 1.0f, 1000.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		renderScene(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint);
	}

	protected void processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], final Point pickPoint) {

		// System.out.println("Number of hits: " +iHitCount);

		int iPtr = 0;
		int i = 0;

		int iPickedObjectId = 0;

		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		for (i = 0; i < iHitCount; i++)
		{
			iPtr++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPtr] < iMinimumZValue)
			{
				iMinimumZValue = iArPickingBuffer[iPtr];
				iPtr++;
				iPtr++;
				iPickedObjectId = iArPickingBuffer[iPtr];
			}
			iPtr++;
		}
		
		if (iPickedObjectId == 0)
		{
			// Remove pathway pool fisheye
			iMouseOverPickedPathwayId = -1;

			infoAreaRenderer.resetPoint();

			return;
		}

		PathwayVertexGraphItemRep pickedVertexRep
			= refGLPathwayManager.getVertexRepByPickID(iPickedObjectId);

		if (pickedVertexRep == null)
			return;

		if (selectedVertex != null
				&& !selectedVertex.equals(pickedVertexRep))
		{
			loadNodeInformationInBrowser(((PathwayVertexGraphItem)pickedVertexRep.getAllItemsByProp(
					EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
			
			infoAreaRenderer.resetAnimation();
		}
		
		// Remove pathway pool fisheye
		iMouseOverPickedPathwayId = -1;

		// System.out.println("Picked node:" +refPickedVertexRep.getName());

		// Reset pick point
		infoAreaRenderer.convertWindowCoordinatesToWorldCoordinates(gl,
				pickPoint.x, pickPoint.y);

		// If event is just mouse over (and not real picking)
		// highlight the object under the cursor
		if (bIsMouseOverPickingEvent)
		{
			if (selectedVertex == null ||
					(selectedVertex != null && !selectedVertex.equals(pickedVertexRep)))
			{
				selectedVertex = pickedVertexRep;
				bSelectionChanged = true;
			}
			
			return;
		}

		if (pickedVertexRep.getPathwayVertexGraphItem().getType().equals(
				EPathwayVertexType.map))
		{
			String strTmp = pickedVertexRep.getPathwayVertexGraphItem().getName();

			int iPathwayId = -1;
			try
			{
				iPathwayId = Integer.parseInt(strTmp
						.substring(strTmp.length() - 4));
			} catch (NumberFormatException e)
			{
				return;
			}

			loadPathwayToUnderInteractionPosition(iPathwayId);

			return;
		}
		else if (pickedVertexRep.getPathwayVertexGraphItem().getType()
				.equals(EPathwayVertexType.enzyme) 
			|| pickedVertexRep.getPathwayVertexGraphItem().getType()
				.equals(EPathwayVertexType.gene)
			|| pickedVertexRep.getPathwayVertexGraphItem().getType()
			.equals(EPathwayVertexType.other)) // FIXME: just for testing BioCarta integration
		{
			selectedVertex = pickedVertexRep;
			bSelectionChanged = true;

			loadDependentPathwayBySingleVertex(gl, selectedVertex);
		}
	}

	private void loadPathwayToUnderInteractionPosition(final int iPathwayId) {
		
		loadNodeInformationInBrowser(((PathwayGraph)pathwayManager
				.getItem(iPathwayId)).getExternalLink());

		// Check if selected pathway is loaded.
		if (!pathwayManager.hasItem(iPathwayId))
		{
			return;
		}

		bRebuildVisiblePathwayDisplayLists = true;

		// Trigger update with current pathway that dependent pathways
		// know which pathway is currently under interaction
		int[] iArOptional = new int[1];
		iArOptional[0] = iPathwayId;
		alSetSelection.get(0).updateSelectionSet(iUniqueId, new int[0],
				new int[0], iArOptional);
	}
	
	public void loadDependentPathwayBySingleVertex(final GL gl,
			final PathwayVertexGraphItemRep vertex) {
		
		Iterator<IGraphItem> iterVertexGraphItems = 
			vertex.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).iterator();

		ArrayList<IGraphItem> alSelectedVertexGraphItemReps = 
			new ArrayList<IGraphItem>();
		
		// Remove duplicates by adding to a hash list
		HashSet<IGraphItem> set = new HashSet<IGraphItem>();
		
		while(iterVertexGraphItems.hasNext())
		{
			set.addAll(iterVertexGraphItems.next().getAllItemsByProp(
					EGraphItemProperty.ALIAS_CHILD));
		}
		
		alSelectedVertexGraphItemReps.addAll(set);
				
		loadDependentPathways(gl, alSelectedVertexGraphItemReps);
	}
		
	public void loadDependentPathways(final GL gl,
			final List<IGraphItem> alVertexRep) {

		refHashPathwayContainingSelectedVertex2VertexCount.clear();
		
		Iterator<IGraphItem> iterIdenticalPathwayGraphItemReps = 
			alVertexRep.iterator();
		
		IGraphItem identicalPathwayGraphItemRep;
		int iPathwayId = 0;
		int iMaxPathwayCount = 0;
		
		while (iterIdenticalPathwayGraphItemReps.hasNext())
		{
			identicalPathwayGraphItemRep = iterIdenticalPathwayGraphItemReps.next();
	
			iPathwayId = ((PathwayGraph)identicalPathwayGraphItemRep
					.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0]).getKeggId();


			// Check if pathway has already a vertex counted
			if (refHashPathwayContainingSelectedVertex2VertexCount.containsKey(iPathwayId))
			{
				// Increase current stored identical vertex count by 1
				refHashPathwayContainingSelectedVertex2VertexCount.put(
						iPathwayId, refHashPathwayContainingSelectedVertex2VertexCount.get(iPathwayId) + 1);
			}
			else
			{
				refHashPathwayContainingSelectedVertex2VertexCount.put(iPathwayId, 1);
			}
		}
		
		bRebuildVisiblePathwayDisplayLists = true;
	}

	private void rebuildPathwayDisplayList(final GL gl) {

		// Reset rebuild trigger flag
		bRebuildVisiblePathwayDisplayLists = false;
		
		refGLPathwayManager.clearOldPickingIDs();

		if (selectedVertex != null)
		{
			// Write currently selected vertex to selection set
			int[] iArTmpSelectionId = new int[1];
			int[] iArTmpDepth = new int[1];
			iArTmpSelectionId[0] = selectedVertex.getId();
			iArTmpDepth[0] = 0;
			alSetSelection.get(0).getWriteToken();
			alSetSelection.get(0).updateSelectionSet(iUniqueId, iArTmpSelectionId, iArTmpDepth, new int[0]);
			alSetSelection.get(0).returnWriteToken();
		}
			
		refGLPathwayManager.performIdenticalNodeHighlighting();
		

		refGLPathwayManager.buildPathwayDisplayList(gl, 261);
		
		// Cleanup unused textures
//		refGLPathwayTextureManager.unloadUnusedTextures(getVisiblePathways());

	}

	public void loadNodeInformationInBrowser(String sUrl) {

		if (sUrl.isEmpty())
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) refGeneralManager
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}
	
	public void setMappingRowCount(final int iMappingRowCount) {
		
		refGLPathwayManager.setMappingRowCount(iMappingRowCount);
	}
		
	public void enableGeneMapping(final boolean bEnableMapping) {
		
		refGLPathwayManager.enableGeneMapping(bEnableMapping);
		bRebuildVisiblePathwayDisplayLists = true;
	}
	
	public void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		
		refGLPathwayManager.enableEdgeRendering(!bEnablePathwayTexture);
		bRebuildVisiblePathwayDisplayLists = true;
		
		this.bEnablePathwayTexture = bEnablePathwayTexture;
	}
	
	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		
		bRebuildVisiblePathwayDisplayLists = true;
		refGLPathwayManager.enableNeighborhood(bEnableNeighborhood);
	}
	
	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		
		bRebuildVisiblePathwayDisplayLists = true;
		refGLPathwayManager.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}
	
	public void enableAnnotation(final boolean bEnableAnnotation) {
		
		refGLPathwayManager.enableAnnotation(bEnableAnnotation);
	}
}