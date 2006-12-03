package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;
import cerberus.view.gui.opengl.GLCanvasStatics;

/**
 * @author Marc Streit
 *
 */
public class GLCanvasPathway2D  
extends APathwayGraphViewRep
implements IGLCanvasUser {
		  	 
	private float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	private static final int X = GLCanvasStatics.X;
	private static final int Y = GLCanvasStatics.Y;
//	private static final int Z = GLCanvasStatics.Z;
	private static final int MIN = GLCanvasStatics.MIN;
	private static final int MAX = GLCanvasStatics.MAX;
//	private static final int OFFSET = GLCanvasStatics.OFFSET;
	
	float fScalingFactorX = 0.0f;
	float fScalingFactorY = 0.0f;
	
	protected int iVertexRepIndex = 0;

	protected GLAutoDrawable canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	protected Vec3f origin;
	
	protected Vec4f rotation;
	
	protected GL gl;
	
	protected float fZLayerValue = 0.0f;
	
	protected int iPathwayDisplayListId = 0;
	
	protected int iEnzymeNodeDisplayListId = 0;
	
	protected int iCompoundNodeDisplayListId = 0;

	protected int iPathwayNodeDisplayListId = 0;
	
	protected float fPathwayNodeWidth = 0.0f;
	
	protected float fPathwayNodeHeight = 0.0f;
	
	protected ArrayList<PathwayVertex> iArSelectionStorageVertexIDs;
	
	protected ArrayList<Integer> iArSelectionStorageNeighborDistance;
	
	protected boolean bCanvasInitialized = false;
	
	protected HashMap<Pathway, Float> refHashPathwayToZLayerValue;
	
	/**
	 * Pathway that is currently under user interaction in the 2D pathway view.
	 */
	protected Pathway refBasicPathway;
		
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	public GLCanvasPathway2D( final IGeneralManager refGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
				
		super(refGeneralManager, -1, iParentContainerId, "");
		
		openGLCanvasDirector =
			refGeneralManager.getSingelton().
				getViewGLCanvasManager().getGLCanvasDirector( iParentContainerId );
		
		this.canvas = openGLCanvasDirector.getGLCanvas();
		
		viewingFrame = new float [2][2];
		
		viewingFrame[X][MIN] = -1.0f;
		viewingFrame[X][MAX] = 1.0f; 
		viewingFrame[Y][MIN] = -1.0f; 
		viewingFrame[Y][MAX] = 1.0f; 
		
		fScalingFactorX = 
			((viewingFrame[X][MAX] - viewingFrame[X][MIN]) / 1000.0f) * 1.5f;
		
		fScalingFactorY = 
			((viewingFrame[Y][MAX] - viewingFrame[Y][MIN]) / 1000.0f) * 1.5f;
		
		iArSelectionStorageVertexIDs = new ArrayList<PathwayVertex>();
		iArSelectionStorageNeighborDistance = new ArrayList<Integer>();
		
		refHashPathwayToZLayerValue = new HashMap<Pathway, Float>();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init( GLAutoDrawable canvas ) {

		// FIXME: usually this should not be needed
		// Ask michael why init in CanvasForwarder is called twice!
		if (bCanvasInitialized == true)
			return;
		
		this.gl = canvas.getGL();
//		
//		System.out.println("Init Pathway GL Canvas");
		
		// General GL settings
//		gl.glEnable(GL.GL_BLEND);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		//gl.glEnable(GL.GL_DEPTH_TEST);
//		gl.glDepthFunc(GL.GL_LEQUAL);
//		gl.glBlendEquation(GL.GL_MAX);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);
	
		// Clearing window and set background to WHITE
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);		
//	
		float[] fMatSpecular = { 1.0f, 1.0f, 1.0f, 1.0f};
		float[] fMatShininess = {25.0f}; 
		float[] fLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};
		float[] fWhiteLight = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] fModelAmbient = {0.2f, 0.2f, 0.2f, 1.0f};
		
//		gl.glShadeModel(GL.GL_SMOOTH);
//		gl.glEnable(GL.GL_COLOR_MATERIAL);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, fMatSpecular, 0);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, fMatShininess, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, fLightPosition, 0);		
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, fWhiteLight, 0);
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, fWhiteLight, 0);
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, fModelAmbient, 0);
			
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);	
		
		refCurrentPathway = refGeneralManager.getSingelton().
			getPathwayManager().getCurrentPathway();
		
		if (refCurrentPathway == null)
		{
			System.err.println("No pathway loaded for OpenGL drawing!");
			return;
		}
		
		buildPathwayDisplayList();	
		
		bCanvasInitialized = true;
	}	
	
	public final void render(GLAutoDrawable canvas) {
		
		this.gl = canvas.getGL();
		
		/* Clear The Screen And The Depth Buffer */
		gl.glPushMatrix();

		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
		gl.glRotatef( rotation.x(), 
				rotation.y(),
				rotation.z(),
				rotation.w() );
		
		renderPart( gl );

		gl.glPopMatrix();		
	}

	public void renderPart(GL gl) {
		
		this.gl = gl;
		
		gl.glCallList(iPathwayDisplayListId);
		Color nodeColor = refRenderStyle.getHighlightedNodeColor();
		
		//Draw selected pathway nodes
		if (!iArSelectionStorageVertexIDs.isEmpty())
		{
			Pathway refTmpPathway = null;
			PathwayVertex refCurrentVertex = null;
			PathwayVertex refTmpVertex = null;
			IPathwayVertexRep refCurrentVertexRep = null;
			
			Iterator<PathwayVertex> iterSelectedVertices = 
				iArSelectionStorageVertexIDs.iterator();
			
			Iterator<Integer> iterSelectedNeighborDistance = 
				iArSelectionStorageNeighborDistance.iterator();			
			
			Iterator<PathwayVertex> iterIdenticalVertices = null;
			
			Iterator<Pathway> iterDrawnPathways = null;
			
			while(iterSelectedVertices.hasNext())
			{	
				//FIXME: should not be statically 0 
				fZLayerValue = 0.0f;
				
//				switch (iterSelectedNeighborDistance.next())
//				{
//				case 1:
//					nodeColor = refRenderStyle.getNeighborhoodNodeColor_1();
//					break;
//				case 2:
//					nodeColor = refRenderStyle.getNeighborhoodNodeColor_2();
//					break;
//				case 3:
//					nodeColor = refRenderStyle.getNeighborhoodNodeColor_3();
//					break;						
//				}
				
				refCurrentVertex = iterSelectedVertices.next();
				createVertex(refCurrentVertex.getVertexRepByIndex(iVertexRepIndex), 
						true, nodeColor);
				
				iterDrawnPathways = refHashPathwayToZLayerValue.keySet().iterator();
				
				while(iterDrawnPathways.hasNext())
				{
					refTmpPathway = iterDrawnPathways.next();
					
					// Hightlight all identical nodes
					iterIdenticalVertices = refGeneralManager.getSingelton().
							getPathwayElementManager().getPathwayVertexListByName(
									refCurrentVertex.getElementTitle()).iterator();
					
					while(iterIdenticalVertices.hasNext())
					{
						refTmpVertex = iterIdenticalVertices.next();
						
						if (refTmpPathway.isVertexInPathway(refTmpVertex) == true)
						{
							fZLayerValue = refHashPathwayToZLayerValue.get(refTmpPathway);
						
							refCurrentVertexRep = refTmpVertex.
								getVertexRepByIndex(iVertexRepIndex);
							
							if (refCurrentVertexRep != null)
							{
								createVertex(refCurrentVertexRep, true, nodeColor);
								
								if (!refTmpPathway.equals(refBasicPathway))
								{
									connectVertices(refCurrentVertexRep, 
											refCurrentVertex.getVertexRepByIndex(iVertexRepIndex));
								}
							}
						}
					}
				}
			}
		}		
	}
	
	public void setTargetPathwayId(final int iTargetPathwayId) {
		
		refCurrentPathway = 
			refGeneralManager.getSingelton().getPathwayManager().
				getCurrentPathway();
		
		if (refCurrentPathway == null) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"GLCanvasPathway2D.setPathwayId(" +
					iTargetPathwayId + ") failed, because Pathway does not exist!");

			return;
		}
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				"GLCanvasPathway2D.setPathwayId(" +
				iTargetPathwayId + ") done!");
	}
	
	public void update(GLAutoDrawable canvas) {
		
	}

	public void destroy() {
		
		System.err.println(" GLCanvasPathway2D.destroy(GLCanvas canvas)");
	}

	public void createVertex(IPathwayVertexRep vertexRep, 
			boolean bHightlighVertex, Color nodeColor) {
		
		float fCanvasXPos = viewingFrame[X][MIN] + 
		vertexRep.getXPosition() * fScalingFactorX;
		float fCanvasYPos = viewingFrame[Y][MIN] + 
		vertexRep.getYPosition() * fScalingFactorY;
	
		String sShapeType = vertexRep.getShapeType();

		gl.glTranslatef(fCanvasXPos, fCanvasYPos, fZLayerValue);

		// Pathway link
		if (sShapeType.equals("roundrectangle"))
		{				
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - fCanvasWidth + 0.02f, 
//					fCanvasYPos + 0.02f, 
//					-0.001f);

			// if vertex should be highlighted then the color parameter is taken.
			// else the standard color for that node type is taken.
			if (bHightlighVertex == true)
				gl.glColor4f(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), 1.0f);
			else 
				gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
						
			gl.glCallList(iPathwayNodeDisplayListId);
		}
		// Compounds
		else if (sShapeType.equals("circle"))
		{				
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - 0.04f, 
//					fCanvasYPos - fCanvasHeight, 
//					-0.001f);
			
			if (bHightlighVertex == true)
				gl.glColor4f(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), 1.0f);
			else 
				gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f); // green
			
			gl.glCallList(iCompoundNodeDisplayListId);
		}	
		// Enzyme
		else if (sShapeType.equals("rectangle"))
		{	
//			renderText(vertexRep.getName(), 
//					fCanvasXPos - fCanvasWidth + 0.02f, 
//					fCanvasYPos + 0.02f, 
//					-0.001f);
		
			if (bHightlighVertex == true)
				gl.glColor4f(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), 1.0f);
			else 
				gl.glColor4f(0.53f, 0.81f, 1.0f, 1.0f); // ligth blue
			
			
			gl.glCallList(iEnzymeNodeDisplayListId);
		}

		gl.glTranslatef(-fCanvasXPos, -fCanvasYPos, -fZLayerValue);
		
//		int[] buffers = new int[2];
//		int iNumVertices = 8;
////			if (iLayerIndex == 1)
//		{
//		loadPathwayFromFile(strPathwayPaths[0]);
//	}
//	else if (iLayerIndex == 2)
//	{
//		loadPathwayFromFile(strPathwayPaths[1]);
//	}
//	
//	refCurrentPathway = refGeneralManager.getSingelton().
//		getPathwayManager().getCurrentPathway();
//
//	if (refCurrentPathway == null)
//	{
//		return;
//	}
//		FloatBuffer vertexBuffer = BufferUtil.newFloatBuffer(3 * iNumVertices);
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(-1.0f);
//		
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(-1.0f);
//
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(-1.0f);
//		
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(-1.0f);
//		
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(1.0f);
//		
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(1.0f);
//		
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(1.0f);
//		
//		vertexBuffer.put(-1.0f);
//		vertexBuffer.put(1.0f);
//		vertexBuffer.put(1.0f);
//
//		IntBuffer indexBuffer = BufferUtil.newIntBuffer(24);
//		indexBuffer.put(0);
//		indexBuffer.put(1);
//		indexBuffer.put(2);
//		indexBuffer.put(3);
//		
//		indexBuffer.put(4);
//		indexBuffer.put(7);
//		indexBuffer.put(6);
//		indexBuffer.put(5);
//		
//		indexBuffer.put(0);
//		indexBuffer.put(4);
//		indexBuffer.put(5);
//		indexBuffer.put(1);
//		
//		indexBuffer.put(3);
//		indexBuffer.put(2);
//		indexBuffer.put(6);
//		indexBuffer.put(7);
//	
//		indexBuffer.put(0);
//		indexBuffer.put(3);
//		indexBuffer.put(7);
//		indexBuffer.put(4);
//		
//		indexBuffer.put(1);
//		indexBuffer.put(5);
//		indexBuffer.put(6);
//		indexBuffer.put(2);
//		
//		vertexBuffer.flip();
//		indexBuffer.flip();
//		
//		//gl.glEnable( GL.GL_DEPTH_TEST );
//		gl.glGenBuffersARB(buffers.length, buffers, 0);
//		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, buffers[0]);
//		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, 
//				vertexBuffer.capacity() * BufferUtil.SIZEOF_FLOAT, 
//				vertexBuffer, 
//				GL.GL_STATIC_DRAW_ARB);
//		
//		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
//		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
//		
//		gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, buffers[1]);
//		gl.glBufferDataARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, 
//				indexBuffer.capacity() * BufferUtil.SIZEOF_INT,
//				indexBuffer,
//				GL.GL_STATIC_DRAW_ARB);
//		
//		gl.glDrawElements(GL.GL_QUADS, 24, GL.GL_UNSIGNED_BYTE, 0);
//		
//		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
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
		vertexRep1.getXPosition() * fScalingFactorX;
		float fCanvasYPos1 = viewingFrame[Y][MIN] + 
		vertexRep1.getYPosition() * fScalingFactorY;

		float fCanvasXPos2 = viewingFrame[X][MIN] + 
		vertexRep2.getXPosition() * fScalingFactorX;
		float fCanvasYPos2 = viewingFrame[Y][MIN] + 
		vertexRep2.getYPosition() * fScalingFactorY;
		
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
	
	protected void buildPathwayDisplayList() {
		
		// Creating display list for pathways
		iPathwayDisplayListId = gl.glGenLists(1);
		
		buildEnzymeNodeDisplayList();
		buildPathwayNodeDisplayList();
		buildCompoundNodeDisplayList();
		
		gl.glNewList(iPathwayDisplayListId, GL.GL_COMPILE);	
		
		String[] strPathwayPaths = new String[2];
		strPathwayPaths[0] = "data/XML/pathways/map00260.xml";
		strPathwayPaths[1] = "data/XML/pathways/map00272.xml";
		
		refBasicPathway = refCurrentPathway;
		
	    // Just for testing!
		for (int iLayerIndex = 0; iLayerIndex < 3; iLayerIndex++)
		{
			fZLayerValue = iLayerIndex;
			
			if (iLayerIndex == 1)
			{
				loadPathwayFromFile(strPathwayPaths[0]);
			}
			else if (iLayerIndex == 2)
			{
				loadPathwayFromFile(strPathwayPaths[1]);
			}
			
			refCurrentPathway = refGeneralManager.getSingelton().
				getPathwayManager().getCurrentPathway();
		
			if (refCurrentPathway == null)
			{
				return;
			}
			
			refHashPathwayToZLayerValue.put(refCurrentPathway, fZLayerValue);
			
			System.err.println("Current pathway: "+refCurrentPathway.getTitle());
			
				
			// Draw pathway "sheet" border
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glBegin(GL.GL_LINE_STRIP);						
				gl.glVertex3f(-1.0f, -1.0f, fZLayerValue + 0.01f);					
				gl.glVertex3f(-1.0f, 1.5f, fZLayerValue + 0.01f);
				gl.glVertex3f(2.5f, 1.5f, fZLayerValue + 0.01f);
				gl.glVertex3f(2.5f, -1.0f, fZLayerValue + 0.01f);
				gl.glVertex3f(-1.0f, -1.0f, fZLayerValue + 0.01f);
			gl.glEnd();	
			
			// Draw pathway layer surface
			gl.glTranslatef(0.0f, 0.0f, fZLayerValue + 0.01f);
			gl.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
			gl.glRectf(-1.0f, -1.0f, 2.5f, 1.5f);
			gl.glTranslatef(0.0f, 0.0f, -fZLayerValue - 0.01f);
			
			extractVertices();
			extractEdges();	
			finishGraphBuilding();
		}
		
		gl.glEndList();
	}
	
	protected void buildEnzymeNodeDisplayList() {

		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getEnzymeNodeWidth() * fScalingFactorX / 2.0f;
		fPathwayNodeHeight = 
			refRenderStyle.getEnzymeNodeHeight() * fScalingFactorY / 2.0f;

		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		
		fillNodeDisplayList();
		
        gl.glEndList();
	}
	
	protected void buildCompoundNodeDisplayList() {

		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);
		
		fPathwayNodeWidth = 
			refRenderStyle.getCompoundNodeWidth() * fScalingFactorX / 2.0f;
		fPathwayNodeHeight = 
			refRenderStyle.getCompoundNodeHeight() * fScalingFactorY / 2.0f;
		
		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		
		fillNodeDisplayList();
		
        gl.glEndList();
	}
	
	protected void buildPathwayNodeDisplayList() {

		// Creating display list for node cube objects
		iPathwayNodeDisplayListId = gl.glGenLists(1);
	
		fPathwayNodeWidth = 
			refRenderStyle.getPathwayNodeWidth() * fScalingFactorX / 2.0f;
		fPathwayNodeHeight = 
			refRenderStyle.getPathwayNodeHeight() * fScalingFactorY / 2.0f;

		gl.glNewList(iPathwayNodeDisplayListId, GL.GL_COMPILE);
	
		fillNodeDisplayList();
		
        gl.glEndList();

	}
	
	protected void fillNodeDisplayList() {
		
		gl.glBegin(GL.GL_QUADS);
		 
        // Front face
		gl.glNormal3f( 0.0f, 0.0f, 1.0f);	
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Top Right Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Top Left Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Bottom Left Of The Quad (Front)
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Bottom Right Of The Quad (Front)

        // Back face
        gl.glNormal3f( 0.0f, 0.0f,-1.0f);		
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Left Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Right Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Right Of The Quad (Back)
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Left Of The Quad (Back)

		// Top face
        gl.glNormal3f( 0.0f, 1.0f, 0.0f);	
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Right Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Left Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Bottom Left Of The Quad (Top)
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Bottom Right Of The Quad (Top)

        // Bottom face
        gl.glNormal3f( 0.0f,-1.0f, 0.0f);	
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Top Right Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Top Left Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Left Of The Quad (Bottom)
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Right Of The Quad (Bottom)

        // Right face
        gl.glNormal3f( 1.0f, 0.0f, 0.0f);	
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Right Of The Quad (Right)
        gl.glVertex3f( fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Top Left Of The Quad (Right)
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Bottom Left Of The Quad (Right)
        gl.glVertex3f( fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Right Of The Quad (Right)
        
        // Left face
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);	
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.02f);			// Top Right Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.02f);			// Top Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight,-0.02f);			// Bottom Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth,-fPathwayNodeHeight, 0.02f);			// Bottom Right Of The Quad (Left)
       
        gl.glEnd();
	}
	
	protected void connectVertices(IPathwayVertexRep refVertexRep1, 
			IPathwayVertexRep refVertexRep2) {

		float fZLayerValue1 = 0.0f; 
		float fZLayerValue2 = 0.0f;
		Pathway refTmpPathway = null;
		Iterator<Pathway> iterDrawnPathways = null;
		
		iterDrawnPathways = refHashPathwayToZLayerValue.keySet().iterator();
		
		while(iterDrawnPathways.hasNext())
		{
			refTmpPathway = iterDrawnPathways.next();

//			if(!refTmpPathway.equals(refCurrentPathway))
//			{			
				if(refTmpPathway.isVertexInPathway(refVertexRep1.getVertex()) == true)
				{
					fZLayerValue1 = refHashPathwayToZLayerValue.get(refTmpPathway);
				}
				
				if(refTmpPathway.isVertexInPathway(refVertexRep2.getVertex()) == true)
				{
					fZLayerValue2 = refHashPathwayToZLayerValue.get(refTmpPathway);
				}
//			}
		}
		
		float fCanvasXPos1 = viewingFrame[X][MIN] + 
		refVertexRep1.getXPosition() * fScalingFactorX;
		float fCanvasYPos1 = viewingFrame[Y][MIN] + 
		refVertexRep1.getYPosition() * fScalingFactorY;

		float fCanvasXPos2 = viewingFrame[X][MIN] + 
		refVertexRep2.getXPosition() * fScalingFactorX;
		float fCanvasYPos2 = viewingFrame[Y][MIN] + 
		refVertexRep2.getYPosition() * fScalingFactorY;
		
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		gl.glBegin(GL.GL_LINES);		
			gl.glVertex3f(fCanvasXPos1, fCanvasYPos1, fZLayerValue1); 
			gl.glVertex3f(fCanvasXPos2, fCanvasYPos2, fZLayerValue2);					
		gl.glEnd();
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
	
	public final Vec3f getOrigin( ) {
		return this.origin;
	}
	
	public final Vec4f getRoation( ) {
		return this.rotation;
	}

	public void setPathwayId(int iPathwayId) {

		// TODO Auto-generated method stub
		
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

	public void showBackgroundOverlay(boolean bTurnOn) {

		// TODO Auto-generated method stub
		
	}

	public void finishGraphBuilding() {

		// Draw title
		renderText(refCurrentPathway.getTitle(), 0.0f, -1.0f, fZLayerValue);	
	}

	public void loadBackgroundOverlayImage(String sPathwayImageFilePath) {

		// TODO Auto-generated method stub
		
	}

	public void resetPathway() {

		// TODO Auto-generated method stub
		
	}

	public void initView() {

		// TODO Auto-generated method stub
		
	}

	public void displayChanged(GLAutoDrawable drawable, final boolean modeChanged, final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateSelection(Object eventTrigger, ISet selectionSet) {

		// Clear old selected vertices
		iArSelectionStorageVertexIDs.clear();
		// Clear old neighborhood distances
		iArSelectionStorageNeighborDistance.clear();
		
		refGeneralManager.getSingelton().logMsg(
				"OpenGL Pathway update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		int[] iArSelectedElements = 
			((IStorage)selectionSet.getStorageByDimAndIndex(0, 0)).getArrayInt();
		
		int[] iArSelectionNeighborDistance = 
			((IStorage)selectionSet.getStorageByDimAndIndex(0, 1)).getArrayInt();
		
		for (int iSelectedVertexIndex = 0; 
			iSelectedVertexIndex < ((IStorage)selectionSet.getStorageByDimAndIndex(0, 0)).getSize(StorageType.INT);
			iSelectedVertexIndex++)
		{
			iArSelectionStorageVertexIDs.add(refGeneralManager.getSingelton().getPathwayElementManager().
					getVertexLUT().get(iArSelectedElements[iSelectedVertexIndex]));
			
			iArSelectionStorageNeighborDistance.add(iArSelectionNeighborDistance[iSelectedVertexIndex]);
			System.err.println(iArSelectionNeighborDistance[iSelectedVertexIndex]);
			
		}
		
		getGLCanvas().display();
		//getGLCanvas().repaint();
	}
}
