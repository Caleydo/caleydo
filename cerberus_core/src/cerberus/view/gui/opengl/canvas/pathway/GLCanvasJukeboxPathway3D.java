package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Rotf;
import gleem.linalg.Transform;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import sun.java2d.pipe.GlyphListPipe;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.selection.ISetSelection;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.util.opengl.GLInfoAreaRenderer;
import cerberus.util.opengl.GLStarEffect;
import cerberus.util.opengl.GLTextUtils;
import cerberus.util.slerp.Slerp;
import cerberus.util.slerp.SlerpAction;
import cerberus.view.gui.jogl.PickingJoglMouseListener;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

import com.sun.opengl.util.BufferUtil;

/**
 * Jukebox setup that supports slerp animation.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class GLCanvasJukeboxPathway3D
extends AGLCanvasUser_OriginRotation
implements IMediatorReceiver, IMediatorSender {	
	
	public static final int MAX_LOADED_PATHWAYS = 300;
	
	private float fTextureTransparency = 1.0f; 
	
	private float fLastMouseMovedTimeStamp = 0;
	
	private boolean bIsMouseOverPickingEvent = false;
	
	private boolean bShowPathwayTexture = true;
	
	private int iMouseOverPickedPathwayId = -1;
	
	private GLPathwayManager refPathwayManager;
	
	private GLPathwayTextureManager refPathwayTextureManager;
		
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	private ArrayList<SlerpAction> arSlerpActions;
	
	private ArrayList<Integer> iAlSelectedElements;
		
	/**
	 * Slerp factor 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	private HashMap<Integer, Integer> refHashPoolLinePickId2PathwayId;
	
	private JukeboxHierarchyLayer pathwayUnderInteractionLayer; // contains only one pathway
	private JukeboxHierarchyLayer pathwayLayeredLayer;
	private JukeboxHierarchyLayer pathwayPoolLayer;
	
	private boolean bSelectionChanged = false;
	
	private PathwayVertex selectedVertex = null;
	
	private GLInfoAreaRenderer infoAreaRenderer;
	
	/**
	 * Constructor
	 * 
	 */
	public GLCanvasJukeboxPathway3D( final IGeneralManager refGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
				
		super(refGeneralManager, 
				null,
				iViewId, 
				iParentContainerId, 
				"");
		
		this.refViewCamera.setCaller(this);
	
		//refHashPathwayIdToModelMatrix = new HashMap<Integer, Mat4f>();
		refPathwayManager = new GLPathwayManager(refGeneralManager);
		refPathwayTextureManager = new GLPathwayTextureManager(refGeneralManager);
		arSlerpActions = new ArrayList<SlerpAction>();
		iAlSelectedElements = new ArrayList<Integer>(); 
		
		refHashPoolLinePickId2PathwayId = new HashMap<Integer, Integer>();
		
		// Create Jukebox hierarchy
		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer(1);
		pathwayLayeredLayer = new JukeboxHierarchyLayer(4);
		pathwayPoolLayer = new JukeboxHierarchyLayer(200);
		pathwayUnderInteractionLayer.setParentLayer(pathwayLayeredLayer);
		pathwayLayeredLayer.setChildLayer(pathwayUnderInteractionLayer);
		pathwayLayeredLayer.setParentLayer(pathwayPoolLayer);
		pathwayPoolLayer.setChildLayer(pathwayLayeredLayer);
		
		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.95f, -2f, 0f));
		transformPathwayUnderInteraction.setScale(new Vec3f(1.8f, 1.8f, 1.8f));
		transformPathwayUnderInteraction.setRotation(new Rotf(0, 0, 0, 0));
		pathwayUnderInteractionLayer.setTransformByPositionIndex(0, transformPathwayUnderInteraction);
		
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) 
			openGLCanvasDirector.getJoglCanvasForwarder().getJoglMouseListener();

		infoAreaRenderer = new GLInfoAreaRenderer();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas( GL gl ) {
		
		// Clearing window and set background to WHITE
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);	
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		//gl.glEnable(GL.GL_ALPHA_TEST);
		//gl.glAlphaFunc(GL.GL_GREATER, 0);  
		
		//gl.glEnable(GL.GL_TEXTURE_2D); 
		//gl.glTexEnvf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		//gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
		initPathwayData(gl);
		
		setInitGLDone();
	}	
	
	protected void initPathwayData(final GL gl) {
	
		refPathwayManager.init(gl, alSetData, iAlSelectedElements);
		buildPathwayPool(gl);
		buildLayeredPathways(gl);
	}

	
	public void renderPart(GL gl) {
		
		handlePicking(gl);		
		renderScene(gl);
		
		if (selectedVertex != null && infoAreaRenderer.getPoint() != null)
		{
			infoAreaRenderer.drawPickedObjectInfo(gl, selectedVertex);
		}
		
//		int viewport[] = new int[4];
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//
//		GLProjectionUtils.orthogonalStart(gl, 1, 1);
//		
//		gl.glColor3f(1,0,0);
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex2f(0, 0);
//		gl.glVertex2f(0, 1);
//		gl.glVertex2f(1, 1);
//		gl.glVertex2f(1, 0);
//		gl.glEnd();
//		
//		GLProjectionUtils.orthogonalEnd(gl);

	}
	
	public void renderScene(final GL gl) {
		
		renderPathwayPool(gl);
		renderPathwayLayered(gl);
		renderPathwayUnderInteraction(gl);

		doSlerpActions(gl);
	}
	
	private void buildLayeredPathways(final GL gl) {
		
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = -2.1f;
		int iMaxLayers = 4;
		
		// Create free pathway layer spots
		Transform transform; 
		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-3.3f, fLayerYPos, 0f));
			transform.setScale(new Vec3f(0.7f, 0.7f, 0.7f));
			transform.setRotation(new Rotf(fTiltAngleRad, -1, -0.7f, 0));
			pathwayLayeredLayer.setTransformByPositionIndex(iLayerIndex, transform);

			fLayerYPos += 1f;
		}	
	}
	
	private void buildPathwayPool(final GL gl) {
		
		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		int iMaxLines = 200;
		
		// Create free pathway spots
		Transform transform; 
		for (int iLineIndex = 0; iLineIndex < iMaxLines; iLineIndex++)
		{
			transform = new Transform();
			transform.setRotation(new Rotf(fTiltAngleRad, -1, 0, 0));
//			transform.setTranslation(new Vec3f(-4.0f, -iLineIndex * fLineHeight, 10));
//			transform.setScale(new Vec3f(0.1f,0.1f,0.1f));	
			pathwayPoolLayer.setTransformByPositionIndex(iLineIndex, transform);
		}
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		int iPathwayId = 0;		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			iPathwayId = iArPathwayIDs[iPathwayIndex];
			
			//Load pathway
			boolean bLoadingOK = 
				refGeneralManager.getSingelton().getPathwayManager().loadPathwayById(iPathwayId);
			
			if (!bLoadingOK)
				return;
		
			refPathwayManager.buildPathwayDisplayList(gl, iPathwayId);			
			pathwayPoolLayer.addElement(iPathwayId);		
		}
	}
	
	private void renderPathwayUnderInteraction(final GL gl) {
		
		// Check if a pathway is currently under interaction
		if (pathwayUnderInteractionLayer.getElementList().size() == 0)
			return;
		
		int iPathwayId = pathwayUnderInteractionLayer.getElementIdByPositionIndex(0);
		
		// If pathway is not visible then render nothing
		if (!pathwayUnderInteractionLayer.getElementVisibilityById(iPathwayId))
			return;
		
		gl.glPushMatrix();
		
		Transform transform = pathwayUnderInteractionLayer.getTransformByElementId(iPathwayId);
		Vec3f translation = transform.getTranslation();
		gl.glTranslatef(translation.x(),
				translation.y(),
				translation.z());
		
		Vec3f scale = transform.getScale();
		gl.glScalef(scale.x(), scale.y(), scale.z());
		
		Rotf rot = transform.getRotation();
		gl.glRotatef(Vec3f.convertRadiant2Grad(rot.getAngle()),
				rot.getX(),
				rot.getY(),
				rot.getZ());		

		refPathwayManager.renderPathway(gl, iPathwayId, true);
		refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, true);
		
		gl.glPopMatrix();
	}
	
	private void renderPathwayLayered(final GL gl) {
		
		LinkedList<Integer> pathwayElementList = pathwayLayeredLayer.getElementList();
		for (int iPathwayIndex = 0; iPathwayIndex < pathwayElementList.size(); iPathwayIndex++)
		{
			int iPathwayId = pathwayElementList.get(iPathwayIndex);
			
			// If pathway is not visible then render nothing
			if (!pathwayLayeredLayer.getElementVisibilityById(iPathwayId))
				continue;
			
			gl.glPushMatrix();
			
			Transform transform = pathwayLayeredLayer.getTransformByElementId(iPathwayId);
			Vec3f translation = transform.getTranslation();
			
			// Pathway texture height is subtracted from Y to align pathways to front level
			gl.glTranslatef(translation.x(),
					translation.y(),
					translation.z());
			
			//gl.glTranslatef(0f, GLPathwayManager.SCALING_FACTOR_Y * 1000 -GLPathwayManager.SCALING_FACTOR_Y * refPathwayTextureManager.loadPathwayTextureById(iPathwayId).getImageHeight(), 0f);
			
			Rotf rot = transform.getRotation();
			gl.glRotatef(Vec3f.convertRadiant2Grad(rot.getAngle()),
					rot.getX(),
					rot.getY(),
					rot.getZ());
			
			Vec3f scale = transform.getScale();
			gl.glScalef(scale.x(), scale.y(), scale.z());

			refPathwayManager.renderPathway(gl, iPathwayId, false);
			
//          gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
//          gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			
			if (bShowPathwayTexture)
			{	
				if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
						&& pathwayUnderInteractionLayer.getElementIdByPositionIndex(0) == iPathwayId)
					refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, true);
				else
					refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, false);
			}
			
			gl.glPopMatrix();
		}
	}

	private void renderPathwayPool(final GL gl) {
		
		// Initialize magnification factors with 0 (minimized)
		ArrayList<Integer> alMagnificationFactor = new ArrayList<Integer>();
		for (int i = 0; i < alSetData.get(0).getStorageByDimAndIndex(0, 0).getSize(StorageType.INT); i++)
		{
			alMagnificationFactor.add(0);			
		}
			
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		int iPathwayId = 0;		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{	
			iPathwayId = iArPathwayIDs[iPathwayIndex];	

			if (iMouseOverPickedPathwayId == iPathwayId)
			{	
				if ((iPathwayIndex-2 >= 0) && (alMagnificationFactor.get(iPathwayIndex-2) < 1))
					alMagnificationFactor.set(iPathwayIndex-2, 1);

				if ((iPathwayIndex-1 >= 0) && (alMagnificationFactor.get(iPathwayIndex-1) < 2))
					alMagnificationFactor.set(iPathwayIndex-1, 2);
				
				alMagnificationFactor.set(iPathwayIndex, 3);
				
				if ((iPathwayIndex+1 < alMagnificationFactor.size()) && (alMagnificationFactor.get(iPathwayIndex+1) < 2))
					alMagnificationFactor.set(iPathwayIndex+1, 2);
				
				if ((iPathwayIndex+2 < alMagnificationFactor.size()) && (alMagnificationFactor.get(iPathwayIndex+2) < 1))
					alMagnificationFactor.set(iPathwayIndex+2, 1);
			}	
			else if (pathwayLayeredLayer.containsElement(iPathwayId))
			{
				 alMagnificationFactor.set(iPathwayIndex, 2);
			}
		}

		recalculatePathwayPoolTransformation(alMagnificationFactor);
		
		float fYPos = 0;
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			gl.glPushMatrix();
			
			iPathwayId = iArPathwayIDs[iPathwayIndex];		
			
			gl.glLoadName(iPathwayIndex+1);

			if (!refHashPoolLinePickId2PathwayId.containsKey(iPathwayIndex+1))
				refHashPoolLinePickId2PathwayId.put(iPathwayIndex+1, iPathwayId);
			
			Transform transform = pathwayPoolLayer.getTransformByElementId(iPathwayId);
			Vec3f translation = transform.getTranslation();
			gl.glTranslatef(translation.x(),
					translation.y(),
					translation.z());
			
			gl.glColor4f(0, 0, 0, 1);
			
			if (alMagnificationFactor.get(iPathwayIndex) == 3)
			{
				GLTextUtils.renderText(gl,
						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
						18,
						0, -0.06f, 1);
				fYPos = -0.15f;
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 2)
			{
				GLTextUtils.renderText(gl,
						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
						12,
						0, -0.04f, 1);
				fYPos = -0.1f;
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 1)
			{
				GLTextUtils.renderText(gl,
						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
						10,
						0, -0.02f, 1);
				fYPos = -0.07f;			
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 0)
			{
				fYPos = -0.02f;
				
		        gl.glColor3f(0, 0, 0);

				gl.glBegin(GL.GL_QUADS);
		        gl.glVertex3f(0, 0, 0);		
		        gl.glVertex3f(0, fYPos, 0);			
		        gl.glVertex3f(0.1f, fYPos, 0);
		        gl.glVertex3f(0.1f, 0, 0);
		        gl.glEnd();
			}

			gl.glColor4f(0, 0, 0, 0);			

			gl.glBegin(GL.GL_QUADS);
	        gl.glVertex3f(0, 0, 0.1f);		
	        gl.glVertex3f(0, fYPos, 0.1f);			
	        gl.glVertex3f(0.5f, fYPos, 0.1f);
	        gl.glVertex3f(0.5f, 0, 0.1f);
	        gl.glEnd();
	        
			gl.glPopMatrix();
		}
	}
	
	private void recalculatePathwayPoolTransformation(
			final ArrayList<Integer> alMagnificationFactor) {
		
		Transform transform; 
		float fPathwayPoolHeight = -2.0f;
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		for (int iLineIndex = 0; iLineIndex < tmpStorage.getSize(StorageType.INT); iLineIndex++)
		{
			transform = pathwayPoolLayer.getTransformByPositionIndex(iLineIndex);
			
			if (alMagnificationFactor.get(iLineIndex) == 3)
			{
				fPathwayPoolHeight += 0.15;
			}
			else if (alMagnificationFactor.get(iLineIndex) == 2)
			{
				fPathwayPoolHeight += 0.1;
			}
			else if (alMagnificationFactor.get(iLineIndex) == 1)
			{
				fPathwayPoolHeight += 0.07;
			}
			else if (alMagnificationFactor.get(iLineIndex) == 0)
			{
				fPathwayPoolHeight += 0.02;
			}
			
			transform.setTranslation(new Vec3f(-4.0f, fPathwayPoolHeight, -7));
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": updateReceiver(): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;
		
		int[] tmp = refSetSelection.getOptionalDataArray();
		if (tmp.length == 0)
			return;
		
		loadPathwayToUnderInteractionPosition(
				refSetSelection.getOptionalDataArray()[0]);
	}
	
	public void updateReceiver(Object eventTrigger) {

	}
	
	private void doSlerpActions(final GL gl) {
		
//		for (int iSlerpIndex = 0; iSlerpIndex < arSlerpActions.size(); iSlerpIndex++) 
//		{
		if (!arSlerpActions.isEmpty())
		{
			slerpPathway(gl, arSlerpActions.get(0));
		}
		else if (arSlerpActions.isEmpty() && bSelectionChanged)
		{
			highlightIdenticalNodes(gl, selectedVertex);
			//selectedVertex = null;
			bSelectionChanged = false;
			
			// Update display list if something changed
			// Rebuild display lists for visible pathways in layered view
			Iterator<Integer> iterVisiblePathway = 
				pathwayLayeredLayer.getElementList().iterator();		
			
			while (iterVisiblePathway.hasNext())
			{
				refPathwayManager.buildPathwayDisplayList(gl, iterVisiblePathway.next());
			}
			
			// Rebuild display lists for visible pathways in focus position
			refPathwayManager.buildPathwayDisplayList(gl, pathwayUnderInteractionLayer
				.getElementIdByPositionIndex(0));
		}
		
		if (iSlerpFactor < 1000)
		{
			iSlerpFactor += 15;
		}
	}
	
	private void slerpPathway(final GL gl,
		SlerpAction slerpAction) {
		
		int iPathwayId = slerpAction.getElementId();
		Slerp slerp = new Slerp();
		Transform transform = slerp.interpolate(slerpAction.getOriginHierarchyLayer().
				getTransformByPositionIndex(slerpAction.getOriginPosIndex()), 
				slerpAction.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()), iSlerpFactor / 1000f);
		
		gl.glPushMatrix();
		slerp.applySlerp(gl, transform);
		
//		// Render labels only in pathway under interaction layer (in focus)
//		if (iSlerpFactor >= 1000 && slerpAction.getDestinationHierarchyLayer().equals(pathwayUnderInteractionLayer))
//			refPathwayManager.renderPathway(gl, iPathwayId, true);
//		else
			refPathwayManager.renderPathway(gl, iPathwayId, false);
		
		// Disable pathway highlighting for slerping back pathways.
//		if (slerpAction.isReversSlerp())
//			refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, false);
//		else
			refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, true);			
			
		gl.glPopMatrix();
		
		if (iSlerpFactor < 1000)
		{
			//iSlerpFactor += 10;
		}
//		else if(slerpAction.isReversSlerp())
//		{
//			arSlerpActions.remove(slerpAction);
//			slerpAction = null;
//			//return;
//			iSlerpFactor = 0;
//		}
		else
		{
			slerpAction.getDestinationHierarchyLayer()
				.setElementVisibilityById(true, iPathwayId);

			arSlerpActions.remove(slerpAction);
			iSlerpFactor = 0;
		}
		
		if ((iSlerpFactor == 0))
			slerp.playSlerpSound();
	}
	
    private void handlePicking(final GL gl) {
    	
    	Point pickPoint = null;
    	
    	if (pickingTriggerMouseAdapter.wasMousePressed())
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
	    else if (bIsMouseOverPickingEvent == true && 
	    		System.nanoTime() - fLastMouseMovedTimeStamp >= 0.0)// * 1e9)
	    {
	    	pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
	    	fLastMouseMovedTimeStamp = System.nanoTime();
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
		
		gl.glPushName(0);
		
		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 1.0, viewport, 0); // pick width and height is set to 5 (i.e. picking tolerance)
		
		float h = (float) (float) (viewport[3]-viewport[1]) / 
			(float) (viewport[2]-viewport[0]) * 4.0f;

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -h, h, 1.0f, 60.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point)pickPoint.clone();
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
	
	protected void processHits(final GL gl,
			int iHitCount, 
			int iArPickingBuffer[],
			final Point pickPoint) {

		//System.out.println("Number of hits: " +iHitCount);
		IPathwayVertexRep refPickedVertexRep;

		int iPtr = 0;
		int i = 0;

		int iPickedObjectId = 0;
		
		//System.out.println("------------------------------------------");
		
//		for (i = 0; i < iHitCount; i++)
//		{
			iPtr++;
			iPtr++;
			iPtr++;	
			//iPtr++;
			iPickedObjectId = iArPickingBuffer[iPtr];
			
			if (iPickedObjectId == 0)
			{
				// Remove pathway pool fisheye
	    		iMouseOverPickedPathwayId = -1;
				return;
			}
			
			// Do not handle picking if a slerp action is in progress
			if (!arSlerpActions.isEmpty())
				return;
			
			//System.out.println("Pick ID: "+iPickedObjectId);
			
			// Check if picked object a non-pathway object (like pathway pool lines, navigation handles, etc.)
			if (iPickedObjectId < MAX_LOADED_PATHWAYS)
			{
				int iPathwayId = refHashPoolLinePickId2PathwayId.get(iPickedObjectId);
				System.out.println("PathwayID: " +iPathwayId);
				
				// If mouse over event - just highlight pathway line
				if (bIsMouseOverPickingEvent)
				{
					// Check if mouse moved to another pathway in the pathway pool list
					if (iMouseOverPickedPathwayId != iPathwayId)
					{
						iMouseOverPickedPathwayId = iPathwayId;
						playPathwayPoolTickSound();						
					}
					
					return;
				}
				
				loadPathwayToUnderInteractionPosition(iPathwayId);
				
				return;
			}
			else if (iPickedObjectId == MAX_LOADED_PATHWAYS) // Picked object is just a pathway texture -> do nothing
			{
				return;
			}
			
			refPickedVertexRep = refPathwayManager.getVertexRepByPickID(iPickedObjectId);
			
			loadNodeInformationInBrowser(refPickedVertexRep.getVertex().getVertexLink());
			
			// Remove pathway pool fisheye
    		iMouseOverPickedPathwayId = -1;
			
			if (refPickedVertexRep == null)
				return;
			
			// Reset pick point 
			infoAreaRenderer.setPoint(null);
									
			System.out.println("Picked node:" +refPickedVertexRep.getName());
						
			infoAreaRenderer.setPoint(pickPoint);
			
			// If event is just mouse over (and not real picking) 
			// highlight the object under the cursor
			if (bIsMouseOverPickingEvent)
			{
				if (selectedVertex != null && !selectedVertex.equals(refPickedVertexRep.getVertex()))
					infoAreaRenderer.resetAnimation();
				
				selectedVertex = refPickedVertexRep.getVertex();
				bSelectionChanged = true;
				return;
			}
			
			if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
			{					
				String strTmp = "";
				strTmp = refPickedVertexRep.getVertex().getElementTitle();
				
				int iPathwayId = -1;
				try {
					iPathwayId = Integer.parseInt(strTmp.substring(strTmp.length()-4));					
				}catch (NumberFormatException e) {
					return;
				}
				
				loadPathwayToUnderInteractionPosition(iPathwayId);
				
				return;
			}
			else if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.enzyme))
			{
				selectedVertex = refPickedVertexRep.getVertex();			
				bSelectionChanged = true;
				loadDependentPathwayContainingVertex(gl, refPickedVertexRep.getVertex());
			}
	//	}
	}	
	
	private void loadPathwayToUnderInteractionPosition(int iPathwayId) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": loadPathwayToUnderInteractionPosition(): Pathway with ID " +iPathwayId
				+ " is under interaction.",
				LoggerType.VERBOSE);
		
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < 1000)
			return;
		
		arSlerpActions.clear();
		
		// Check if selected pathway is loaded.
		if (!refGeneralManager.getSingelton().getPathwayManager().hasItem(iPathwayId))
			return;
		
		// Slerp current pathway back to layered view
		if (!pathwayUnderInteractionLayer.getElementList().isEmpty())
		{
			SlerpAction reverseSlerpAction = new SlerpAction(
					pathwayUnderInteractionLayer.getElementIdByPositionIndex(0),
					pathwayUnderInteractionLayer,
					true);
			
//			pathwayUnderInteractionLayer.removeElement(
//					pathwayUnderInteractionLayer.getElementIdByPositionIndex(0));
			arSlerpActions.add(reverseSlerpAction);
		}
		
		SlerpAction slerpAction;
		
//		// Prevent slerp action if pathway is already in layered view
//		if (pathwayLayeredLayer.containsElement(iPathwayId))
//			return;
		
		// Slerp to layered pathway view
		slerpAction = new SlerpAction(
				iPathwayId,
				pathwayPoolLayer,
				false);
		
		arSlerpActions.add(slerpAction);

		// Slerp from layered to under interaction position
		slerpAction = new SlerpAction(
				iPathwayId,
				pathwayLayeredLayer,
				false);
		
		arSlerpActions.add(slerpAction);
		iSlerpFactor = 0;
		
		// Trigger update with current pathway that dependent pathways 
		// know which pathway is currently under interaction
		int[] tmp = new int[1];
		tmp[0] = iPathwayId;
		alSetSelection.get(0).updateSelectionSet(iUniqueId,
				new int[0], new int[0], tmp);		
	}
	
    @SuppressWarnings("unchecked")
	protected void highlightIdenticalNodes(final GL gl,
    		final PathwayVertex refVertex) {
    	
    	// Remove previous selection
    	iAlSelectedElements.clear();
    	
		Pathway refTmpPathway = null;
		PathwayVertex refTmpVertex = null;
		Iterator<PathwayVertex> iterIdenticalVertices = null;
		int iPathwayId = 0;	
		
		LinkedList<Integer> tmpVisiblePathways = (LinkedList<Integer>) 
			pathwayLayeredLayer.getElementList().clone();
		
		// Add pathway under interaction to pathways in layered view
		tmpVisiblePathways.add(pathwayUnderInteractionLayer.getElementIdByPositionIndex(0));
		Iterator<Integer> iterVisiblePathway = tmpVisiblePathways.iterator();	

		while (iterVisiblePathway.hasNext())
		{
			iPathwayId = iterVisiblePathway.next();
			
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId);
		
			iterIdenticalVertices = refGeneralManager.getSingelton().
					getPathwayElementManager().getPathwayVertexListByName(
							refVertex.getElementTitle()).iterator();
			
			while(iterIdenticalVertices.hasNext())
			{
				refTmpVertex = iterIdenticalVertices.next();
				
				if (refTmpPathway.isVertexInPathway(refTmpVertex) == true)
				{	
					if (refTmpVertex != null)
					{		
						iAlSelectedElements.add(refTmpVertex.getElementId());
						System.out.println("Adding vertex " +refTmpVertex.getElementTitle() + " to selection.");
					}
				}
			}
	    }
		
//		int[] iArTmp = new int[iAlSelectedElements.size()];
//		for(int i = 0; i <  iArTmp.length; i++)
//		{
//			iArTmp[i] = ((Integer)iAlSelectedElements.get(i)).intValue();
//		}
//		alSetSelection.get(0).setSelectionIdArray(iArTmp);
    }
    
    public void loadDependentPathwayContainingVertex(final GL gl,
    		final PathwayVertex refVertex) {
    	
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		Iterator<PathwayVertex> iterIdenticalVertices = null;
		Pathway refTmpPathway = null;
		PathwayVertex refTmpVertex = null;
		int iPathwayId = 0;	
		int iMaxPathways = 4;
		int iMaxPathwayCount = 0;
		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			iPathwayId = iArPathwayIDs[iPathwayIndex];
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId);

			iterIdenticalVertices = refGeneralManager.getSingelton().
				getPathwayElementManager().getPathwayVertexListByName(
					refVertex.getElementTitle()).iterator();
			
			while(iterIdenticalVertices.hasNext())
			{
				refTmpVertex = iterIdenticalVertices.next();
				
				if (refTmpPathway.isVertexInPathway(refTmpVertex) == true)
				{	
					if (refTmpVertex != null)
					{												
						SlerpAction slerpAction;
						
						// Prevent slerp action if pathway is already in layered view
						if (!pathwayLayeredLayer.containsElement(iPathwayId))
						{

							if (iMaxPathwayCount >= iMaxPathways)
								return;
							
							iMaxPathwayCount++;
							
							// Slerp to layered pathway view
							slerpAction = new SlerpAction(
									iPathwayId,
									pathwayPoolLayer,
									false);
							
							arSlerpActions.add(slerpAction);				
							iSlerpFactor = 0;
						}
					}
				}
			}	
		}
	}
    
	private void playPathwayPoolTickSound() {
		
		try{
            AudioInputStream audioInputStream = 
            	AudioSystem.getAudioInputStream(new File("data/sounds/tick.wav"));
            AudioFormat af     = audioInputStream.getFormat();
            int size      = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio       = new byte[size];
            DataLine.Info info      = new DataLine.Info(Clip.class, af, size);
            audioInputStream.read(audio, 0, size);
            
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, size);
            clip.start();

		}catch(Exception e){ e.printStackTrace(); }
	}
	
	public void loadNodeInformationInBrowser(String sUrl) {

		if (sUrl.isEmpty())
			return;
		
		CmdViewLoadURLInHTMLBrowser createdCmd = 
			(CmdViewLoadURLInHTMLBrowser)refGeneralManager.getSingelton().getCommandManager().
				createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}
	
	/**
	 * @param textureTransparency the fTextureTransparency to set
	 */
	public final void setTextureTransparency(float textureTransparency) {
	
		if (( textureTransparency >= 0.0f)&&( textureTransparency<= 1.0f)) {
			fTextureTransparency = textureTransparency;
			return;
		}
		
		refGeneralManager.getSingelton().logMsg("setTextureTransparency() failed! value=" +
				textureTransparency +
				" was out of range [0.0f .. 1.0f]",
				LoggerType.MINOR_ERROR);
	}
}