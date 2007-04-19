package cerberus.view.gui.opengl.canvas.pathway;

import java.awt.Color;

import java.awt.Point;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.selection.ISetSelection;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.data.IPathwayElementManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.util.colormapping.ColorMapping;
import cerberus.util.colormapping.EnzymeToExpressionColorMapper;
import cerberus.util.system.SystemTime;
import cerberus.view.gui.jogl.PickingJoglMouseListener;
import cerberus.view.gui.opengl.GLCanvasStatics;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;


/**
 * @author Marc Streit
 * @author Michael Kalkusch
 * 
 * @see cerberus.view.gui.opengl.IGLCanvasUser
 */
public abstract class AGLCanvasPathway3D
extends AGLCanvasUser_OriginRotation
implements IMediatorReceiver, IMediatorSender {
	
	protected float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	protected boolean bInitGLcanvawsWasCalled = false;
	
	protected static final int X = GLCanvasStatics.X;
	protected static final int Y = GLCanvasStatics.Y;
	private static final int Z = GLCanvasStatics.Z;
	protected static final int MIN = GLCanvasStatics.MIN;
	protected static final int MAX = GLCanvasStatics.MAX;
	private static final int OFFSET = GLCanvasStatics.OFFSET;
	
	protected static final float SCALING_FACTOR_X = 0.0025f;
	protected static final float SCALING_FACTOR_Y = 0.0025f;
	
	protected int iVertexRepIndex = 0;
	
	protected float fZLayerValue = 0.0f;
	
	//protected ArrayList<Integer> iArPathwayNodeDisplayListIDs;
	
	protected ArrayList<Integer> iArPathwayEdgeDisplayListIDs;
	
	protected int iEnzymeNodeDisplayListId = -1;
	
	protected int iHighlightedEnzymeNodeDisplayListId = -1;
	
	protected int iCompoundNodeDisplayListId = -1;
	
	protected int iHighlightedCompoundNodeDisplayListId = -1;
	
	protected float fPathwayNodeWidth = 0.0f;
	
	protected float fPathwayNodeHeight = 0.0f;
	
	protected float fCanvasXPos = 0.0f;
	
	protected float fCanvasYPos = 0.0f;
	
	protected float fPathwayTextureAspectRatio = 1.0f;

	protected ArrayList<Integer> iArSelectionStorageNeighborDistance;
	
	protected HashMap<Pathway, Float> refHashPathwayToZLayerValue;
	
	/**
	 * Holds the pathways with the corresponding pathway textures.
	 */
	protected HashMap<Pathway, Texture> refHashPathwayToTexture;
	
	/**
	 * Pathway that is currently under user interaction in the 2D pathway view.2
	 */
	protected Pathway refPathwayUnderInteraction;
	
	protected boolean bShowPathwayTexture = true;
		
	// Picking relevant variables
	
	protected static final int PICKING_BUFSIZE = 1024;
	
	protected Point pickPoint;
	
	protected int iUniqueObjectPickId = 0;
	
	protected HashMap<Integer, IPathwayVertexRep> refHashPickID2VertexRep;
		
	protected ArrayList<IPathwayVertexRep> iArHighlightedVertices;
	
	protected HashMap<Integer, Pathway> refHashDisplayListNodeId2Pathway;
	
	protected HashMap<Pathway, Integer> refHashPathway2DisplayListNodeId;
	
	protected boolean bBlowUp = true;
	
	protected float fHighlightedNodeBlowFactor = 1.0f;
	
	protected boolean bAcordionDirection = false;
	
	protected HashMap<Pathway, FloatBuffer> refHashPathway2ModelMatrix;
	
	protected boolean bSelectionDataChanged = false;
	
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	protected boolean bIsMouseOverPickingEvent = false;
	
	protected float fLastMouseMovedTimeStamp = 0;
	
	protected SystemTime systemTime = new SystemTime();
	
	protected ArrayList<String> refInfoAreaCaption;
	protected ArrayList<String> refInfoAreaContent;
	
//	protected ColorMapping expressionColorMapping;
	
	protected PathwayRenderStyle refRenderStyle;
		
	/**
	 * Constructor
	 * 
	 */
	public AGLCanvasPathway3D( final IGeneralManager refGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
				
		super(refGeneralManager, iViewId, iParentContainerId, "");
					
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) 
			openGLCanvasDirector.getJoglCanvasForwarder().getJoglMouseListener();
		//pickingTriggerMouseAdapter.setJoglMouseListener(this);
		pickingTriggerMouseAdapter.addMouseListenerAll(canvas);
//		
//		pickingTriggerMouseAdapter = new PickingJoglMouseListener(this);
//		
//		
		canvas.addMouseListener(pickingTriggerMouseAdapter);
		canvas.addMouseMotionListener(pickingTriggerMouseAdapter);
		
		fAspectRatio = new float [2][3];
		viewingFrame = new float [3][2];
	
		iArSelectionStorageNeighborDistance = new ArrayList<Integer>();
		//iArPathwayNodeDisplayListIDs = new ArrayList<Integer>();
		iArPathwayEdgeDisplayListIDs = new ArrayList<Integer>();
		iArHighlightedVertices = new ArrayList<IPathwayVertexRep>();

		refHashPathwayToZLayerValue = new HashMap<Pathway, Float>();
		refHashPathwayToTexture = new HashMap<Pathway, Texture>();
		refHashPickID2VertexRep = new HashMap<Integer, IPathwayVertexRep>();
		refHashDisplayListNodeId2Pathway = new HashMap<Integer, Pathway>();
		refHashPathway2DisplayListNodeId = new HashMap<Pathway, Integer>();
		refHashPathway2ModelMatrix = new HashMap<Pathway, FloatBuffer>();
		
		refInfoAreaCaption = new ArrayList<String>();
		refInfoAreaContent = new ArrayList<String>();
		
//		expressionColorMapping = new ColorMapping(0, 60000);
//		expressionColorMapping.createLookupTable();
		
		refRenderStyle = new PathwayRenderStyle();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas( GL gl ) {
	
		System.err.println("Init called from " +this.getClass().getSimpleName());
		
		//GL gl = canvas.getGL();
		
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
		buildPathwayDisplayList(gl);	
		
		setInitGLDone();
	}	
	
	/**
	 * Initializing the zLayer value for the layered view
	 * and loading the overlay texture for each pathway.
	 *
	 */
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
	
	protected abstract void buildPathwayDisplayList(final GL gl);

	protected void buildEnzymeNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getEnzymeNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getEnzymeNodeHeight() / 2.0f * SCALING_FACTOR_Y;
			
		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl);
        gl.glEndList();
	}
	
	protected void buildHighlightedEnzymeNodeDisplayList(final GL gl) {
		
//		if (iHighlightedEnzymeNodeDisplayListId == -1)
//		{
			// Creating display list for node cube objects
			iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);
//		}
		
		fPathwayNodeWidth = 
			refRenderStyle.getEnzymeNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getEnzymeNodeHeight() / 2.0f * SCALING_FACTOR_Y;
				
		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL.GL_COMPILE);
		gl.glScaled(fHighlightedNodeBlowFactor, 
				fHighlightedNodeBlowFactor, fHighlightedNodeBlowFactor);
		fillNodeDisplayList(gl);
		gl.glScaled(1.0f/fHighlightedNodeBlowFactor, 
				1.0f/fHighlightedNodeBlowFactor, 1.0f/fHighlightedNodeBlowFactor);  
        
        gl.glEndList();
	}
	
	protected void buildCompoundNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getCompoundNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getCompoundNodeHeight() / 2.0f * SCALING_FACTOR_Y;
		
		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl);
        gl.glEndList();
	}
	
	protected void buildHighlightedCompoundNodeDisplayList(final GL gl) {

		if (iHighlightedCompoundNodeDisplayListId == -1)
		{
			// Creating display list for node cube objects
			iHighlightedCompoundNodeDisplayListId = gl.glGenLists(1);
		}
		
		fPathwayNodeWidth = 
			refRenderStyle.getCompoundNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getCompoundNodeHeight() / 2.0f * SCALING_FACTOR_Y;
		
		gl.glNewList(iHighlightedCompoundNodeDisplayListId, GL.GL_COMPILE);
		gl.glScaled(fHighlightedNodeBlowFactor, 
				fHighlightedNodeBlowFactor, fHighlightedNodeBlowFactor);
		fillNodeDisplayList(gl);
		gl.glScaled(1.0f/fHighlightedNodeBlowFactor, 
				1.0f/fHighlightedNodeBlowFactor, 1.0f/fHighlightedNodeBlowFactor);
		
        gl.glEndList();
	}
	
	protected void fillNodeDisplayList(final GL gl) {
		
		gl.glBegin(GL.GL_QUADS);
		
        // FRONT FACE
		gl.glNormal3f( 0.0f, 0.0f, 1.0f);	
		// Top Right Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth , -fPathwayNodeHeight, 0.015f);		
        // Top Left Of The Quad (Front)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);			
        // Bottom Left Of The Quad (Front)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
		// Bottom Right Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);

        // BACK FACE
        gl.glNormal3f( 0.0f, 0.0f,-1.0f);
        // Bottom Left Of The Quad (Back)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Top Right Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			
        // Top Left Of The Quad (Back)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			

		// TOP FACE
        gl.glNormal3f( 0.0f, 1.0f, 0.0f);	
        // Top Right Of The Quad (Top)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Top Left Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Top)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);			

        // BOTTOM FACE
        gl.glNormal3f( 0.0f,-1.0f, 0.0f);	
        // Top Right Of The Quad (Bottom)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Top Left Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);
        // Bottom Right Of The Quad (Bottom)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			

        // RIGHT FACE
        gl.glNormal3f( 1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);
        // Top Left Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			
        
        // LEFT FACE
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);	
        // Top Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);	
        
        gl.glEnd();
	}
	
	protected void fillHighlightedNodeDisplayList(final GL gl) {
		
		int iGlowIterations = 15;
		
		for (int iGlowIndex = 0; iGlowIndex < iGlowIterations; iGlowIndex++)
		{		
			gl.glColor4f(1.0f, 1.0f, 0.0f, 0.3f / (float)iGlowIndex);
			gl.glScalef(1.0f + (float)iGlowIndex / 20.0f, 1.0f + (float)iGlowIndex / 20.0f, 
					1.0f + (float)iGlowIndex / 20.0f);
			gl.glBegin(GL.GL_QUADS);
			
	        // FRONT FACE
			gl.glNormal3f( 0.0f, 0.0f, 1.0f);	
			// Top Right Of The Quad (Front)
	        gl.glVertex3f(-fPathwayNodeWidth , -fPathwayNodeHeight, 0.015f);		
	        // Top Left Of The Quad (Front)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);			
	        // Bottom Left Of The Quad (Front)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
			// Bottom Right Of The Quad (Front)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
	
	        // BACK FACE
	        gl.glNormal3f( 0.0f, 0.0f,-1.0f);
	        // Bottom Left Of The Quad (Back)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
	        // Bottom Right Of The Quad (Back)
	        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
	        // Top Right Of The Quad (Back)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			
	        // Top Left Of The Quad (Back)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			
	
			// TOP FACE
	        gl.glNormal3f( 0.0f, 1.0f, 0.0f);	
	        // Top Right Of The Quad (Top)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
	        // Top Left Of The Quad (Top)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
	        // Bottom Left Of The Quad (Top)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
	        // Bottom Right Of The Quad (Top)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);			
	
	        // BOTTOM FACE
	        gl.glNormal3f( 0.0f,-1.0f, 0.0f);	
	        // Top Right Of The Quad (Bottom)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
	        // Top Left Of The Quad (Bottom)
	        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
	        // Bottom Left Of The Quad (Bottom)
	        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);
	        // Bottom Right Of The Quad (Bottom)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			
	
	        // RIGHT FACE
	        gl.glNormal3f( 1.0f, 0.0f, 0.0f);	
	        // Top Right Of The Quad (Right)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);
	        // Top Left Of The Quad (Right)
	        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
	        // Bottom Left Of The Quad (Right)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
	        // Bottom Right Of The Quad (Right)
	        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			
	        
	        // LEFT FACE
	        gl.glNormal3f(-1.0f, 0.0f, 0.0f);	
	        // Top Right Of The Quad (Left)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);	
	        // Top Left Of The Quad (Left)
	        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
	        // Bottom Left Of The Quad (Left)
	        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
	        // Bottom Right Of The Quad (Left)
	        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);	
	        
	        gl.glEnd();
	        
			gl.glScalef(1.0f / (1.0f + (float)iGlowIndex / 20.0f), 
					1.0f / (1.0f + (float)iGlowIndex / 20.0f), 
					1.0f / (1.0f + (float)iGlowIndex / 20.0f));
		}
	}

	protected abstract void renderPathway(final GL gl,
			final Pathway refTmpPathway, 
			int iDisplayListNodeId);
		
	public void createVertex(final GL gl, 
			IPathwayVertexRep vertexRep, 
			Pathway refContainingPathway) {
		
		boolean bHighlightVertex = false;
		Color tmpNodeColor = null;
		int iNeighborDistance = 0;
		
//		refGeneralManager.getSingelton().logMsg(
//				"OpenGL Pathway creating vertex for node " +vertexRep.getName(),
//				LoggerType.VERBOSE);
		
		fCanvasXPos = viewingFrame[X][MIN] + 
			(vertexRep.getXPosition() * SCALING_FACTOR_X);
		fCanvasYPos = viewingFrame[Y][MIN] + 
			(vertexRep.getYPosition() * SCALING_FACTOR_Y);
		
		fZLayerValue = refHashPathwayToZLayerValue.get(refContainingPathway);
		
		// Init picking for this vertex
//		if (bIsRefreshRendering == false)
//		{
			iUniqueObjectPickId++;
			gl.glPushName(iUniqueObjectPickId);
			//gl.glLoadName(iUniqueObjectPickId);
			refHashPickID2VertexRep.put(iUniqueObjectPickId, vertexRep);
//		}
		
		if (iArHighlightedVertices.contains(vertexRep))
		{
			iNeighborDistance = iArSelectionStorageNeighborDistance.get(
					iArHighlightedVertices.indexOf(vertexRep));
			
			bHighlightVertex = true;
			
			if (iNeighborDistance < PathwayRenderStyle.neighborhoodNodeColorArraysize) 
			{
				tmpNodeColor = refRenderStyle.getNeighborhoodNodeColorByDepth(iNeighborDistance);
			}
			else 
			{
				assert false : "can not find color for selection depth";
			}
			
			gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
					tmpNodeColor.getGreen() / 255.0f, 
					tmpNodeColor.getBlue() / 255.0f, 1.0f);
		}
		
		String sShapeType = vertexRep.getShapeType();

		gl.glTranslatef(fCanvasXPos, fCanvasYPos, fZLayerValue);

		// Pathway link
		if (sShapeType.equals("roundrectangle"))
		{				
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - fCanvasWidth + 0.02f, 
//					fCanvasYPos + 0.02f, 
//					-0.001f);

			if (bHighlightVertex == false)
			{
				tmpNodeColor = refRenderStyle.getPathwayNodeColor();
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
						tmpNodeColor.getGreen() / 255.0f, 
						tmpNodeColor.getBlue() / 255.0f, 1.0f);
			}
			
			fPathwayNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			fPathwayNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;

			fillNodeDisplayList(gl);
		}
		// Compounds
		else if (sShapeType.equals("circle"))
		{				
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - 0.04f, 
//					fCanvasYPos - fCanvasHeight, 
//					-0.001f);
			
			if (bHighlightVertex == true)
			{
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			
			}
			else 
			{
				tmpNodeColor = refRenderStyle.getCompoundNodeColor();
				gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
						tmpNodeColor.getGreen() / 255.0f, 
						tmpNodeColor.getBlue() / 255.0f, 1.0f);				//gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f); // green
				gl.glCallList(iCompoundNodeDisplayListId);
			}
		}	
		// Enzyme
		else if (sShapeType.equals("rectangle"))
		{	
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - fCanvasWidth + 0.02f, 
//					fCanvasYPos + 0.02f, 
//					-0.001f);
		
			if (bHighlightVertex == true)
			{	
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
		
			}
			else 
			{
				EnzymeToExpressionColorMapper enzymeToExpressionColorMapper =
					new EnzymeToExpressionColorMapper(refGeneralManager, alSetData);
				
				ArrayList<Color> arMappingColor = 
					enzymeToExpressionColorMapper.getMappingColorArrayByVertex(vertexRep);
				
				// Factor indicates how often the enzyme needs to be split
				// so that all genes can be mapped.
				int iSplitFactor = arMappingColor.size();
				
				fPathwayNodeWidth = 
					refRenderStyle.getEnzymeNodeWidth() * SCALING_FACTOR_X;
					
				gl.glPushMatrix();
				double bla = 0;
				
				if (iSplitFactor > 1)
				{
//					System.out.println("Node width: " +fPathwayNodeWidth);
//					System.out.println("Number of genes to map: " +iSplitFactor);
//					System.out.println("Start position: "+bla);
					gl.glTranslatef(-(fPathwayNodeWidth / 2.0f), 0.0f, 0.0f);
					gl.glTranslatef(fPathwayNodeWidth / (iSplitFactor * 2.0f), 0.0f, 0.0f);
				
					bla = bla -fPathwayNodeWidth / 2.0f + fPathwayNodeWidth / (iSplitFactor * 2.0f);
//					System.out.println("Initial position:" +bla);
				}
				
				for (int iSplitIndex = 0; iSplitIndex < iSplitFactor; iSplitIndex++)
				{
					tmpNodeColor = arMappingColor.get(iSplitIndex);
				
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
						tmpNodeColor = refRenderStyle.getEnzymeNodeColor();
					}
					
					gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
							tmpNodeColor.getGreen() / 255.0f, 
							tmpNodeColor.getBlue() / 255.0f, 1.0f);
					
					gl.glScalef(1.0f / iSplitFactor, 1.0f, 1.0f);
					gl.glCallList(iEnzymeNodeDisplayListId);
					gl.glScalef(iSplitFactor, 1.0f, 1.0f);

					if (iSplitFactor > 1)
					{
						bla = bla + fPathwayNodeWidth / iSplitFactor;
//						System.out.println("Intermediate position: "+bla);
					
						gl.glTranslatef(fPathwayNodeWidth / iSplitFactor, 0.0f, 0.0f);
					}
				}

				gl.glPopMatrix();
//				if (iSplitFactor > 1) 
//				{
////					gl.glTranslatef((fPathwayNodeWidth / (float)iSplitFactor*2.0f), 0.0f, 0.0f);
//					gl.glTranslatef(-fPathwayNodeWidth, 0.0f, 0.0f);
//					gl.glTranslatef(fPathwayNodeWidth / (iSplitFactor * 2.0f), 0.0f, 0.0f);
//					
//					bla = bla - fPathwayNodeWidth + fPathwayNodeWidth / (iSplitFactor * 2.0f);
//					System.out.println("End position: " +bla);
//				}
			}
		}

		gl.glTranslatef(-fCanvasXPos, -fCanvasYPos, -fZLayerValue);
		
//		if (bIsRefreshRendering == false)
			gl.glPopName();
	}
	
	public void createEdge(final GL gl,
			int iVertexId1, 
			int iVertexId2, 
			boolean bDrawArrow,
			APathwayEdge refPathwayEdge) {
		
		IPathwayVertexRep vertexRep1, vertexRep2;
		
		PathwayVertex vertex1 = 
			refGeneralManager.getSingelton().getPathwayElementManager().
				getVertexLUT().get(iVertexId1);
		
		PathwayVertex vertex2 = 
			refGeneralManager.getSingelton().getPathwayElementManager().
				getVertexLUT().get(iVertexId2);
		
		vertexRep1 = vertex1.getVertexRepByIndex(iVertexRepIndex);
		vertexRep2 = vertex2.getVertexRepByIndex(iVertexRepIndex);
		
		float fCanvasXPos1 = viewingFrame[X][MIN] + 
			vertexRep1.getXPosition() * SCALING_FACTOR_X;
		float fCanvasYPos1 = viewingFrame[Y][MIN] + 
			vertexRep1.getYPosition() * SCALING_FACTOR_Y;

		float fCanvasXPos2 = viewingFrame[X][MIN] + 
			vertexRep2.getXPosition() * SCALING_FACTOR_X;
		float fCanvasYPos2 = viewingFrame[Y][MIN] + 
			vertexRep2.getYPosition() * SCALING_FACTOR_Y;
		
		Color tmpColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

		// Differentiate between Relations and Reactions
		if (refPathwayEdge.getEdgeType() == EdgeType.REACTION)
		{
//			edgeLineStyle = refRenderStyle.getReactionEdgeLineStyle();
//			edgeArrowHeadStyle = refRenderStyle.getReactionEdgeArrowHeadStyle();
			tmpColor = refRenderStyle.getReactionEdgeColor();
		}
		else if (refPathwayEdge.getEdgeType() == EdgeType.RELATION)
		{
			// In case when relations are maplinks
			if (((PathwayRelationEdge)refPathwayEdge).getEdgeRelationType() 
					== EdgeRelationType.maplink)
			{
//				edgeLineStyle = refRenderStyle.getMaplinkEdgeLineStyle();
//				edgeArrowHeadStyle = refRenderStyle.getMaplinkEdgeArrowHeadStyle();
				tmpColor = refRenderStyle.getMaplinkEdgeColor();
			}
			else 
			{
//				edgeLineStyle = refRenderStyle.getRelationEdgeLineStyle();
//				edgeArrowHeadStyle = refRenderStyle.getRelationEdgeArrowHeadStyle();
				tmpColor = refRenderStyle.getRelationEdgeColor();
			}
		}
		
		gl.glColor4f(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue(), 1.0f);
		gl.glBegin(GL.GL_LINES);		
			gl.glVertex3f(fCanvasXPos1, fCanvasYPos1, fZLayerValue); 
			gl.glVertex3f(fCanvasXPos2, fCanvasYPos2, fZLayerValue);					
		gl.glEnd();				
	}
	
	protected void replacePathway(final GL gl,
			Pathway refPathwayToReplace, 
			int iNewPathwayId) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": replacePathway(): Replace pathway "+refPathwayToReplace.getPathwayID() 
				+" with " +iNewPathwayId,
				LoggerType.MINOR_ERROR );
		
		refGeneralManager.getSingelton().getPathwayManager().loadPathwayById(iNewPathwayId);
		
		Pathway refNewPathway = (Pathway)refGeneralManager.getSingelton().
			getPathwayManager().getItem(iNewPathwayId);
		
		// Replace old pathway with new one in hash maps
		FloatBuffer tmpModelMatrix = refHashPathway2ModelMatrix.get(refPathwayToReplace);
		refHashPathway2ModelMatrix.remove(refPathwayToReplace);
		refHashPathway2ModelMatrix.put(refNewPathway, tmpModelMatrix);

		float fZLayer = refHashPathwayToZLayerValue.get(refPathwayToReplace);
		refHashPathwayToZLayerValue.remove(refPathwayToReplace);
		refHashPathwayToZLayerValue.put(refNewPathway, fZLayer);	
		
		Texture tmpTexture = refHashPathwayToTexture.get(refPathwayToReplace);
		refHashPathwayToTexture.remove(refPathwayToReplace);
		refHashPathwayToTexture.put(refNewPathway, tmpTexture);			
		
		int iTmpDisplayListNodeId = refHashPathway2DisplayListNodeId.get(refPathwayToReplace);
		refHashPathway2DisplayListNodeId.remove(refPathwayToReplace);
		refHashDisplayListNodeId2Pathway.remove(iTmpDisplayListNodeId);

		iArHighlightedVertices.clear();
		iArSelectionStorageNeighborDistance.clear();
		
		IStorage refTmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = refTmpStorage.getArrayInt();
		
		//Replace old pathway ID with new ID
		for (int index = 0; index < iArPathwayIDs.length; index++)
		{
			if (iArPathwayIDs[index] == refPathwayToReplace.getPathwayID())
			{
				iArPathwayIDs[index] = iNewPathwayId;
				break;
			}
		}
		
		refTmpStorage.setArrayInt(iArPathwayIDs);
		
		if (bShowPathwayTexture)
			loadBackgroundOverlayImage(refNewPathway);
		
		createPathwayDisplayList(gl, refNewPathway);
	}

	protected void createPathwayDisplayList(final GL gl, 
			Pathway refTmpPathway) {
		
		// Creating display list for pathways
		int iVerticesDiplayListId = gl.glGenLists(1);
		int iEdgeDisplayListId = gl.glGenLists(1);
		//iArPathwayNodeDisplayListIDs.add(iVerticesDiplayListId);
		iArPathwayEdgeDisplayListIDs.add(iEdgeDisplayListId);

		refHashDisplayListNodeId2Pathway.put(iVerticesDiplayListId, refTmpPathway);	
		refHashPathway2DisplayListNodeId.put(refTmpPathway, iVerticesDiplayListId);
		
		gl.glNewList(iVerticesDiplayListId, GL.GL_COMPILE);	
		extractVertices(gl, refTmpPathway);
		gl.glEndList();

		gl.glNewList(iEdgeDisplayListId, GL.GL_COMPILE);	
		extractEdges(gl, refTmpPathway);
		gl.glEndList();	
	}
	
	protected void extractVertices(final GL gl,
			Pathway refPathwayToExtract) {
		
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    IPathwayVertexRep vertexRep;
		
        vertexIterator = refPathwayToExtract.getVertexListIterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexRep = vertex.getVertexRepByIndex(iVertexRepIndex);

        	if (vertexRep != null)
        	{
        		createVertex(gl,
        				vertexRep, 
        				refPathwayToExtract);        	
        	}
        }   
	}
	
	protected void extractEdges(final GL gl,
			Pathway refPathwayToExtract) {
		
		// Process relation edges
	    Iterator<PathwayRelationEdge> relationEdgeIterator;
        relationEdgeIterator = refPathwayToExtract.getRelationEdgeIterator();
        while (relationEdgeIterator.hasNext())
        {
        	extractRelationEdges(gl, relationEdgeIterator.next()); 		
        }
		
	    // Process reaction edges
        PathwayReactionEdge reactionEdge;
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
		IPathwayElementManager pathwayElementManager = 
			((IPathwayElementManager)refGeneralManager.getSingelton().
				getPathwayElementManager());
		
        vertexIterator = refPathwayToExtract.getVertexListIterator();
	    
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
	            	extractReactionEdges(gl, reactionEdge, vertex);
	    		}// if (edge != null)
	    	}// if (vertex.getVertexType() == PathwayVertexType.enzyme)
	    }
	}
	
	protected void extractRelationEdges(final GL gl,
			PathwayRelationEdge relationEdge) {
		
		// Direct connection between nodes
		if (relationEdge.getCompoundId() == -1)
		{
			createEdge(gl,
					relationEdge.getElementId1(), 
					relationEdge.getElementId2(), 
					false, 
					relationEdge);
		}
		// Edge is routed over a compound
		else 
		{
			createEdge(gl,
					relationEdge.getElementId1(), 
					relationEdge.getCompoundId(), 
					false, 
					relationEdge);
			
			if (relationEdge.getEdgeRelationType() 
					== EdgeRelationType.ECrel)
			{
    			createEdge(gl,
    					relationEdge.getCompoundId(), 
    					relationEdge.getElementId2(), 
    					false,
    					relationEdge);
			}
			else
			{
    			createEdge(gl,
    					relationEdge.getElementId2(),
    					relationEdge.getCompoundId(),
    					true,
    					relationEdge);
			}
		}
	}
	
	protected void extractReactionEdges(final GL gl,
			PathwayReactionEdge reactionEdge, 
			PathwayVertex vertex) {
		
		if (!reactionEdge.getSubstrates().isEmpty())
		{
			//FIXME: interate over substrates and products
			createEdge(gl,
					reactionEdge.getSubstrates().get(0), 
					vertex.getElementId(), 
					false,
					reactionEdge);	
		}
		
		if (!reactionEdge.getProducts().isEmpty())
		{
			createEdge(gl,
					vertex.getElementId(),
					reactionEdge.getProducts().get(0), 
					true,
					reactionEdge);
		}	  
	}
	
	protected void connectVertices(final GL gl,
			IPathwayVertexRep refVertexRep1, 
			IPathwayVertexRep refVertexRep2) {

		float fZLayerValue1 = 0.0f; 
		float fZLayerValue2 = 0.0f;
		Pathway refTmpPathway = null;
		Texture refPathwayTexture = null;
		float fCanvasXPos1 = 0.0f;
		float fCanvasYPos1 = 0.0f;
		float fCanvasXPos2 = 0.0f;
		float fCanvasYPos2 = 0.0f;
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		
		buildEnzymeNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);
		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{			
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
				getItem(iArPathwayIDs[iPathwayIndex]);

			// Recalculate scaling factor
			refPathwayTexture = refHashPathwayToTexture.get(refTmpPathway);
			fPathwayTextureAspectRatio = 
				(float)refPathwayTexture.getImageWidth() / 
				(float)refPathwayTexture.getImageHeight();								
			
			if(refTmpPathway.isVertexInPathway(refVertexRep1.getVertex()) == true)
			{					
				//fZLayerValue1 = refHashPathwayToZLayerValue.get(refTmpPathway);
				fZLayerValue1 = 0.0f;
				
				fCanvasXPos1 = viewingFrame[X][MIN] + 
					refVertexRep1.getXPosition() * SCALING_FACTOR_X;
				fCanvasYPos1 = viewingFrame[Y][MIN] + 
					refVertexRep1.getYPosition() * SCALING_FACTOR_Y;
			}
			
			if(refTmpPathway.isVertexInPathway(refVertexRep2.getVertex()) == true)
			{					
				//fZLayerValue2 = refHashPathwayToZLayerValue.get(refTmpPathway);
				fZLayerValue2 = 0.0f;
				
				fCanvasXPos2 = viewingFrame[X][MIN] + 
					refVertexRep2.getXPosition() * SCALING_FACTOR_X;
				fCanvasYPos2 = viewingFrame[Y][MIN] + 
					refVertexRep2.getYPosition() * SCALING_FACTOR_Y;
			}
		}
		
		float[] tmpVec1 = {fCanvasXPos1, fCanvasYPos1, fZLayerValue1, 1.0f};
		float[] tmpVec2 = {fCanvasXPos2, fCanvasYPos2, fZLayerValue2, 1.0f};
		
		float[] resultVec1 = {1.0f, 1.0f, 1.0f, 1.0f};		
		float[] resultVec2 = {1.0f, 1.0f, 1.0f, 1.0f};
		
		vecMatrixMult(tmpVec1, refHashPathway2ModelMatrix.get(refTmpPathway).array(), resultVec1);
		vecMatrixMult(tmpVec2, refHashPathway2ModelMatrix.get(refTmpPathway).array(), resultVec2);
		
		fCanvasXPos1 = resultVec1[0];
		fCanvasYPos1 = resultVec1[1];
		fZLayerValue1 = resultVec1[2];
		
		fCanvasXPos2 = resultVec2[0];
		fCanvasYPos2 = resultVec2[1];
		fZLayerValue2 = resultVec2[2];
		
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		gl.glLineWidth(3);
		gl.glBegin(GL.GL_LINES);		
			gl.glVertex3f(fCanvasXPos1, fCanvasYPos1, fZLayerValue1); 
			gl.glVertex3f(fCanvasXPos2, fCanvasYPos2, fZLayerValue2);					
		gl.glEnd();
		gl.glLineWidth(1);
	}
	
	protected abstract void renderScene(final GL gl);
	
	public void renderInfoArea(final GL gl) {
		
		gl.glDisable(GL.GL_LIGHTING);

		for (int iLineNumber = 0; iLineNumber < refInfoAreaContent.size(); iLineNumber++)
		{
			renderStaticText(gl,
					refInfoAreaCaption.get(iLineNumber),
					10,
					(refInfoAreaContent.size() - iLineNumber) * 15);
			
			renderStaticText(gl,
					refInfoAreaContent.get(iLineNumber), 
					150, 
					(refInfoAreaContent.size() - iLineNumber) * 15);
		}

//		float fHeight = 0.7f;
//		float fWidth = 5.0f;		
		
//		gl.glColor4f(1f, 1.0f, 0.2f, 0.2f);
////		gl.glTranslated(fx, fy, fz);
//		gl.glRectf(fx, fy, fx + fWidth, fy + fHeight);
//		//gl.glTranslated(-fx, -fy, -fz);
//		
		gl.glEnable(GL.GL_LIGHTING);
	}
	
	/**
	 * Method for rendering text in OpenGL.
	 * TODO: Move method to some kind of GL Utility class.
	 * 
	 * @param gl
	 * @param showText
	 * @param fx
	 * @param fy
	 * @param fz
	 */
	public void renderText(final GL gl,
			final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		final float fFontSizeOffset = 0.02f;

		GLUT glut = new GLUT();

		// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// gl.glLoadIdentity();
		// gl.glTranslatef(0.0f,0.0f,-1.0f);

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 0.0f);

		// Position The Text On The Screen...fullscreen goes much slower than
		// the other
		// way so this is kind of necessary to not just see a blur in smaller
		// windows
		// and even in the 640x480 method it will be a bit blurry...oh well you
		// can
		// set it if you would like :)
		gl.glRasterPos3f(fx - fFontSizeOffset, fy - fFontSizeOffset, fz);
		
		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText); 
	}

	public void renderStaticText(final GL gl,
			final String showText,
			final int iWindowPosX, 
			final int iWindowPosY) { 
		
		GLUT glut = new GLUT();

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 0.0f);

		gl.glWindowPos2i(iWindowPosX, iWindowPosY);
		
		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText); 
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#link2GLCanvasDirector(cerberus.view.gui.opengl.IGLCanvasDirector)
	 */
	public final void link2GLCanvasDirector(IGLCanvasDirector parentView) {
		
		if ( openGLCanvasDirector == null ) {
			openGLCanvasDirector = parentView;
		}
		
		parentView.addGLCanvasUser( this );
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#showBackgroundOverlay(boolean)
	 */
	public void showBackgroundOverlay(boolean bTurnOn) {

		System.err.println("SHOW BACKGROUND OVERLAY: " + bTurnOn);	
		
		bShowPathwayTexture = bTurnOn;
//		
//		buildPathwayDisplayList(gl);
		//getGLCanvas().display();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#finishGraphBuilding()
	 */
	public void finishGraphBuilding() {

		// Draw title
//		renderText(refCurrentPathway.getTitle(), 0.0f, 0.0f, fZLayerValue);	
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#loadBackgroundOverlayImage(java.lang.String)
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
		
		ISetSelection refSetSelection = (ISetSelection)updatedSet;
		
		refGeneralManager.getSingelton().logMsg(
				"OpenGL Pathway update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		// Clear old selected vertices
		iArHighlightedVertices.clear();
		// Clear old neighborhood distances
		iArSelectionStorageNeighborDistance.clear();
		
		// Read selected vertex IDs
		int[] iArSelectedElements = refSetSelection.getSelectionIdArray();
		
		// Read neighbor data
		int[] iArSelectionNeighborDistance = refSetSelection.getOptionalDataArray();
		
		for (int iSelectedVertexIndex = 0; 
			iSelectedVertexIndex < ((IStorage)refSetSelection.getStorageByDimAndIndex(0, 0)).getSize(StorageType.INT);
			iSelectedVertexIndex++)
		{			
			iArHighlightedVertices.add(refGeneralManager.getSingelton().getPathwayElementManager().
					getVertexLUT().get(iArSelectedElements[iSelectedVertexIndex]).getVertexRepByIndex(0));
			
			if (iArSelectionNeighborDistance.length > 0)
				iArSelectionStorageNeighborDistance.add(iArSelectionNeighborDistance[iSelectedVertexIndex]);
			else
				iArSelectionStorageNeighborDistance.add(0);
		}
		
		bSelectionDataChanged = true;
	}
	
	
	protected void pickObjects(final GL gl) {

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount;
		int viewport[] = new int[4];

		// Deselect all highlighted nodes.
//		iArHighlightedVertices.clear();
//		iArSelectionStorageNeighborDistance.clear();

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);

		gl.glInitNames();
		//gl.glPushName(0);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 1.0, viewport, 0); // pick width and height is set to 5 (i.e. picking tolerance)
		
		float h = (float) (float) (viewport[3]-viewport[1]) / 
			(float) (viewport[2]-viewport[0]);

		//FIXME: The frustum should be calculated from the XML parameters in the future
		//gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
		gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 60.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

//		System.out.println("Viewport: " +viewport[0] +" " +viewport[1] +" " +viewport[2] +" " +viewport[3]);
//		System.out.println("Picked point: " +pickPoint.x +" " +pickPoint.y);
		
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
		
		int iNames = 0;
		int iPtr = 0;
		int i = 0;
		int iPickedPathwayDisplayListNodeId = 0;

		for (i = 0; i < iHitCount; i++)
		{
			iNames = iArPickingBuffer[iPtr];
			System.out.println(" number of names for this hit = " + iNames);
			iPtr++;
			System.out.println(" z1 is  " + (float) iArPickingBuffer[iPtr] / 0x7fffffff);
			iPtr++;
			System.out.println(" z2 is " + (float) iArPickingBuffer[iPtr] / 0x7fffffff);
			iPtr++;
			System.out.println(" names are ");
			
//			if (iNames != 2)
//				return;
			
//			for (int j = 0; j < iNames; j++)
//			{
//				System.out.println("Pathway pick node ID:" + iArPickingBuffer[iPtr]);
//				iPtr++;
//			}
			
			iPickedPathwayDisplayListNodeId = iArPickingBuffer[iPtr];
			
			iPtr++;

			//System.out.println("Object pick ID: " + iArPickingBuffer[iPtr]);

			refPickedVertexRep = refHashPickID2VertexRep.get(iArPickingBuffer[iPtr]);
			
			if (refPickedVertexRep == null)
				return;
							
			fillInfoAreaContent(refPickedVertexRep);

			// Perform real element picking
			// That means the picked node will be highlighted.
			// Otherwise the picking action was only mouse over.
			if (!bIsMouseOverPickingEvent)
			{
				// Update the currently selected pathway
				refPathwayUnderInteraction = refHashDisplayListNodeId2Pathway.get(
						iPickedPathwayDisplayListNodeId);
				
				// Check if the clicked node is a pathway
				// If this is the case the current pathway will be replaced by the clicked one.
				if(refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
				{		
					int iNewPathwayId = new Integer(refPickedVertexRep.getVertex().getElementTitle().substring(8));

					//Check if picked pathway is alread displayed
					if (!refHashPathway2DisplayListNodeId.containsKey(refGeneralManager.getSingelton().
							getPathwayManager().getItem(iNewPathwayId)))
					{
						replacePathway(gl, 
								refPathwayUnderInteraction, 
								iNewPathwayId);
						return;							
					}
				}
				
				if (!iArHighlightedVertices.contains(refPickedVertexRep))
				{
					// Clear currently highlighted vertices when new node was selected
					if(!iArHighlightedVertices.isEmpty()) 
					{
						iArHighlightedVertices.clear();
						iArSelectionStorageNeighborDistance.clear();
					}
					
					iArHighlightedVertices.add(refPickedVertexRep);
					iArSelectionStorageNeighborDistance.add(0);
					
					// Convert to int[]
					int[] iArTmp = new int[iArHighlightedVertices.size()];
					for(int index = 0; index < iArHighlightedVertices.size(); index++)
						iArTmp[index] = iArHighlightedVertices.get(index).getVertex().getElementId();
					
					updateSelectionSet(iArTmp, 
							new int[0], new int[0]);
					
					refGeneralManager.getSingelton().logMsg(
							"OpenGL Pathway object selected: " +refPickedVertexRep.getName(),
							LoggerType.VERBOSE);
				}
				else
				{
					//iArHighlightedVertices.remove(refPickedVertexRep);
					iArHighlightedVertices.clear();
					
//					// Remove identical nodes from unselected vertex
//					iterIdenticalVertices = refGeneralManager.getSingelton().
//						getPathwayElementManager().getPathwayVertexListByName(
//							pickedVertexRep.getVertex().getElementTitle()).iterator();
//	
//					while(iterIdenticalVertices.hasNext())
//					{
//						iArHighlightedVertices.remove(iterIdenticalVertices.next().
//								getVertexRepByIndex(iVertexRepIndex));
//					}
					
					refGeneralManager.getSingelton().logMsg(
							"OpenGL Pathway object unselected: " +refPickedVertexRep.getName(),
							LoggerType.VERBOSE);
				}
				
				//fillInfoAreaContent(refPickedVertexRep);
				
				// FIXME: not very efficient
				// All display lists are newly created
				//iArPathwayNodeDisplayListIDs.clear();
				iArPathwayEdgeDisplayListIDs.clear();
				buildPathwayDisplayList(gl);
				
//				gl.glNewList(iPickedPathwayDisplayListNodeId, GL.GL_COMPILE);	
//				extractVertices(refPathwayUnderInteraction);
//				gl.glEndList();
								
//				loadNodeInformationInBrowser(
//						refPickedVertexRep.getVertex().getVertexLink());
			}
		}
	}	
	
	protected void fillInfoAreaContent(IPathwayVertexRep refPickedVertexRep) {
		
		// Do nothing if picked node is invalid.
		if (refPickedVertexRep == null)
			return;

		refInfoAreaCaption.clear();
		refInfoAreaContent.clear();
		
		// Check if vertex is an pathway
		if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
		{
			refInfoAreaCaption.add("Pathway: ");
			refInfoAreaContent.add(refPickedVertexRep.getVertex().getVertexReps()[0].getName());
		}		
		// Check if vertex is an compound
		else if (refPickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.compound))
		{
			refInfoAreaCaption.add("Compound: ");
			refInfoAreaContent.add(refPickedVertexRep.getVertex().getVertexReps()[0].getName());
		}
		// Check if vertex is an enzyme.
		else if (refPickedVertexRep.getVertex().getVertexType().
				equals(PathwayVertexType.enzyme))
		{
			String sEnzymeCode = refPickedVertexRep.getVertex().getElementTitle().substring(3);
			String sAccessionCode = "";
			String sTmpGeneName = "";
			String sMicroArrayCode = "";
			int iAccessionID = 0;
			int iGeneID = 0;
			Collection<Integer> iArTmpAccessionId = null;
			
			// FIXME: From where can we get the storage ID?
			int iExpressionStorageId = 45301;
			
			refInfoAreaCaption.add("Enzyme: ");
			refInfoAreaContent.add(sEnzymeCode);
			
			//Just for testing mapping!
			IGenomeIdManager refGenomeIdManager = 
				refGeneralManager.getSingelton().getGenomeIdManager();
			
			int iEnzymeID = refGenomeIdManager.getIdIntFromStringByMapping(sEnzymeCode, 
					GenomeMappingType.ENZYME_CODE_2_ENZYME);
			
			if (iEnzymeID == -1)
				return;
			
			Collection<Integer> iTmpGeneId = refGenomeIdManager.getIdIntListByType(iEnzymeID, 
					GenomeMappingType.ENZYME_2_NCBI_GENEID);
			
			if(iTmpGeneId == null)
				return;
			
			Iterator<Integer> iterTmpGeneId = iTmpGeneId.iterator();
			Iterator<Integer> iterTmpAccessionId = null;
			while (iterTmpGeneId.hasNext())
			{
				iGeneID = iterTmpGeneId.next();
				
				String sNCBIGeneId = refGenomeIdManager.getIdStringFromIntByMapping(iGeneID, 
						GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);
				
				refInfoAreaCaption.add("NCBI GeneID: ");
				refInfoAreaContent.add(sNCBIGeneId);
				
				iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
						GenomeMappingType.NCBI_GENEID_2_ACCESSION);
		
				if (iAccessionID == -1)
					break;
				
				sAccessionCode = refGenomeIdManager.getIdStringFromIntByMapping(iAccessionID, 
						GenomeMappingType.ACCESSION_2_ACCESSION_CODE);
								
				refInfoAreaCaption.add("Accession: ");
				refInfoAreaContent.add(sAccessionCode);
				
				sTmpGeneName = refGenomeIdManager.getIdStringFromIntByMapping(iAccessionID, 
						GenomeMappingType.ACCESSION_2_GENE_NAME);
		
				refInfoAreaCaption.add("Gene name: ");
				refInfoAreaContent.add(sTmpGeneName);
				
				iArTmpAccessionId = refGenomeIdManager.getIdIntListByType(iAccessionID, 
						GenomeMappingType.ACCESSION_2_MICROARRAY);
				
				if(iArTmpAccessionId == null)
					continue;
						
				iterTmpAccessionId = iArTmpAccessionId.iterator();
				while (iterTmpAccessionId.hasNext())
				{
					int iMicroArrayId = iterTmpAccessionId.next();
					
					sMicroArrayCode = refGenomeIdManager.getIdStringFromIntByMapping(iMicroArrayId, 
							GenomeMappingType.MICROARRAY_2_MICROARRAY_CODE);
					
					refInfoAreaCaption.add("MicroArray: ");
					refInfoAreaContent.add(sMicroArrayCode);
					
					//Get expression value by MicroArrayID
					IStorage refExpressionStorage = refGeneralManager.getSingelton().
						getStorageManager().getItemStorage(iExpressionStorageId);
					int iExpressionStorageIndex = refGenomeIdManager.getIdIntFromIntByMapping(
							iMicroArrayId, GenomeMappingType.MICROARRAY_2_MICROARRAY_EXPRESSION);
					
					// Get rid of 770 internal ID identifier
					iExpressionStorageIndex = (int)(((float)iExpressionStorageIndex - 770.0f) / 1000.0f);
					
					int iExpressionValue = (refExpressionStorage.getArrayInt())[iExpressionStorageIndex];
					refInfoAreaCaption.add("Expression value: ");
					refInfoAreaContent.add(new Integer(iExpressionValue).toString());
				}
			}
		}
	}
    
    protected abstract void highlightIdenticalNodes(final GL gl);

    protected void vecMatrixMult(float[] vecIn, float[] matIn, float[] vecOut) {
    	
    	vecOut[0] = (vecIn[0]*matIn[ 0]) + (vecIn[1]*matIn[ 1]) + (vecIn[2]*matIn[ 2]) + (vecIn[3]*matIn[ 3]);
    	vecOut[1] = (vecIn[0]*matIn[ 4]) + (vecIn[1]*matIn[ 5]) + (vecIn[2]*matIn[ 6]) + (vecIn[3]*matIn[ 7]);
    	vecOut[2] = (vecIn[0]*matIn[ 8]) + (vecIn[1]*matIn[ 9]) + (vecIn[2]*matIn[10]) + (vecIn[3]*matIn[11]);
    	vecOut[3] = (vecIn[0]*matIn[12]) + (vecIn[1]*matIn[13]) + (vecIn[2]*matIn[14]) + (vecIn[3]*matIn[15]);
      
    	vecOut[0] /= vecOut[3];
    	vecOut[1] /= vecOut[3];
    	vecOut[2] /= vecOut[3];
    	vecOut[3] = 1.0f;
    }
    
    protected void handlePicking(final GL gl) {
    	
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
    		pickObjects(gl);
    		bIsMouseOverPickingEvent = false;
    	}
	    //System.out.println("Picking idle time: " + ((System.nanoTime() - fLastMouseMovedTimeStamp)) * 1e-9);
    }
    
	public void setResolution( float[] setResolution ) {
		
//		if ( fResolution.length < 6 ) {
//			throw new RuntimeException("GLCanvasMinMaxScatterPlot2D.setResolution() array must contain 3 items.");
//		}
		
		this.fResolution = setResolution;
		
		fAspectRatio[X][MIN] = fResolution[0];
		fAspectRatio[X][MAX] = fResolution[1]; 
		fAspectRatio[Y][MIN] = fResolution[2]; 
		fAspectRatio[Y][MAX] = fResolution[3]; 
		
		fAspectRatio[X][OFFSET] = fResolution[4]; 
		fAspectRatio[Y][OFFSET] = fResolution[5];
		
		viewingFrame[X][MIN] = fResolution[6];
		viewingFrame[X][MAX] = fResolution[7]; 
		viewingFrame[Y][MIN] = fResolution[8]; 
		viewingFrame[Y][MAX] = fResolution[9];
		viewingFrame[Z][MIN] = fResolution[10]; 
		viewingFrame[Z][MAX] = fResolution[11]; 
				
//		iValuesInRow = (int) fResolution[12]; 
		
	}

	
	public void updateReceiver(Object eventTrigger) {
		System.err.println( "UPDATE BINGO !");
	}
	
	//FIXME: Method is also implemented in APathwayGraphViewRep for 2D pathway
	public void updateSelectionSet(int[] iArSelectionVertexId,
			int[] iArSelectionGroup,
			int[] iArNeighborVertices) {
	
		try {
			// Update selection SET data.
			alSetSelection.get(0).setAllSelectionDataArrays(
					iArSelectionVertexId, iArSelectionGroup, iArNeighborVertices);
			
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Set selection data and trigger update.",
					LoggerType.VERBOSE );
						
	 		// Calls update with the ID of the PathwayViewRep
	 		((EventPublisher)refGeneralManager.getSingelton().
				getEventPublisher()).updateReceiver(refGeneralManager.
						getSingelton().getViewGLCanvasManager().
							getItem(iUniqueId), alSetSelection.get(0));
	 		
		} catch (Exception e)
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Problem during selection update triggering.",
					LoggerType.MINOR_ERROR );
	
			e.printStackTrace();
		}
	}
}
