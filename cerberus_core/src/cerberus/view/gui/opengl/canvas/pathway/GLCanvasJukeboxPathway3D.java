package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.media.opengl.GL;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.Pathway;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.util.slerp.Slerp;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;
import cerberus.util.opengl.GLTextUtils;

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
	
	protected float fTextureTransparency = 1.0f; 

	/**
	 * Pathway that is currently under user interaction in the 2D pathway view.2
	 */
	protected Pathway refPathwayUnderInteraction;
	
	protected boolean bShowPathwayTexture = true;
			
	protected HashMap<Integer, Mat4f> refHashPathwayIdToModelMatrix;	
	
	protected float fSlerpFactor = 0f;
	
	protected GLPathwayTextureManager refPathwayTextureManager;
		
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
		refPathwayTextureManager = new GLPathwayTextureManager(refGeneralManager);
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
	
//		float[] fMatSpecular = { 1.0f, 1.0f, 1.0f, 1.0f};
//		float[] fMatShininess = {25.0f}; 
		//float[] fLightPosition = {0.0f, 0.0f, 10.0f, 1.0f};
		//float[] fWhiteLight = {1.0f, 1.0f, 1.0f, 1.0f};
		//float[] fModelAmbient = {0.9f, 0.9f, 0.9f, 1.0f};
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
//		
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, fMatSpecular, 0);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, fMatShininess, 0);
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, fLightPosition, 0);		
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, fWhiteLight, 0);
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, fWhiteLight, 0);
		//gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, fModelAmbient, 0);

		//gl.glEnable(GL.GL_LIGHTING);
		//gl.glEnable(GL.GL_LIGHT0);		

	    //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	    //gl.glEnable(GL.GL_TEXTURE_2D);
		
		initPathwayData();
		
		setInitGLDone();
	}	
	
	protected void initPathwayData() {
	

	}

	
	public void renderPart(GL gl) {

		//slerpTest(gl);
		//renderPathwayList(gl);
		renderLayeredPathways(gl);

		slerpPathwayById(gl, 4310);
//		slerpPathwayById(gl, 4012);
	}
	
	public void renderLayeredPathways(final GL gl) {
		
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		
		float fLayerYPos = -1f;
		
		gl.glTranslatef(2.5f, fLayerYPos, 0f);
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
				
		for (int iPathwayIndex = 1; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
//			Pathway refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
//				getItem(iArPathwayIDs[iPathwayIndex]);
			
			if (bShowPathwayTexture)
			{				
				gl.glRotatef(fTiltAngleDegree, -1, 0, 0);
				gl.glScalef(0.7f, 0.7f, 1.0f);
				refPathwayTextureManager.renderPathway(gl,iArPathwayIDs[iPathwayIndex], fTextureTransparency);
				
				// Store current model-view matrix
				//FloatBuffer tmpMatrixBuffer = FloatBuffer.allocate(16);
				//gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, tmpMatrixBuffer);
				Mat4f refModelViewMatrix = new Mat4f(Mat4f.MAT4F_UNITY);
				//refModelViewMatrix.set(tmpMatrixBuffer.array());
				refModelViewMatrix.setRotation(new Rotf(fTiltAngleRad, -1, 0, 0));
				refModelViewMatrix.setTranslation(new Vec3f(2.5f, fLayerYPos, 0f));
				refModelViewMatrix.setScale(new Vec3f(0.7f, 0.7f, 0.7f));
				refHashPathwayIdToModelMatrix.put(iArPathwayIDs[iPathwayIndex], refModelViewMatrix);
				gl.glScalef(1/0.7f, 1/0.7f, 1.0f);
				gl.glRotatef(-fTiltAngleDegree, -1, 0, 0);
				gl.glTranslatef(0f, 1.5f, 0f);
				fLayerYPos += 1.5f;
			}
		}

		gl.glTranslatef(-2.5f, -3.5f, 0f);
	}

	public void renderPathwayList(final GL gl) {
		
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

//	protected void slerpTest(final GL gl) {
//
//		// Draw source objects
//		gl.glColor4f(0, 1, 0, 1);
//		
//		gl.glTranslatef(2.5f, 0f, 0f);
//		gl.glRotatef(57, -1, 0, 0);
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//		
//		gl.glRotatef(-57, -1, 0, 0);
//		gl.glTranslatef(0f, 1f, 0f);
//		gl.glRotatef(57, -1, 0, 0);
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//
//		gl.glRotatef(-57, -1, 0, 0);
//		gl.glTranslatef(0f, 1f, 0f);
//		gl.glRotatef(57, -1, 0, 0);
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//
//		gl.glRotatef(-57, -1, 0, 0);
//		gl.glTranslatef(0f, 1f, 0f);
//		gl.glRotatef(57, -1, 0, 0);
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//
//		gl.glRotatef(-57, -1, 0, 0);
//		gl.glTranslatef(-2.5f, -3f, 0f);
//
//		// Draw destination object
//		gl.glTranslatef(-2f, 0.5f, 0f);
//		gl.glScaled(3, 3, 3);
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//
//		gl.glScaled(1f/3f, 1f/3f, 1f/3f);
//		gl.glTranslatef(2f, -0.5f, 0f);
//		
//		Slerp slerp = new Slerp();
//		Rotf quatResult = null;
//		
//		slerp.setTranslationOrigin(2.5f, 1f, 0f);
//		slerp.setTranslationDestination(-2, 0.5f, 0f);
//		slerp.setScaleDestination(3, 3, 3);
//		
//		if (fSlerpFactor < 1)
//		{
//			fSlerpFactor += 0.002f;
//		}
//		else
//			fSlerpFactor = 0f;
//				
//		quatResult = slerp.interpolate(new Rotf(1, -1, 0, 0), new Rotf(0, 0, 0, 0), 
//				fSlerpFactor);
//		
//		gl.glColor4f(1, 0, 0, 1);
//		
//		gl.glTranslatef(slerp.getTranslationResult().x(), 
//				slerp.getTranslationResult().y(), 
//				slerp.getTranslationResult().z());
//		
//		gl.glRotatef(quatResult.getAngle() * 180 / (float)Math.PI, 
//				quatResult.getX(), 
//				quatResult.getY(), 
//				quatResult.getZ());
//				
//		gl.glScalef(slerp.getScaleResult().x(), 
//				slerp.getScaleResult().y(),
//				slerp.getScaleResult().z());
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glVertex3f(0f, 0f, 0f);			  
//		gl.glVertex3f(0f, 1f, 0f);			  
//		gl.glVertex3f(1f, 1f, 0f);			  
//		gl.glVertex3f(1f, 0f, 0f);			  
//		gl.glEnd();	
//	}
	
	public void slerpPathwayById(final GL gl, int iPathwayID) {
		
		if (refHashPathwayIdToModelMatrix.containsKey(iPathwayID))
		{
			Rotf quatOrigin = new Rotf();
			Rotf quatResult = new Rotf();
			Mat4f matOrigin = refHashPathwayIdToModelMatrix.get(iPathwayID);
			
			quatOrigin.fromMatrix(matOrigin);
			
			Slerp slerp = new Slerp();
			
			slerp.setTranslationOrigin(matOrigin.get(0,3), matOrigin.get(1,3), matOrigin.get(2,3));
			slerp.setTranslationDestination(-3, -1, 0f);
			slerp.setScaleOrigin(matOrigin.get(0,0), matOrigin.get(1, 1), matOrigin.get(2,2));
			slerp.setScaleDestination(1.7f, 1.7f, 1.7f);
			
			if (fSlerpFactor < 1)
			{
				fSlerpFactor += 0.001f;
			}
			else
			{
				fSlerpFactor = 0f;
			}
			
			quatResult = slerp.interpolate(quatOrigin,
					new Rotf(0, 0, 0, 0), 
					fSlerpFactor);
			
			//gl.glLoadIdentity();
			//gl.glTranslatef(0, 0, -8);	// why is this needed?
			
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
			
			refPathwayTextureManager.renderPathway(gl, iPathwayID, fTextureTransparency);
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
