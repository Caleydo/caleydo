package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Rotf;
import gleem.linalg.Transform;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
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
	
	private float fTextureTransparency = 1.0f; 
	
	private float fLastMouseMovedTimeStamp = 0;
	
	private boolean bIsMouseOverPickingEvent = false;
	
	private boolean bShowPathwayTexture = true;
	
	private int iMouseOverPickedPathwayId = -1;
	
	private GLPathwayManager refPathwayManager;
	
	private GLPathwayTextureManager refPathwayTextureManager;
		
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	private ArrayList<SlerpAction> arSlerpActions;
		
	/**
	 * Slerp factor 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	private HashMap<Integer, Integer> refHashPoolLinePickId2PathwayId;
	
	private JukeboxHierarchyLayer pathwayUnderInteractionLayer; // contains only one pathway
	private JukeboxHierarchyLayer pathwayLayeredLayer;
	private JukeboxHierarchyLayer pathwayPoolLayer;
	
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
		
		refHashPoolLinePickId2PathwayId = new HashMap<Integer, Integer>();
		
		// Create Jukebox hierarchy
		pathwayUnderInteractionLayer = new JukeboxHierarchyLayer();
		pathwayLayeredLayer = new JukeboxHierarchyLayer();
		pathwayPoolLayer = new JukeboxHierarchyLayer();
		pathwayUnderInteractionLayer.setParentLayer(pathwayLayeredLayer);
		pathwayLayeredLayer.setChildLayer(pathwayUnderInteractionLayer);
		pathwayLayeredLayer.setParentLayer(pathwayPoolLayer);
		pathwayPoolLayer.setChildLayer(pathwayLayeredLayer);
		
		Transform transformPathwayUnderInteraction = new Transform();
		transformPathwayUnderInteraction.setTranslation(new Vec3f(-0.95f, -2f, 0f));
		transformPathwayUnderInteraction.setScale(new Vec3f(1.5f, 1.5f, 1.5f));
		transformPathwayUnderInteraction.setRotation(new Rotf(0, 0, 0, 0));
		pathwayUnderInteractionLayer.setTransformByPositionIndex(0, transformPathwayUnderInteraction);
		//pathwayUnderInteractionLayer.addElement(0);
		
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) 
			openGLCanvasDirector.getJoglCanvasForwarder().getJoglMouseListener();
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

		gl.glDepthFunc(GL.GL_LEQUAL);
		//gl.glEnable(GL.GL_LINE_SMOOTH);
		//gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		//gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
		initPathwayData(gl);
		
		setInitGLDone();
	}	
	
	protected void initPathwayData(final GL gl) {
	
		refPathwayManager.init(gl, alSetData);
		buildPathwayPool(gl);
		buildLayeredPathways(gl);
	}

	
	public void renderPart(GL gl) {
		
		handlePicking(gl);		
		renderScene(gl);
	}
	
	public void renderScene(final GL gl) {
		
		renderPathwayPool(gl);
		renderPathwayLayered(gl);

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
		
		float fLineHeight = 0.05f;
		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		int iMaxLines = 10;
		
		// Create free pathway spots
		Transform transform; 
		for (int iLineIndex = 0; iLineIndex < iMaxLines; iLineIndex++)
		{
			transform = new Transform();
			transform.setRotation(new Rotf(fTiltAngleRad, -1, 0, 0));
			transform.setTranslation(new Vec3f(-3.95f, -iLineIndex * fLineHeight, 0f));
			transform.setScale(new Vec3f(0.1f,0.1f,0.1f));	
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
			refPathwayManager.buildPathwayDisplayList(gl, iPathwayId);
			refPathwayTextureManager.loadPathwayTexture(iPathwayId);
			pathwayPoolLayer.addElement(iPathwayId);		
		}
	}
	
	private void renderPathwayLayered(final GL gl) {
		
		LinkedList<Integer> pathwayElementList = pathwayLayeredLayer.getElementList();
		for (int iPathwayIndex = 0; iPathwayIndex < pathwayElementList.size(); iPathwayIndex++)
		{
			int iPathwayId = pathwayElementList.get(iPathwayIndex);
			
			gl.glPushMatrix();
			
			Transform transform = pathwayLayeredLayer.getTransformByElementId(iPathwayId);
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

			refPathwayManager.renderPathway(gl, iPathwayId, false);
			
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

			if (iMouseOverPickedPathwayId == iPathwayId || pathwayUnderInteractionLayer.containsElement(iPathwayId))
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
		}

		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			gl.glPushMatrix();
			
			iPathwayId = iArPathwayIDs[iPathwayIndex];		
			
			Transform transform = pathwayPoolLayer.getTransformByElementId(iPathwayId);
			Vec3f translation = transform.getTranslation();
			gl.glTranslatef(translation.x(),
					translation.y(),
					translation.z());
			
			gl.glLoadName(iPathwayIndex);
			
			if (!refHashPoolLinePickId2PathwayId.containsKey(iPathwayIndex))
				refHashPoolLinePickId2PathwayId.put(iPathwayIndex, iPathwayId);
						
			if (alMagnificationFactor.get(iPathwayIndex) == 3)
			{
				gl.glColor3f(0 ,0 ,0);
				GLTextUtils.renderText(gl,
						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
						18,
						0, 0.025f, 0);
				gl.glColor4f(0, 0, 0, 0);				
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 2)
			{
				gl.glColor3f(0 ,0 ,0);
				GLTextUtils.renderText(gl,
						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
						10,
						0, 0.025f, 0);
				gl.glColor4f(0, 0, 0, 0);				
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 1)
			{
				gl.glScaled(0.5f, 0.5f, 0.5f);
				gl.glColor3f(0, 1, 0);
			}
			else if (alMagnificationFactor.get(iPathwayIndex) == 0)
			{
				gl.glScaled(0.2f, 0.2f, 0.2f);
				gl.glColor3f(0, 0, 1);
			}
			
			//			// Highlight pathway under interaction
//			else if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
//					&& pathwayUnderInteractionLayer.getElementIdByPositionIndex(0) == iPathwayId)
//			{
//				gl.glColor3f(1, 0, 0);
//			}
			
			gl.glBegin(GL.GL_QUADS);
	        gl.glVertex3f(0, 0, 0);		
	        gl.glVertex3f(0, 0.03f, 0);			
	        gl.glVertex3f(0.5f, 0.03f, 0f);
	        gl.glVertex3f(0.5f, 0, 0f);
	        gl.glEnd();

			gl.glPopMatrix();
		}
	}
	
//	private void renderPathwayPool(final GL gl) {
//		
//		gl.glColor3f(0, 0, 0);
//		//gl.glPushName(0);
//		
//		// Load pathway storage
//		// Assumes that the set consists of only one storage
//		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
//		int[] iArPathwayIDs = tmpStorage.getArrayInt();
//				
//		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
//			iPathwayIndex++)
//		{
//			gl.glPushMatrix();
//			
//			int iPathwayId = iArPathwayIDs[iPathwayIndex];		
//			
//			Transform transform = pathwayPoolLayer.getTransformByElementId(iPathwayId);
//			Vec3f translation = transform.getTranslation();
//			gl.glTranslatef(translation.x(),
//					translation.y(),
//					translation.z());
//			
//			gl.glLoadName(iPathwayIndex);
//			
//			if (!refHashPoolLinePickId2PathwayId.containsKey(iPathwayIndex))
//				refHashPoolLinePickId2PathwayId.put(iPathwayIndex, iPathwayId);
//						
//			if (iMouseOverPickedPathwayId == iPathwayId)
//			{
//				GLTextUtils.renderText(gl, 
//						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
//						0, 0.025f, 0);
//				gl.glColor4f(0, 0, 0, 0);				
//			}
//			// Highlight pathway under interaction
//			else if (!pathwayUnderInteractionLayer.getElementList().isEmpty() 
//					&& pathwayUnderInteractionLayer.getElementIdByPositionIndex(0) == iPathwayId)
//			{
//				gl.glColor3f(1, 0, 0);
////				GLTextUtils.renderText(gl, 
////						((Pathway)refGeneralManager.getSingelton().getPathwayManager().getItem(iPathwayId)).getTitle(), 
////						0, 0.025f, 0);
////				gl.glColor4f(0, 0, 0, 0);
//			}
//			else
//			{
//				gl.glColor3f(0, 0, 0);
//			}			
//			
//			gl.glBegin(GL.GL_QUADS);
//	        gl.glVertex3f(0, 0, 0);		
//	        gl.glVertex3f(0, 0.03f, 0);			
//	        gl.glVertex3f(0.5f, 0.03f, 0f);
//	        gl.glVertex3f(0.5f, 0, 0f);
//	        gl.glEnd();
//
//			gl.glPopMatrix();
//		}
//	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		
	}
	
	public void updateReceiver(Object eventTrigger) {

	}
	
	public void updateSelectionSet(int[] iArSelectionVertexId,
			int[] iArSelectionGroup,
			int[] iArNeighborVertices) {
	}
	
	private void doSlerpActions(final GL gl) {
		
		for (int iSlerpIndex = 0; iSlerpIndex < arSlerpActions.size(); iSlerpIndex++) 
		{
			slerpPathway(gl, arSlerpActions.get(iSlerpIndex));
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
		Transform transform = slerp.interpolate(slerpAction.getOriginHierarchyLayer().getTransformByPositionIndex(slerpAction.getOriginPosIndex()), 
				slerpAction.getDestinationHierarchyLayer().getTransformByPositionIndex(slerpAction.getDestinationPosIndex()), iSlerpFactor / 1000f);
		
		if (iSlerpFactor == 0)
			slerp.playSlerpSound();
		
		gl.glPushMatrix();
		slerp.applySlerp(gl, transform);
		
		// Render labels only in pathway under interaction layer (in focus)
		if (iSlerpFactor >= 1000 && slerpAction.getDestinationHierarchyLayer().equals(pathwayUnderInteractionLayer))
			refPathwayManager.renderPathway(gl, iPathwayId, true);
		else
			refPathwayManager.renderPathway(gl, iPathwayId, false);
		
		// Disable pathway highlighting for slerping back pathways.
		if (slerpAction.isReversSlerp())
			refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, false);
		else
			refPathwayTextureManager.renderPathway(gl, iPathwayId, fTextureTransparency, true);			
			
		gl.glPopMatrix();
		
		if (iSlerpFactor < 1000)
		{
			//iSlerpFactor += 10;
		}
		else if(slerpAction.isReversSlerp())
		{
			slerpAction.setSlerpDone(true);
			arSlerpActions.remove(slerpAction);
			slerpAction = null;
		}
		else
		{
			slerpAction.setSlerpDone(true);
			
			// Slerp pathway from layered view to pathway under interaction position
			if (slerpAction.getOriginHierarchyLayer().equals(pathwayPoolLayer))
			{	
				// Slerp to pathway under interaction view
				SlerpAction slerpActionUnderInteraction = new SlerpAction(
					iPathwayId,
					pathwayLayeredLayer,
					false,
					false,
					0);
				arSlerpActions.add(slerpActionUnderInteraction);
				arSlerpActions.remove(slerpAction);
				slerpAction = null;
				iSlerpFactor = 0;
			}
		}
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
	    		System.nanoTime() - fLastMouseMovedTimeStamp >= 0.0 * 1e9)
	    {
	    	pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
	    	fLastMouseMovedTimeStamp = System.nanoTime();
	    }
	    
    	// Check if a object was picked
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

		// Reset picked point 
		pickPoint = null;
		
		renderScene(gl);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		processHits(gl, iHitCount, iArPickingBuffer);
	}
	
	protected void processHits(final GL gl,
			int iHitCount, 
			int iArPickingBuffer[]) {

		//System.out.println("Number of hits: " +iHitCount);
		IPathwayVertexRep refPickedVertexRep;

		int iPtr = 0;
		int i = 0;

		int iPickedObjectId = 0;
		
		//System.out.println("------------------------------------------");
		
		for (i = 0; i < iHitCount; i++)
		{
			iPtr++;
			iPtr++;
			iPtr++;	
			//iPtr++;
			iPickedObjectId = iArPickingBuffer[iPtr];
			
			System.out.println("Pick ID: "+iPickedObjectId);
			
			// Check if picked object a non-pathway object (like pathway pool lines, navigation handles, etc.)
			if (iPickedObjectId < 100)
			{
				int iPathwayId = refHashPoolLinePickId2PathwayId.get(iPickedObjectId);
				System.out.println("PathwayID: " +iPathwayId);
				
				// If mouse over event - just highlight pathway line
				if (bIsMouseOverPickingEvent)
				{
					iMouseOverPickedPathwayId = iPathwayId;
					return;
				}
				
				// Check if other slerp action is currently running
				if (iSlerpFactor > 0 && iSlerpFactor < 1000)
					return;
				
				arSlerpActions.clear();
				
				// Slerp current pathway back to layered view
				if (!pathwayUnderInteractionLayer.getElementList().isEmpty())
				{
					SlerpAction reverseSlerpAction = new SlerpAction(
							pathwayUnderInteractionLayer.getElementIdByPositionIndex(0),
							pathwayUnderInteractionLayer,
							true,
							true,
							0);
					
					arSlerpActions.add(reverseSlerpAction);
				}
				
				// Slerp to layered pathway view
				SlerpAction slerpAction = new SlerpAction(
						iPathwayId,
						pathwayPoolLayer,
						false,
						false,
						pathwayLayeredLayer.getElementList().size()); // append to the end
				
				arSlerpActions.add(slerpAction);
								
				iSlerpFactor = 0;
				return;
			}
			
			refPickedVertexRep = refPathwayManager.getVertexRepByPickID(iPickedObjectId);
			
			if (refPickedVertexRep == null)
				return;
			
			System.out.println("Picked node:" +refPickedVertexRep.getName());
			
			if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
			{
				// Check if other slerp action is currently running
				if (iSlerpFactor > 0 && iSlerpFactor < 1000)
					return;
				
				String strTmp = "";
				strTmp = refPickedVertexRep.getVertex().getElementTitle();
				
				arSlerpActions.clear();
				
				int iPathwayId = -1;
				try {
					iPathwayId = Integer.parseInt(strTmp.substring(strTmp.length()-4));					
				}catch (NumberFormatException e) {
					return;
				}
				
				// Check if selected pathway is loaded.
				if (!refGeneralManager.getSingelton().getPathwayManager().hasItem(iPathwayId))
					return;
					
				System.out.println("PathwayID: " +iPathwayId);
				
				// Slerp current pathway back to layered view
				if (!pathwayUnderInteractionLayer.getElementList().isEmpty())
				{
					SlerpAction reverseSlerpAction = new SlerpAction(
							pathwayUnderInteractionLayer.getElementIdByPositionIndex(0),
							pathwayUnderInteractionLayer,
							true,
							true,
							0);
					
					arSlerpActions.add(reverseSlerpAction);
				}
				
				SlerpAction slerpAction = null;
				if (pathwayLayeredLayer.containsElement(iPathwayId))
				{
					slerpAction = new SlerpAction(
							iPathwayId,
							pathwayLayeredLayer,
							false,
							false,
							0); 
				}
				else
				{
					slerpAction = new SlerpAction(
							iPathwayId,
							pathwayPoolLayer,
							false,
							false,
							pathwayLayeredLayer.getElementList().size()); // append to the end 
				}
				arSlerpActions.add(slerpAction);
				
				iSlerpFactor = 0;
			}
		}
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
