package cerberus.view.gui.opengl.canvas.pathway;

import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.PathwayReactionEdge;
import cerberus.data.pathway.element.PathwayRelationEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;
import cerberus.view.gui.swt.pathway.IPathwayGraphView;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IPathwayElementManager;

/**
 * @author Marc Streit
 *
 */
public class GLCanvasPathway2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser {
		  	 
	private float [][] viewingFrame;
	
	protected float[][] fAspectRatio;
	
	Pathway refCurrentPathway;
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int MIN = 0;
	public static final int MAX = 1;
	public static final int OFFSET = 2;
	
	float fScalingFactorX = 0.0f;
	float fScalingFactorY = 0.0f;
	
	protected int iVertexRepIndex = 0;

	/**
	 * Constructor
	 * 
	 * @param setGeneralManager
	 */
	public GLCanvasPathway2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
		
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
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
	
	@Override
	public void renderPart(GL gl)
	{
		gl.glTranslatef( 0, 0, 0.01f);
	
		displayPathway(gl);
	}
	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasPathway2D.update(GLCanvas canvas)");	
//		
//		createHistogram( iCurrentHistogramLength );
	}

	public void destroy()
	{
		System.err.println(" GLCanvasPathway2D.destroy(GLCanvas canvas)");
	}

	public void displayPathway(GL gl) {
 
		// Clearing window and set background to WHITE
//		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		
		
		// Draw title
		renderText(gl, refCurrentPathway.getTitle(), 0.0f, -1.0f, 0.0f);
		
		extractVertices(gl);
		extractEdges(gl);
	}
	
	protected void extractVertices(GL gl) {
		
	    Iterator<PathwayVertex> vertexIterator;
	    PathwayVertex vertex;
	    IPathwayVertexRep vertexRep;
		
        vertexIterator = refCurrentPathway.getVertexListIterator();
        while (vertexIterator.hasNext())
        {
        	vertex = vertexIterator.next();
        	vertexRep = vertex.getVertexRepByIndex(iVertexRepIndex);

        	if (vertexRep != null)
        	{
        		createVertex(gl, vertexRep);        	
        	}
        }   
	}
	
	protected void extractEdges(GL gl) {
		
		// Process relation edges
	    Iterator<PathwayRelationEdge> relationEdgeIterator;
        relationEdgeIterator = refCurrentPathway.getRelationEdgeIterator();
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
		
        vertexIterator = refCurrentPathway.getVertexListIterator();
	    
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
	
	protected void extractRelationEdges(GL gl, PathwayRelationEdge relationEdge) {
		
		// Direct connection between nodes
		if (relationEdge.getCompoundId() == -1)
		{
			createEdge(gl, relationEdge.getElementId1(), 
					relationEdge.getElementId2(), 
					false, 
					relationEdge);
		}
		// Edge is routed over a compound
		else 
		{
			createEdge(gl, relationEdge.getElementId1(), 
					relationEdge.getCompoundId(), 
					false, 
					relationEdge);
			
			if (relationEdge.getEdgeRelationType() 
					== EdgeRelationType.ECrel)
			{
    			createEdge(gl, relationEdge.getCompoundId(), 
    					relationEdge.getElementId2(), 
    					false,
    					relationEdge);
			}
			else
			{
    			createEdge(gl, relationEdge.getElementId2(),
    					relationEdge.getCompoundId(),
    					true,
    					relationEdge);
			}

		}
	}
	
	protected void extractReactionEdges(GL gl, PathwayReactionEdge reactionEdge, 
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
	
	
	protected void createVertex(GL gl, IPathwayVertexRep vertexRep) {
		
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		
		float fCanvasXPos = viewingFrame[X][MIN] + 
			vertexRep.getXPosition() * fScalingFactorX;
		float fCanvasYPos = viewingFrame[Y][MIN] + 
			vertexRep.getYPosition() * fScalingFactorY;
		
		gl.glRectf(fCanvasXPos - 0.07f, fCanvasYPos - 0.03f, 
				fCanvasXPos + 0.07f, fCanvasYPos + 0.03f);
		
		renderText(gl, 
				vertexRep.getName(), 
				fCanvasXPos - 0.07f, 
				fCanvasYPos - 0.03f, 
				0.001f);

	}
	
	protected void createEdge(GL gl,
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
		
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);						
			gl.glVertex2d(fCanvasXPos1, fCanvasYPos1);					
			gl.glVertex2d(fCanvasXPos2, fCanvasYPos2);					
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
	public void renderText( GL gl, 
			final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		final float fFontSizeOffset = 0.01f;

		GLUT glut = new GLUT();

		// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// gl.glLoadIdentity();
		// gl.glTranslatef(0.0f,0.0f,-1.0f);

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 1.0f);

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
}
