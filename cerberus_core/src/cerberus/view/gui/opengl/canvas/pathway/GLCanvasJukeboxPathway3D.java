package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Rotf;

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

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;


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
	
	protected float fZLayerValue = 0.0f;
	
	protected float fPathwayTextureAspectRatio = 1.0f;
	
	protected HashMap<Pathway, Float> refHashPathwayToZLayerValue;
	
	protected float fTextureTransparency = 1.0f; 
	
	/**
	 * Holds the pathways with the corresponding pathway textures.
	 */
	protected HashMap<Pathway, Texture> refHashPathwayToTexture;
	
	/**
	 * Pathway that is currently under user interaction in the 2D pathway view.2
	 */
	protected Pathway refPathwayUnderInteraction;
	
	protected boolean bShowPathwayTexture = true;
			
	protected HashMap<Pathway, FloatBuffer> refHashPathway2ModelMatrix;	
	
	protected float fSlerpFactor = 0f;
		
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
	
		refHashPathwayToZLayerValue = new HashMap<Pathway, Float>();
		refHashPathwayToTexture = new HashMap<Pathway, Texture>();
		refHashPathway2ModelMatrix = new HashMap<Pathway, FloatBuffer>();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas( GL gl ) {
		
		// Clearing window and set background to WHITE
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		//gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
		float[] fModelAmbient = {0.9f, 0.9f, 0.9f, 1.0f};
		
//		gl.glEnable(GL.GL_COLOR_MATERIAL);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
//		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
//		
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, fMatSpecular, 0);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, fMatShininess, 0);
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, fLightPosition, 0);		
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, fWhiteLight, 0);
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, fWhiteLight, 0);
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, fModelAmbient, 0);

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);		
		
//		bCanvasInitialized = true;
		
	    gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	    gl.glEnable(GL.GL_TEXTURE_2D);
		
		initPathwayData();
		
		setInitGLDone();
	}	
	
	protected void initPathwayData() {
	
		Pathway refTmpPathway = null;
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
				getItem(iArPathwayIDs[iPathwayIndex]);
			
			// Do not load texture if pathway texture was already loaded before.
			if (refHashPathwayToTexture.containsValue(refTmpPathway))
				break;
			
			if (bShowPathwayTexture)
				loadBackgroundOverlayImage(refTmpPathway);		
		}
	}

	
	public void renderPart(GL gl) {

		slerpTest(gl);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#loadBackgroundOverlayImage(Stringt)
	 */
	public void loadBackgroundOverlayImage(Pathway refTexturedPathway) {
		
		int iPathwayId = refTexturedPathway.getPathwayID();
		String sPathwayTexturePath = "";
		Texture refPathwayTexture;
		
		if (iPathwayId < 10)
		{
			sPathwayTexturePath = "map0000" + Integer.toString(iPathwayId);
		}
		else if (iPathwayId < 100 && iPathwayId >= 10)
		{
			sPathwayTexturePath = "map000" + Integer.toString(iPathwayId);
		}
		else if (iPathwayId < 1000 && iPathwayId >= 100)
		{
			sPathwayTexturePath = "map00" + Integer.toString(iPathwayId);
		}
		else if (iPathwayId < 10000 && iPathwayId >= 1000)
		{
			sPathwayTexturePath = "map0" + Integer.toString(iPathwayId);
		}
		
		sPathwayTexturePath = refGeneralManager.getSingelton().getPathwayManager().getPathwayImagePath()
			+ sPathwayTexturePath +".gif";	
		
		try
		{
			refPathwayTexture = TextureIO.newTexture(new File(sPathwayTexturePath), false);
//			refPathwayTexture.bind();
//			refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
//			refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			refHashPathwayToTexture.put(refTexturedPathway, refPathwayTexture);
			
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadBackgroundOverlay(): Loaded Texture for Pathway" +refTexturedPathway.getTitle(),
					LoggerType.VERBOSE );
			
		} catch (Exception e)
		{
			System.out.println("Error loading texture " + sPathwayTexturePath);
			e.printStackTrace();
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

	protected void slerpTest(final GL gl) {

		gl.glColor4f(0, 1, 0, 1);
		
		gl.glRotatef(57, 1, 0, 0);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0f, 0f, 0f);			  
		gl.glVertex3f(0f, 0.5f, 0f);			  
		gl.glVertex3f(0.5f, 0.5f, 0f);			  
		gl.glVertex3f(0.5f, 0f, 0f);			  
		gl.glEnd();	
		gl.glRotatef(-57, 1, 0, 0);
		
		Slerp slerp = new Slerp();
		Rotf quatResult = null;
		
		//slerp.setTranslationOrigin(1.2f, 1.2f, 0f);
		slerp.setTranslationDestination(-2, 2, 0);
		slerp.setScaleDestination(3, 3, 3);
		
		if (fSlerpFactor < 1)
		{
			fSlerpFactor += 0.002f;
		}
		else
			fSlerpFactor = 0f;
				
		quatResult = slerp.interpolate(new Rotf(1, 1, 0, 0), new Rotf(0, 0, 0, 0), 
				fSlerpFactor);
		
		gl.glColor4f(1, 0, 0, 1);
		gl.glRotatef(quatResult.getAngle() * 180 / (float)Math.PI, 
				quatResult.getX(), 
				quatResult.getY(), 
				quatResult.getZ());
		
		gl.glTranslatef(slerp.getTranslationResult().x(), 
				slerp.getTranslationResult().y(), 
				slerp.getTranslationResult().z());
				
		gl.glScalef(slerp.getScaleResult().x(), 
				slerp.getScaleResult().y(),
				slerp.getScaleResult().z());
		
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0f, 0f, 0f);			  
		gl.glVertex3f(0f, 0.5f, 0f);			  
		gl.glVertex3f(0.5f, 0.5f, 0f);			  
		gl.glVertex3f(0.5f, 0f, 0f);			  
		gl.glEnd();	
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
