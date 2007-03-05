package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.layout.GridLayout;

import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.selection.SetSelection;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.view.gui.opengl.GLCanvasStatics;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;
import cerberus.view.gui.swt.toolbar.Pathway3DToolbar;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/**
 * @author Marc Streit
 *
 */
public abstract class AGLCanvasPathway3D
extends APathwayGraphViewRep
implements IGLCanvasUser {
		  	 
	protected float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	protected boolean bInitGLcanvawsWasCalled = false;
	
	protected static final int X = GLCanvasStatics.X;
	protected static final int Y = GLCanvasStatics.Y;
//	private static final int Z = GLCanvasStatics.Z;
	protected static final int MIN = GLCanvasStatics.MIN;
	protected static final int MAX = GLCanvasStatics.MAX;
//	private static final int OFFSET = GLCanvasStatics.OFFSET;
	
	protected static final float SCALING_FACTOR_X = 0.0025f;
	protected static final float SCALING_FACTOR_Y = 0.0025f;
	
	protected int iVertexRepIndex = 0;

	protected GLAutoDrawable canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	protected Vec3f origin;
	
	protected Vec4f rotation;
	
	protected GL gl;
	
	protected float fZLayerValue = 0.0f;
	
	protected ArrayList<Integer> iArPathwayNodeDisplayListIDs;
	
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
	 * Pathway that is currently under user interaction in the 2D pathway view.
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
	
	/**
	 * Constructor
	 * 
	 */
	public AGLCanvasPathway3D( final IGeneralManager refGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
				
		super(refGeneralManager, iViewId, iParentContainerId, "");
		
		openGLCanvasDirector =
			refGeneralManager.getSingelton().
				getViewGLCanvasManager().getGLCanvasDirector( iParentContainerId );
				
		refSWTContainer = ((SwtJoglGLCanvasViewRep)openGLCanvasDirector).getSWTContainer();
		refSWTContainer.setLayout(new GridLayout(1, false));
		new Pathway3DToolbar(refSWTContainer, this);
		
		//FIXME: Is refEmbeddedFrameComposite variable really needed?
		refEmbeddedFrameComposite = refSWTContainer;
		
		//FIXME: Browser id should be specified in the XML bootstrap file.
		iHTMLBrowserId = 85401;
		
		this.canvas = openGLCanvasDirector.getGLCanvas();
		
		canvas.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseEvent) {

				if (mouseEvent.getButton() == MouseEvent.BUTTON2)
				{
					pickPoint = mouseEvent.getPoint();
				}
			}
		});
		
		viewingFrame = new float [2][2];
		
		viewingFrame[X][MIN] = 0.0f;
		viewingFrame[X][MAX] = 1.0f; 
		viewingFrame[Y][MIN] = 0.0f; 
		viewingFrame[Y][MAX] = 1.0f; 
	
		iArSelectionStorageNeighborDistance = new ArrayList<Integer>();
		iArPathwayNodeDisplayListIDs = new ArrayList<Integer>();
		iArPathwayEdgeDisplayListIDs = new ArrayList<Integer>();
		iArHighlightedVertices = new ArrayList<IPathwayVertexRep>();

		refHashPathwayToZLayerValue = new HashMap<Pathway, Float>();
		refHashPathwayToTexture = new HashMap<Pathway, Texture>();
		refHashPickID2VertexRep = new HashMap<Integer, IPathwayVertexRep>();
		refHashDisplayListNodeId2Pathway = new HashMap<Integer, Pathway>();
		refHashPathway2DisplayListNodeId = new HashMap<Pathway, Integer>();
		refHashPathway2ModelMatrix = new HashMap<Pathway, FloatBuffer>();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas( GLCanvas canvas ) {
		
		//FIXME: derive from AGLCanvasUser !

		System.err.println(" init GLPAthway 3D...");
		
		this.gl = canvas.getGL();
		
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
	
		float[] fMatSpecular = { 1.0f, 1.0f, 1.0f, 1.0f};
		float[] fMatShininess = {25.0f}; 
		float[] fLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};
		float[] fWhiteLight = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] fModelAmbient = {0.6f, 0.6f, 0.6f, 1.0f};
		
//		gl.glEnable(GL.GL_COLOR_MATERIAL);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
//		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
//		
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, fMatSpecular, 0);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, fMatShininess, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, fLightPosition, 0);		
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, fWhiteLight, 0);
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, fWhiteLight, 0);
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, fModelAmbient, 0);

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);		
		
//		bCanvasInitialized = true;
		
	    gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	    gl.glEnable(GL.GL_TEXTURE_2D);
		
		initPathwayData();
		buildPathwayDisplayList();	
		
		setInitGLDone();

	}	
	
	/**
	 * Initializing the zLayer value for the layered view
	 * and loading the overlay texture for each pathway.
	 *
	 */
	protected void initPathwayData() {
	
		Pathway refTmpPathway = null;
		
		refHashPathwayToTexture.clear();
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		String sPathwayTexturePath = "";
		int iPathwayId = 0;
		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{
			System.out.println("Create display list for new pathway");
		
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
				getItem(iArPathwayIDs[iPathwayIndex]);
			
			iPathwayId = refTmpPathway.getPathwayID();
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
			
			sPathwayTexturePath = "data/images/pathways/" + sPathwayTexturePath +".gif";	
			
			loadBackgroundOverlayImage(sPathwayTexturePath, refTmpPathway);		
		}
	}
	
	protected abstract void buildPathwayDisplayList();
//
//		Pathway refTmpPathway = null;
//
//		refHashDisplayListNodeId2Pathway.clear();
//		refHashPathway2DisplayListNodeId.clear();
//		
//		System.out.println("Create pathway display lists");
//
//		// Load pathway storage
//		// Assumes that the set consists of only one storage
//		IStorage tmpStorage = refPathwaySet.getStorageByDimAndIndex(0, 0);
//		int[] iArPathwayIDs = tmpStorage.getArrayInt();
//		
//		buildEnzymeNodeDisplayList();
//		buildHighlightedEnzymeNodeDisplayList();
//		buildCompoundNodeDisplayList();
//		buildHighlightedCompoundNodeDisplayList();
//		
//		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
//			iPathwayIndex++)
//		{			
//			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
//				getItem(iArPathwayIDs[iPathwayIndex]);
//		
//			System.out.println("Create display list for pathway "+refTmpPathway.getTitle());
//			
//			// Creating display list for pathways
//			int iVerticesDisplayListId = gl.glGenLists(1);
//			int iEdgeDisplayListId = gl.glGenLists(1);
//			iArPathwayNodeDisplayListIDs.add(iVerticesDisplayListId);
//			iArPathwayEdgeDisplayListIDs.add(iEdgeDisplayListId);
//
//			refHashDisplayListNodeId2Pathway.put(iVerticesDisplayListId, refTmpPathway);	
//			refHashPathway2DisplayListNodeId.put(refTmpPathway, iVerticesDisplayListId);
//			
//			gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);	
//			extractVertices(refTmpPathway);
//			gl.glEndList();
//	
//			gl.glNewList(iEdgeDisplayListId, GL.GL_COMPILE);	
//			extractEdges(refTmpPathway);
//			gl.glEndList();
//		}
//	}

	protected void buildEnzymeNodeDisplayList() {

		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getEnzymeNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getEnzymeNodeHeight() / 2.0f * SCALING_FACTOR_Y;
			
		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList();
        gl.glEndList();
	}
	
	protected void buildHighlightedEnzymeNodeDisplayList() {
		
		if (iHighlightedEnzymeNodeDisplayListId == -1)
		{
			// Creating display list for node cube objects
			iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);
		}
		
		fPathwayNodeWidth = 
			refRenderStyle.getEnzymeNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getEnzymeNodeHeight() / 2.0f * SCALING_FACTOR_Y;
				
		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL.GL_COMPILE);
		gl.glScaled(fHighlightedNodeBlowFactor, 
				fHighlightedNodeBlowFactor, fHighlightedNodeBlowFactor);
		fillNodeDisplayList();
		gl.glScaled(1.0f/fHighlightedNodeBlowFactor, 
				1.0f/fHighlightedNodeBlowFactor, 1.0f/fHighlightedNodeBlowFactor);  
        
        gl.glEndList();
	}
	
	protected void buildCompoundNodeDisplayList() {

		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getCompoundNodeWidth() / 2.0f * SCALING_FACTOR_X;
		fPathwayNodeHeight = 
			refRenderStyle.getCompoundNodeHeight() / 2.0f * SCALING_FACTOR_Y;
		
		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList();
        gl.glEndList();
	}
	
	protected void buildHighlightedCompoundNodeDisplayList() {

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
		fillNodeDisplayList();
		gl.glScaled(1.0f/fHighlightedNodeBlowFactor, 
				1.0f/fHighlightedNodeBlowFactor, 1.0f/fHighlightedNodeBlowFactor);
		
        gl.glEndList();
	}
	
	protected void fillNodeDisplayList() {
		
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
	
	protected void fillHighlightedNodeDisplayList() {
		
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
	
	public final void render(GLAutoDrawable canvas) {
		
		this.gl = canvas.getGL();
		
		// Clear The Screen And The Depth Buffer
		gl.glPushMatrix();

		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
		gl.glRotatef( rotation.x(), 
				rotation.y(),
				rotation.z(),
				rotation.w() );

		if (pickPoint != null)
		{
			pickObjects(gl);
		}
		
		renderPart(gl, GL.GL_RENDER);

		//gl.glFlush();
		gl.glPopMatrix();	
	}

	protected abstract void renderPart(GL gl, int iRenderMode);
//		
//		this.gl = gl;
//		
//		int iDisplayListNodeId = 0;
//		int iDisplayListEdgeId = 0;
//		
//		// Rebuild highlight node display list using the new scaling factor
//		if (!iArHighlightedVertices.isEmpty())
//		{
//			if (bBlowUp == true)
//			{
//				fHighlightedNodeBlowFactor += 0.010f;
//				
//				if (fHighlightedNodeBlowFactor >= 1.3f)
//					bBlowUp = false;
//			}
//			else
//			{
//				fHighlightedNodeBlowFactor -= 0.010;
//				
//				if (fHighlightedNodeBlowFactor <= 1.0f)
//					bBlowUp = true;
//			}
//			
//			buildHighlightedEnzymeNodeDisplayList();
//			buildHighlightedCompoundNodeDisplayList();		
//		}
//		
//		gl.glPushMatrix();
//		if (bShowPathwayTexture == true)
//		{				
//			Pathway refTmpPathway = null;
//			refHashPathway2ModelMatrix.clear();
//			
//			// Load pathway storage
//			// Assumes that the set consists of only one storage
//			IStorage tmpStorage = refPathwaySet.getStorageByDimAndIndex(0, 0);
//			int[] iArPathwayIDs = tmpStorage.getArrayInt();
//			
//			// Render pathway under interaction
//			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
//				getItem(iArPathwayIDs[0]);
//			
//			gl.glRotatef(-60, 1.0f, 0.0f, 0.0f);
//
//			iDisplayListNodeId = refHashPathway2DisplayListNodeId.get(refTmpPathway);
//
//			FloatBuffer testMatrixBuffer = FloatBuffer.allocate(16);
//			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, testMatrixBuffer);
//			refHashPathway2ModelMatrix.put(refTmpPathway, testMatrixBuffer);
//			
//			renderPathway(refTmpPathway, iDisplayListNodeId);
//
//			gl.glRotatef(60, 1.0f, 0.0f, 0.0f);
//			
//			gl.glTranslatef(-8.0f, 7.0f, 15.0f);
//			
//			for (int iPathwayIndex = 1; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
//				iPathwayIndex++)
//			{
//				refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
//					getItem(iArPathwayIDs[iPathwayIndex]);
//			
//				gl.glTranslatef(3.0f, 0.0f, 0.0f);
//								
//				iDisplayListNodeId = refHashPathway2DisplayListNodeId.get(refTmpPathway);
//
//				testMatrixBuffer = FloatBuffer.allocate(16);
//				gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, testMatrixBuffer);
//				refHashPathway2ModelMatrix.put(refTmpPathway, testMatrixBuffer);
//				
//				renderPathway(refTmpPathway, iDisplayListNodeId);
//			}
//		}
//		gl.glPopMatrix();
//
//		highlightIdenticalNodes();
//	}
	
	protected abstract void renderPathway(final Pathway refTmpPathway, 
			int iDisplayListNodeId);
		
//		// Creating hierarchical picking names
//		// This is the layer of the pathways, therefore we can use the pathway
//		// node picking ID
//		gl.glPushName(iDisplayListNodeId);	
//		gl.glCallList(iDisplayListNodeId);
//		gl.glPopName();
//		
//		Texture refPathwayTexture = null;
//		float fTmpZLayerValue = 0.0f;
//		float fTextureWidth;
//		float fTextureHeight;
//		
//		refPathwayTexture = refHashPathwayToTexture.get(refTmpPathway);
//		fTmpZLayerValue = refHashPathwayToZLayerValue.get(refTmpPathway);
//		
//		refPathwayTexture.enable();
//		refPathwayTexture.bind();
//		gl.glTexEnvi(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
//
//		gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
//
//		TextureCoords texCoords = refPathwayTexture.getImageTexCoords();
//		
//		// Recalculate scaling factor
//		fPathwayTextureAspectRatio = 
//			(float)refPathwayTexture.getImageWidth() / 
//			(float)refPathwayTexture.getImageHeight();								
//		
//		fTextureWidth = 0.0025f * (float)refPathwayTexture.getImageWidth();
//		fTextureHeight = 0.0025f * (float)refPathwayTexture.getImageHeight();				
//		
//		gl.glBegin(GL.GL_QUADS);
//		gl.glTexCoord2f(0, texCoords.top()); 
//		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);			  
//		gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
//		gl.glVertex3f(fTextureWidth, 0.0f, fTmpZLayerValue);			 
//		gl.glTexCoord2f(texCoords.right(), 0); 
//		gl.glVertex3f(fTextureWidth, fTextureHeight, fTmpZLayerValue);
//		gl.glTexCoord2f(0, 0); 
//		gl.glVertex3f(0.0f, fTextureHeight, fTmpZLayerValue);
//		gl.glEnd();	
//		
//		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
//		gl.glLineWidth(1);
//		gl.glBegin(GL.GL_LINE_STRIP); 
//		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);; 
//		gl.glVertex3f(fTextureWidth, 0.0f, fTmpZLayerValue);
//		gl.glVertex3f(fTextureWidth, fTextureHeight, fTmpZLayerValue);
//		gl.glVertex3f(0.0f, fTextureHeight, fTmpZLayerValue);
//		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);; 				
//		gl.glEnd();
//
//		//gl.glTranslatef(texCoords.right() * 0.0025f, 0.0f, 0.0f);
//		
////
////		if (bAcordionDirection == false)
////		{
////			gl.glRotated(-140, 0, 1, 0);
////			bAcordionDirection = true;
////		}
////		else
////		{
////			gl.glRotated(140, 0, 1, 0);				
////			bAcordionDirection = false;
////		}
////		
//		refPathwayTexture.disable();
//	}
	
//	public void setTargetPathwayId(final int iTargetPathwayId) {
//		
//		refCurrentPathway = 
//			refGeneralManager.getSingelton().getPathwayManager().
//				getCurrentPathway();
//		
//		if (refCurrentPathway == null) 
//		{
//			refGeneralManager.getSingelton().getLoggerManager().logMsg(
//					"GLCanvasPathway2D.setPathwayId(" +
//					iTargetPathwayId + ") failed, because Pathway does not exist!");
//
//			return;
//		}
//		
//		refGeneralManager.getSingelton().getLoggerManager().logMsg(
//				"GLCanvasPathway2D.setPathwayId(" +
//				iTargetPathwayId + ") done!");
//	}
	
	public void update(GLAutoDrawable canvas) {

	}

	public void destroy() {
		
		System.err.println(" GLCanvasPathway2D.destroy(GLCanvas canvas)");
	}

	public void createVertex(IPathwayVertexRep vertexRep, Pathway refContainingPathway) {
		
		boolean bHighlightVertex = false;
		Color tmpNodeColor = null;
		
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
			bHighlightVertex = true;
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

			// if vertex should be highlighted then the highlight color is taken.
			// else the standard color for that node type is taken.
			if (bHighlightVertex == true)
			{
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
			
				gl.glColor4f(tmpNodeColor.getRed(), tmpNodeColor.getGreen(), tmpNodeColor.getBlue(), 1.0f);
			}
			else 
			{
				gl.glColor4f(131f/255f,111f/255f,1.0f, 1f);
			}
			
			fPathwayNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			fPathwayNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;

			fillNodeDisplayList();
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
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				gl.glColor4f(tmpNodeColor.getRed(), tmpNodeColor.getGreen(), tmpNodeColor.getBlue(), 1.0f);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			
			}
			else 
			{
				gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f); // green
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
				tmpNodeColor = refRenderStyle.getHighlightedNodeColor();
				gl.glColor4f(tmpNodeColor.getRed(), tmpNodeColor.getGreen(), tmpNodeColor.getBlue(), 1.0f);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
		
			}
			else 
			{
				gl.glColor4f(0.53f, 0.81f, 1.0f, 1.0f); // ligth blue
				gl.glCallList(iEnzymeNodeDisplayListId);
			}
		}

		gl.glTranslatef(-fCanvasXPos, -fCanvasYPos, -fZLayerValue);
		
//		if (bIsRefreshRendering == false)
			gl.glPopName();
	}
	
	public void createEdge(
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
	
	protected void replacePathway(Pathway refPathwayToReplace, int iNewPathwayId) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": replacePathway(): Replace pathway "+refPathwayToReplace.getPathwayID() 
				+" with " +iNewPathwayId,
				LoggerType.MINOR_ERROR );
		
		// Throw away old pathway from local hash maps
		refHashPathway2ModelMatrix.remove(refPathwayToReplace);
//		refHashPathway2DisplayListNodeId.remove(refPathwayToReplace);
//		refHashPathwayToTexture.remove(refPathwayToReplace);
		refHashPathwayToZLayerValue.remove(refPathwayToReplace);
		
		iArHighlightedVertices.clear();
		
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
		
		String sPathwayFilePath = "";
		
		//-----------------------
		//Just for testing!
		if (iNewPathwayId < 10)
		{
			sPathwayFilePath = "map0000" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 100 && iNewPathwayId >= 10)
		{
			sPathwayFilePath = "map000" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 1000 && iNewPathwayId >= 100)
		{
			sPathwayFilePath = "map00" + Integer.toString(iNewPathwayId);
		}
		else if (iNewPathwayId < 10000 && iNewPathwayId >= 1000)
		{
			sPathwayFilePath = "map0" + Integer.toString(iNewPathwayId);
		}
		
		sPathwayFilePath = "data/XML/pathways/" + sPathwayFilePath +".xml";		
		
		refGeneralManager.getSingelton().
			getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
		//----------------------------
		
		initPathwayData();
		
		//FIXME: not good to rebuild all display lists
		buildPathwayDisplayList();	
	}
	
	protected void connectVertices(IPathwayVertexRep refVertexRep1, 
			IPathwayVertexRep refVertexRep2) {

		float fZLayerValue1 = 0.0f; 
		float fZLayerValue2 = 0.0f;
		Pathway refTmpPathway = null;
		Iterator<Pathway> iterDrawnPathways = null;
		Texture refPathwayTexture = null;
		float fCanvasXPos1 = 0.0f;
		float fCanvasYPos1 = 0.0f;
		float fCanvasXPos2 = 0.0f;
		float fCanvasYPos2 = 0.0f;
		
		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		
		buildEnzymeNodeDisplayList();
		buildHighlightedEnzymeNodeDisplayList();
		buildCompoundNodeDisplayList();
		buildHighlightedCompoundNodeDisplayList();
		
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
	
	/**
	 * Method for rendering text in OpenGL.
	 * TODO: Move method to some kind of GL Utility class.
	 * 
	 * @param gl
	 * @param showText
	 * @param fx<
	 * @param fy
	 * @param fz
	 */
	public void renderText(final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		final float fFontSizeOffset = 0.01f;

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
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, showText);    
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

	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvasDirector()
	 */
	public final IGLCanvasDirector getGLCanvasDirector() {
		
		return openGLCanvasDirector;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvas()
	 */
	public final GLAutoDrawable getGLCanvas() {
		
		return canvas;
	}
	
	public final void setOriginRotation( final Vec3f origin,	
			final Vec4f rotation ) {
		
		this.origin   = origin;
		this.rotation = rotation;
	}

	public void loadImageMapFromFile(String sImagePath) {

		// TODO Auto-generated method stub
		
	}

	public void setNeighbourhoodDistance(int iNeighbourhoodDistance) {

		// TODO Auto-generated method stub
		
	}

	public void zoomOrig() {

		// TODO Auto-generated method stub
		
	}

	public void zoomIn() {

		// TODO Auto-generated method stub
		
	}

	public void zoomOut() {

		// TODO Auto-generated method stub
		
	}

	public void showOverviewMapInNewWindow(Dimension dim) {

		// TODO Auto-generated method stub
		
	}

	public void showHideEdgesByType(boolean bShowEdges, EdgeType edgeType) {

		// TODO Auto-generated method stub
		
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.swt.pathway.IPathwayGraphView#showBackgroundOverlay(boolean)
	 */
	public void showBackgroundOverlay(boolean bTurnOn) {

		System.err.println("SHOW BACKGROUND OVERLAY: " + bTurnOn);	
		
		bShowPathwayTexture = bTurnOn;
		
		//buildPathwayDisplayList();
		getGLCanvas().display();
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
	public void loadBackgroundOverlayImage(String sPathwayImageFilePath, 
			Pathway refTexturedPathway) {
		
		Texture refPathwayTexture;
		
		try
		{
			refPathwayTexture = TextureIO.newTexture(new File(sPathwayImageFilePath), false);
			//refPathwayTexture.bind();
			//refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			//refPathwayTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			refHashPathwayToTexture.put(refTexturedPathway, refPathwayTexture);
			
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadBackgroundOverlay(): Loaded Texture for Pathway" +refTexturedPathway.getTitle(),
					LoggerType.VERBOSE );
			
		} catch (Exception e)
		{
			System.out.println("Error loading texture " + sPathwayImageFilePath);
			e.printStackTrace();
		}
	}
	
	public void resetPathway() {

		// TODO Auto-generated method stub
		
	}

	public void initView() {
		
	}

	public void displayChanged(GLAutoDrawable drawable, final boolean modeChanged, final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateSelection(Object eventTrigger, SetSelection selectionSet) {
		
		refGeneralManager.getSingelton().logMsg(
				"OpenGL Pathway update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		// Clear old selected vertices
		iArHighlightedVertices.clear();
		// Clear old neighborhood distances
		//iArSelectionStorageNeighborDistance.clear();
		
		// Read selected vertex IDs
		int[] iArSelectedElements = selectionSet.getSelectionIdArray();
		
		// Read neighbor data
		int[] iArSelectionNeighborDistance = selectionSet.getOptionalDataArray();
		
		for (int iSelectedVertexIndex = 0; 
			iSelectedVertexIndex < ((IStorage)selectionSet.getStorageByDimAndIndex(0, 0)).getSize(StorageType.INT);
			iSelectedVertexIndex++)
		{			
			iArHighlightedVertices.add(refGeneralManager.getSingelton().getPathwayElementManager().
					getVertexLUT().get(iArSelectedElements[iSelectedVertexIndex]).getVertexRepByIndex(0));
			
			iArSelectionStorageNeighborDistance.add(iArSelectionNeighborDistance[iSelectedVertexIndex]);
		}
		
		getGLCanvas().display();
	}
	
	public final boolean isInitGLDone() 
	{
		return this.bInitGLcanvawsWasCalled;
	}
	
	protected final void setInitGLDone() 
	{
		if ( bInitGLcanvawsWasCalled ) {
			System.err.println(" called setInitGLDone() for more than once! " + 
					this.getClass().getSimpleName()  +
					" " + this.getId());
		}
		else 
		{
			System.out.println(" called setInitGLDone() " + 
					this.getClass().getSimpleName() + 
					" " + this.getId() );
		}
		bInitGLcanvawsWasCalled = true;
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		//FIXME this is just a work around! derive from AGLCanvasUser or AGLCanvasUser_OriginRotation!
		this.render( drawable );
		
	}
	
	protected void pickObjects(GL gl) {

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount;
		int viewport[] = new int[4];

		// Deselect all highlighted nodes.
		iArHighlightedVertices.clear();
		
		// if (button != GLUT_LEFT_BUTTON || state != GLUT_DOWN) return;

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
				5.0, 5.0, viewport, 0); // pick width and height is set to 10 (i.e. picking tolerance)

//		glu.gluPerspective(17.0f, (float) (viewport[2]-viewport[0]) / // magic occurs here!!
//				(float) (viewport[3]-viewport[1]), 0.1f, 100.0f);
		
		float h = (float) (float) (viewport[3]-viewport[1]) / 
			(float) (viewport[2]-viewport[0]);
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

//		System.out.println("Viewport: " +viewport[0] +" " +viewport[1] +" " +viewport[2] +" " +viewport[3]);
//		System.out.println("Picked point: " +pickPoint.x +" " +pickPoint.y);
		
		renderPart(gl, GL.GL_SELECT);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		processHits(iHitCount, iArPickingBuffer);
		
		// Reset picked point 
		pickPoint = null;
	}
	
	protected void processHits(int iHitCount, int iArPickingBuffer[]) {

		//System.out.println("Number of hits: " +iHitCount);
		
		int iNames = 0;
		int iPtr = 0;
		int i = 0;
		int iPickedPathwayDisplayListNodeId = 0;
		IPathwayVertexRep pickedVertexRep = null;
		Iterator<PathwayVertex> iterIdenticalVertices = null;

		for (i = 0; i < iHitCount; i++)
		{
			iNames = iArPickingBuffer[iPtr];
			//System.out.println(" number of names for this hit = " + iNames);
			iPtr++;
			//System.out.println(" z1 is  " + (float) iArPickingBuffer[iPtr] / 0x7fffffff);
			iPtr++;
			//System.out.println(" z2 is " + (float) iArPickingBuffer[iPtr] / 0x7fffffff);
			iPtr++;
			//System.out.println(" names are ");
			
			if (iNames != 2)
				return;
			
//			for (int j = 0; j < iNames; j++)
//			{
				System.out.println("Pathway pick node ID:" + iArPickingBuffer[iPtr]);

				iPickedPathwayDisplayListNodeId = iArPickingBuffer[iPtr];
				
				iPtr++;

				//System.out.println("Object pick ID: " + iArPickingBuffer[iPtr]);

				pickedVertexRep = refHashPickID2VertexRep.get(iArPickingBuffer[iPtr]);
				
				if (pickedVertexRep == null)
					return;
				
				// Update the currently selected pathway
				refPathwayUnderInteraction = refHashDisplayListNodeId2Pathway.get(
						iPickedPathwayDisplayListNodeId);
				
				// Check if the clicked node is a pathway
				// If this is the case the current pathway will be replaced by the clicked one.
				if(pickedVertexRep.getVertex().getVertexType().equals(PathwayVertexType.map))
				{
					int iNewPathwayId = new Integer(pickedVertexRep.getVertex().getElementTitle().substring(8));
					replacePathway(refPathwayUnderInteraction, iNewPathwayId);
					return;
				}
				
				if (!iArHighlightedVertices.contains(pickedVertexRep))
				{
					// Clear currently highlighted vertices when new node was selected
					if(!iArHighlightedVertices.isEmpty())
						iArHighlightedVertices.clear();
					
					iArHighlightedVertices.add(pickedVertexRep);
					
					// Convert to int[]
					int[] iArTmp = new int[iArHighlightedVertices.size()];
					for(int index = 0; index < iArHighlightedVertices.size(); index++)
						iArTmp[index] = iArHighlightedVertices.get(index).getVertex().getElementId();
					
					updateSelectionSet(iArTmp, 
							new int[0], new int[0]);
					
					refGeneralManager.getSingelton().logMsg(
							"OpenGL Pathway object selected: " +pickedVertexRep.getName(),
							LoggerType.VERBOSE);
				}
				else
				{
					iArHighlightedVertices.remove(pickedVertexRep);
	
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
							"OpenGL Pathway object unselected: " +pickedVertexRep.getName(),
							LoggerType.VERBOSE);
				}
				
				// FIXME: not very efficient
				// All display lists are newly created
				iArPathwayNodeDisplayListIDs.clear();
				iArPathwayEdgeDisplayListIDs.clear();
				buildPathwayDisplayList();
				
//				gl.glNewList(iPickedPathwayDisplayListNodeId, GL.GL_COMPILE);	
//				extractVertices(refPathwayUnderInteraction);
//				gl.glEndList();
								
				loadNodeInformationInBrowser(pickedVertexRep.getVertex().getVertexLink());
//			}
		}
	}	
	
//    private void drawBezierCurve(GL gl) {
//
//		final int nbCtrlPoints = 4;
//		final int sizeCtrlPoints = nbCtrlPoints * 3;
//
//		float a = 1.0f;
//		float b = 0.f;
//		float ctrlPoints[] =
//		{ -a, -a, 0f, 
//		  -a/2, +a, 2f, 
//		  +a, +2*a, 1f, 
//		  +2*a, -a/4, -1f };
//
//		// Check if the ctrl points array has a legal size.
//		if (ctrlPoints.length != sizeCtrlPoints)
//		{
//			System.out.println("ERROR ctrlPoints\n");
//		}
//		;
//
//		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlPoints, 0);
//		gl.glEnable(GL.GL_MAP1_VERTEX_3);
//
//		// Draw ctrlPoints.
//		gl.glBegin(GL.GL_POINTS);
//		{
//			for (int i = 0; i < sizeCtrlPoints; i += 3)
//			{
//				gl.glVertex3f(ctrlPoints[i], ctrlPoints[i + 1],
//						ctrlPoints[i + 2]);
//			}
//		}
//		gl.glEnd();
//
//		// Draw courve.
//		gl.glBegin(GL.GL_LINE_STRIP);
//		{
//			for (float v = 0; v <= 1; v += 0.01)
//			{
//				gl.glEvalCoord1f(v);
//			}
//		}
//		gl.glEnd();
//	}
    
    protected abstract void highlightIdenticalNodes();

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
}
