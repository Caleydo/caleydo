package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;

import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;

/**
 * @author Marc Streit
 *
 */
public class GLCanvasPathway2D  
extends APathwayGraphViewRep
implements IGLCanvasUser {
		  	 
	private float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int OFFSET = 2;
	
	float fScalingFactorX = 0.0f;
	float fScalingFactorY = 0.0f;
	
	protected int iVertexRepIndex = 0;

	protected GLAutoDrawable canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	protected Vec3f origin;
	
	protected Vec4f rotation;
	
	protected GL gl;
		
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
		
//		listHistogramData = new  LinkedList < HistogramData > ();
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
		
		System.err.println(" GLCanvasPathway2D.update(GLCanvas canvas)");	
//		
//		createHistogram( iCurrentHistogramLength );
	}

	public void destroy() {
		
		System.err.println(" GLCanvasPathway2D.destroy(GLCanvas canvas)");
	}

	public void createVertex(IPathwayVertexRep vertexRep) {

		Color tmpColor; 
		tmpColor = refRenderStyle.getEnzymeNodeColor();
		
		gl.glColor3f(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue());
		
		float fCanvasXPos = viewingFrame[X][MIN] + 
			vertexRep.getXPosition() * fScalingFactorX;
		float fCanvasYPos = viewingFrame[Y][MIN] + 
			vertexRep.getYPosition() * fScalingFactorY;
		
		float fCanvasWidth = (vertexRep.getWidth()/2.0f) * fScalingFactorX;
		float fCanvasHeight = (vertexRep.getHeight()/2.0f) * fScalingFactorY;
		
		gl.glRectf(fCanvasXPos - fCanvasWidth, 
				fCanvasYPos - fCanvasHeight, 
				fCanvasXPos + fCanvasWidth, 
				fCanvasYPos + fCanvasHeight);
	
		// draw border
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_STRIP);						
			gl.glVertex3f(fCanvasXPos - fCanvasWidth, fCanvasYPos - fCanvasHeight, 0.0f);					
			gl.glVertex3f(fCanvasXPos + fCanvasWidth, fCanvasYPos - fCanvasHeight, 0.0f);	
			gl.glVertex3f(fCanvasXPos + fCanvasWidth, fCanvasYPos + fCanvasHeight, 0.0f);
			gl.glVertex3f(fCanvasXPos - fCanvasWidth, fCanvasYPos + fCanvasHeight, 0.0f);
			gl.glVertex3f(fCanvasXPos - fCanvasWidth, fCanvasYPos - fCanvasHeight, 0.0f);			
		gl.glEnd();	
		
		renderText(vertexRep.getName(), 
				fCanvasXPos - fCanvasWidth, 
				fCanvasYPos - fCanvasHeight, 
				0.001f);
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
		
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);						
			gl.glVertex3f(fCanvasXPos1, fCanvasYPos1, 0.001f);
			gl.glVertex3f(fCanvasXPos2, fCanvasYPos2, 0.001f);					
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
		gl.glRasterPos2f(fx - fFontSizeOffset, fy - fFontSizeOffset);

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
		
	public final void render(GLAutoDrawable canvas)
	{
		GL gl = canvas.getGL();
		
		/* Clear The Screen And The Depth Buffer */
		gl.glPushMatrix();

		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
		gl.glRotatef( rotation.x(), 
				rotation.y(),
				rotation.z(),
				rotation.w() );
		
		renderPart( gl );

		gl.glPopMatrix();		
		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}

	public void renderPart(GL gl) {
		
		this.gl = gl;
		
		gl.glTranslatef(0.0f, 0.0f, 0.01f);
	
		// Clearing window and set background to WHITE
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		
		
		refCurrentPathway = refGeneralManager.getSingelton().
			getPathwayManager().getCurrentPathway();
		
		if (refCurrentPathway == null)
		{
			return;
		}
		
		extractVertices();
		extractEdges();
			
		finishGraphBuilding();
	}

	public void setPathwayId(int iPathwayId) {

		// TODO Auto-generated method stub
		
	}

	public void loadPathwayFromFile(String sFilePath) {

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
		renderText(refCurrentPathway.getTitle(), 0.0f, -1.0f, 0.0f);	
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
}
