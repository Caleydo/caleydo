package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import com.sun.opengl.util.BufferUtil;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.util.slerp.Slerp;
import cerberus.view.gui.jogl.PickingJoglMouseListener;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

/**
 * Jukebox setup that supports slerp animation.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 *
 */
public class GLCanvasJukeboxPathway3D
extends AGLCanvasUser_OriginRotation
implements IMediatorReceiver, IMediatorSender {	
	
	private float fTextureTransparency = 1.0f; 
				
	private int iSlerpFactor = 0;
	
	private float fLastMouseMovedTimeStamp = 0;
	
	private boolean bIsMouseOverPickingEvent = false;
	
	private GLPathwayManager refPathwayManager;
	
	private GLPathwayTextureManager refPathwayTextureManager;
	
	private boolean bShowPathwayTexture = true;
	
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	private HashMap<Integer, Mat4f> refHashPathwayIdToModelMatrix;	
	
	private ArrayList<Integer> iArPathwaysToSlerp;
	
	private int iPathwayUnderInteraction = -1;
		
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
	
		refHashPathwayIdToModelMatrix = new HashMap<Integer, Mat4f>();
		refPathwayManager = new GLPathwayManager(refGeneralManager);
		refPathwayTextureManager = new GLPathwayTextureManager(refGeneralManager);
		iArPathwaysToSlerp = new ArrayList<Integer>();

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
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
		initPathwayData(gl);
		
		setInitGLDone();
	}	
	
	protected void initPathwayData(final GL gl) {
	
		refPathwayManager.init(gl);
		buildPathways(gl);
	}

	
	public void renderPart(GL gl) {
		
		handlePicking(gl);		
		renderScene(gl);
	}
	
	public void renderScene(final GL gl) {
		
		//renderPathwayList(gl);
		renderLayeredPathways(gl);

		doSlerpActions(gl);
		//slerpPathwayById(gl, 4310);
		//slerpPathwayById(gl, 4012);
		//slerpPathwayById(gl, 4010);
	}
	
	private void buildPathways(final GL gl) {
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
				
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			int iPathwayID = iArPathwayIDs[iPathwayIndex];
			refPathwayManager.buildPathwayDisplayList(gl, iPathwayID);
		}
	}
	
	private void renderLayeredPathways(final GL gl) {
		
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		
		float fLayerYPos = -1f;
		
		gl.glPushMatrix();
		gl.glTranslatef(2.5f, fLayerYPos, 0f);
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
				
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			int iPathwayID = iArPathwayIDs[iPathwayIndex];
			
			if (bShowPathwayTexture)
			{				
				gl.glRotatef(fTiltAngleDegree, -1, -0.7f, 0);
				gl.glScalef(0.7f, 0.7f, 1.0f);
				
				refPathwayManager.renderPathway(gl, iPathwayID, false);
				
				if (iPathwayID == iPathwayUnderInteraction)
					refPathwayTextureManager.renderPathway(gl, iPathwayID, fTextureTransparency, true);
				else
					refPathwayTextureManager.renderPathway(gl, iPathwayID, fTextureTransparency, false);
				
				// Store current model-view matrix
				//FloatBuffer tmpMatrixBuffer = FloatBuffer.allocate(16);
				//gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, tmpMatrixBuffer);
				Mat4f refModelViewMatrix = new Mat4f(Mat4f.MAT4F_UNITY);
				//refModelViewMatrix.set(tmpMatrixBuffer.array());
				refModelViewMatrix.setRotation(new Rotf(fTiltAngleRad, -1, -0.7f, 0));
				//refModelViewMatrix.setRotation(new Rotf(0,0,0,0));
				refModelViewMatrix.setTranslation(new Vec3f(2.5f, fLayerYPos, 0f));
				refModelViewMatrix.setScale(new Vec3f(0.7f, 0.7f, 0.7f));
				//refModelViewMatrix.setScale(new Vec3f(1,1,1));
				refHashPathwayIdToModelMatrix.put(iPathwayID, refModelViewMatrix);
				gl.glScalef(1/0.7f, 1/0.7f, 1.0f);
				gl.glRotatef(-fTiltAngleDegree, -1, -0.7f, 0);
				gl.glTranslatef(0f, 1f, 0f);
				fLayerYPos += 1f;
			}
		}

		gl.glPopMatrix();
		//gl.glTranslatef(-2.5f, -3.0f, 0f);
	}

	private void renderPathwayList(final GL gl) {
		
		gl.glLineWidth(2);
		gl.glColor3f(1, 0, 0);
				
		for(int iLineIndex = 0; iLineIndex < 320; iLineIndex++) 
		{
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0.5f, 0, 0);
			gl.glEnd();
			
			gl.glTranslatef(0, -0.03f, 0);
			
			if (iLineIndex == 160)
				gl.glTranslatef(0.55f, 0.03f * iLineIndex, 0);
		}
	}
	
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
		
		if (iSlerpFactor < 1000)
		{
			// Slerp current pathway back to layered view.
			slerpPathwayById(gl, iPathwayUnderInteraction, false);
		}
		
		for (int iSlerpIndex = 0; iSlerpIndex < iArPathwaysToSlerp.size(); iSlerpIndex++) 
		{
			slerpPathwayById(gl, iArPathwaysToSlerp.get(iSlerpIndex), true);
		}
		
		// If slerp actions are done - update pathway under interaction variable. 
		if (iSlerpFactor >= 1000)
			iPathwayUnderInteraction = iArPathwaysToSlerp.get(0);
	}
	
	private void slerpPathwayById(final GL gl, int iPathwayID, boolean bSlerpDirection) {
		
		if (refHashPathwayIdToModelMatrix.containsKey(iPathwayID))
		{
			Slerp slerp = new Slerp();
			
			if (iSlerpFactor == 0)
				slerp.playSlerpSound();
			
			Rotf quatOrigin = new Rotf();
			Rotf quatResult = new Rotf();
			Mat4f matOrigin = refHashPathwayIdToModelMatrix.get(iPathwayID);
		
			quatOrigin.fromMatrix(matOrigin);
			
			slerp.setTranslationOrigin(matOrigin.get(0,3), matOrigin.get(1,3), matOrigin.get(2,3));
			slerp.setScaleOrigin(matOrigin.get(0,0), matOrigin.get(1, 1), matOrigin.get(2,2));
			slerp.setTranslationDestination(-3, -0.4f, 0f);
			slerp.setScaleDestination(1.6f, 1.6f, 1.6f);
			
			int iSlerpTmp = 0;
			if (bSlerpDirection == false)
				iSlerpTmp = 1000 - iSlerpFactor;
			else 
				iSlerpTmp = iSlerpFactor;
			
			quatResult = slerp.interpolate(quatOrigin,
					new Rotf(0, 0, 0, 0), 
					(float)iSlerpTmp / 1000.0f);
			
			//gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(slerp.getTranslationResult().x(), 
					slerp.getTranslationResult().y(), 
					slerp.getTranslationResult().z());
			
			gl.glRotatef(Vec3f.convertRadiant2Grad(quatResult.getAngle()), 
					quatResult.getX(), 
					quatResult.getY(), 
					quatResult.getZ());
					
			gl.glScalef(slerp.getScaleResult().x(), 
					slerp.getScaleResult().y(),
					slerp.getScaleResult().z());
//			// Only render labels if pathway reached destination
//			boolean bRenderLabels = false;
//			boolean bPickingRendering = false;
//			
//			if (fSlerpFactor >= 1)
//			{
//				bRenderLabels = true;
//				bPickingRendering = true;
//			}
			
			// Render labels only in final position
			if (iSlerpFactor == 1000)
				refPathwayManager.renderPathway(gl, iPathwayID, true);
			else
				refPathwayManager.renderPathway(gl, iPathwayID, false);
			
			refPathwayTextureManager.renderPathway(gl, iPathwayID, fTextureTransparency, true);
			gl.glPopMatrix();
			
			if (iSlerpFactor < 1000)
			{
				iSlerpFactor += 10;
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
	    		System.nanoTime() - fLastMouseMovedTimeStamp >= 0.3 * 1e9)
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

		System.out.println("Number of hits: " +iHitCount);
		IPathwayVertexRep refPickedVertexRep;
		

		int iPtr = 0;
		int i = 0;

		int iPickedNodeDisplayListId = 0;
		
		System.out.println("------------------------------------------");
		
		for (i = 0; i < iHitCount; i++)
		{
			iPtr++;
			iPtr++;
			iPtr++;	
			iPtr++;
			iPickedNodeDisplayListId = iArPickingBuffer[iPtr];
			
			refPickedVertexRep = refPathwayManager.getVertexRepByPickID(iPickedNodeDisplayListId);
			
			if (refPickedVertexRep == null)
				return;
			
			System.out.println("Picked node:" +refPickedVertexRep.getName());
			
			if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
			{
				String strTmp = "";
				strTmp = refPickedVertexRep.getVertex().getElementTitle();
				int iPathwayID = Integer.parseInt(strTmp.substring(strTmp.length()-4));
				System.out.println("PathwayID: " +iPathwayID);
				iArPathwaysToSlerp.clear();
				iArPathwaysToSlerp.add(iPathwayID);
				iSlerpFactor = 0;
			}
		}
		//fillInfoAreaContent(refPickedVertexRep);
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
