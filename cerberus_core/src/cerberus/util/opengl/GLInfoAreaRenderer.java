package cerberus.util.opengl;

//import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.org.apache.bcel.internal.generic.IALOAD;
import com.sun.org.apache.bcel.internal.generic.SALOAD;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
//import com.sun.opengl.*;
//import com.sun.opengl.util.j2d.TextRenderer;
import cerberus.manager.IGeneralManager;
import cerberus.util.mapping.GeneAnnotationMapper;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayManager;

public class GLInfoAreaRenderer {

	private IGeneralManager refGeneralManager;
	
	private float[] fArWorldCoordinatePosition;
	
	private float fScaleFactor = 0.0f;

	private GLStarEffectRenderer starEffectRenderer;
	
	private float fZValue = -1f;
	
	private GLPathwayManager refGLPathwayManager;
	
	private LinkedList<String> sLLMultipleGeneMappingID;
	
	private GeneAnnotationMapper geneAnnotationMapper;
	
	private float fHalfWidth = 0.5f;
	private float fHalfHeight = 0.2f;
	
	public GLInfoAreaRenderer(final IGeneralManager refGeneralManager,
			final GLPathwayManager refGLPathwayManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		fArWorldCoordinatePosition = new float[3];
		starEffectRenderer = new GLStarEffectRenderer();		
		
		this.refGLPathwayManager = refGLPathwayManager;
		
		sLLMultipleGeneMappingID = new LinkedList<String>();

		geneAnnotationMapper = 
			new GeneAnnotationMapper(refGeneralManager);
	}
	
    
    public void renderInfoArea(final GL gl,
    		final PathwayVertex pickedVertex) {
    	
    	if (fScaleFactor < 1.0)
    		fScaleFactor += 0.04;
    	
    	if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))
    		extractMultipleGeneMapping(pickedVertex);
    	
    	// Check if vertex has multiple mapping and draw info areas in star formation
    	if (pickedVertex.getElementTitle().contains(" ") 
    			&& pickedVertex.getElementTitle().contains("hsa"))
    	{
    		drawPickedObjectInfoStar(gl, pickedVertex);
    	}
    	// In case of single mapping draw singe info area
    	else
    	{
        	drawPickedObjectInfoSingle(gl, pickedVertex, true);
    	}
    }
	
    private void drawPickedObjectInfoSingle(final GL gl,
			final PathwayVertex pickedVertex,
			final boolean bDrawDisplaced) {

    	if (fArWorldCoordinatePosition == null)
    		return;
    	
    	gl.glPushMatrix();		

		float fOffsetX = 1.0f;
		float fOffsetY = -0.5f;

		gl.glTranslatef(fArWorldCoordinatePosition[0], fArWorldCoordinatePosition[1], fZValue);
				
    	if (bDrawDisplaced)
    	{   
    		gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
        	
			gl.glLineWidth(2);
			gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3f(0, 0, -fZValue);
			gl.glVertex3f(fOffsetX - fHalfWidth, fOffsetY + fHalfHeight, 0);
			gl.glVertex3f(fOffsetX - fHalfWidth, fOffsetY - fHalfHeight, 0);
			gl.glEnd();
			
			gl.glColor3f(0.2f, 0.2f, 0.2f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, -fZValue);
			gl.glVertex3f(fOffsetX - fHalfWidth, fOffsetY + fHalfHeight, 0);
			gl.glVertex3f(fOffsetX - fHalfWidth, fOffsetY - fHalfHeight, 0);
			gl.glEnd();
			
			gl.glTranslatef(fOffsetX, fOffsetY, 0.0f);
    	}
		
		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(-fHalfWidth, -fHalfHeight, 0);
		gl.glVertex3f(fHalfWidth, -fHalfHeight, 0);
		gl.glVertex3f(fHalfWidth, fHalfHeight, 0);
		gl.glVertex3f(-fHalfWidth, fHalfHeight, 0);
		gl.glEnd();
		
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-fHalfWidth, -fHalfHeight, 0);
		gl.glVertex3f(fHalfWidth, -fHalfHeight, 0);
		gl.glVertex3f(fHalfWidth, fHalfHeight, 0);
		gl.glVertex3f(-fHalfWidth, fHalfHeight, 0);
		gl.glEnd();
		
//		TextRenderer textRenderer = new TextRenderer(
//				new Font("Arial", Font.BOLD, 12), true);
//		textRenderer.begin3DRendering();
//		textRenderer.setColor(1,1,1,1);
//		
//		textRenderer.draw3D("ID: " +pickedVertex.getElementTitle(), 
//				(float)wcoord[0] + 0.05f, (float)wcoord[1] + 0.1f, fZValue - 0.01f, 0.01f);
//		
//		textRenderer.end3DRendering();
		
		if (fScaleFactor < 0.8f)
		{
			gl.glPopMatrix();
			return;
		}
		
//		float fLineHeight = 0.1f;
//		gl.glTranslatef(0.0f, fLineHeight, 0.0f);
//		
//		GLTextUtils.renderText(gl, "Name: " +pickedVertex.getVertexRepByIndex(0).getName(), 12,
//				-fHalfWidth + 0.05f, 
//				-fHalfHeight + 0.09f, -0.01f);		
		
		drawMappingAnnotation(gl, pickedVertex);
			
		gl.glPopMatrix();
	}
    
    private void drawPickedObjectInfoStar(final GL gl,
    		final PathwayVertex pickedVertex) {
    	
    	// Calculate star points by taking the gene number as edge count
		starEffectRenderer.calculateStarPoints(sLLMultipleGeneMappingID.size(), 1.2f, 0, 0);
    	
		// Draw star effect 
		Iterator<float[]>iterStarPoints = starEffectRenderer.getStarPoints().iterator();			
		float[] fArTmpPosition; 
		
		gl.glPushMatrix();
		
    	if (iterStarPoints.hasNext())
		{
			iterStarPoints.next();
		}
		
		gl.glTranslatef(getWorldCoordinatePosition()[0], 
				getWorldCoordinatePosition()[1], 0);
    	gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
		GLStarEffectRenderer.drawStar(gl, starEffectRenderer.getStarPoints());
		gl.glTranslatef(-getWorldCoordinatePosition()[0], 
				-getWorldCoordinatePosition()[1], 0);
		
		while(iterStarPoints.hasNext()) 
		{
			fArTmpPosition = iterStarPoints.next();
			gl.glTranslatef(fArTmpPosition[0], fArTmpPosition[1], 0);				
			drawPickedObjectInfoSingle(gl, pickedVertex, false);
			gl.glTranslatef(-fArTmpPosition[0], -fArTmpPosition[1], 0);
		}
		
		gl.glPopMatrix();

    }
    
    private void drawMappingAnnotation(final GL gl,
    		final PathwayVertex pickedVertex) {
    	
    	
		float fNodeWidth = pickedVertex.getVertexRepByIndex(0).getWidth() / 2.0f 
			* GLPathwayManager.SCALING_FACTOR_X;
		
		String sElementId;

		gl.glScalef(3.0f, 3.0f, 3.0f);
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))
		{			
			sElementId = sLLMultipleGeneMappingID.getFirst();
			
			refGLPathwayManager.mapExpressionByGeneId(
					gl, sLLMultipleGeneMappingID.removeFirst(), fNodeWidth);
		}
		else
		{
	    	sElementId = pickedVertex.getElementTitle();

	    	refGLPathwayManager.mapExpression(gl, pickedVertex, fNodeWidth);
		}
		gl.glScalef(1 / 3.0f, 1 / 3.0f, 1 / 3.0f);
		
		gl.glColor3f(1, 1, 1);
		GLTextUtils.renderText(gl, "ID: " +sElementId, 12,
				-fHalfWidth + 0.05f, 
				-fHalfHeight + 0.09f, -0.01f);
		
		if (!pickedVertex.getVertexType().equals(PathwayVertexType.gene))
			return;
		
		GLTextUtils.renderText(gl, "Gene short name: " 
				+geneAnnotationMapper.getGeneShortNameByNCBIGeneId(sElementId), 12,
				-fHalfWidth + 0.05f, 
				-fHalfHeight + 0.35f, -0.01f);	
    }
    
    public void convertWindowCoordinatesToWorldCoordinates(final GL gl, 
    		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY) {
    	
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - iWindowCoordinatePositionY - 1;

//		System.out.println("Coordinates at cursor are (" + point.x + ", "
//				+ realy);
		
		GLU glu = new GLU();
		glu.gluUnProject((double) iWindowCoordinatePositionX, (double) realy, 0.0, //
				mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		
//		System.out.println("World coords at z=0.0 are ( " //
//				+ wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]);
		
		fArWorldCoordinatePosition[0] = (float)wcoord[0];
		fArWorldCoordinatePosition[1] = (float)wcoord[1];
		fArWorldCoordinatePosition[2] = (float)wcoord[2];
    }
    
    public void setWorldCoordinatePosition(float x, float y, float z) {
    	
    	fArWorldCoordinatePosition[0] = x;
    	fArWorldCoordinatePosition[1] = y;
    	fArWorldCoordinatePosition[2] = z;
    }
    
    public float[] getWorldCoordinatePosition() {
    	
    	return fArWorldCoordinatePosition;
    }
    
    public void resetAnimation() {

    	fScaleFactor = 0.0f;
    }
    
    public void resetPoint() {
    	
    	fArWorldCoordinatePosition = null;
    }
    
    public final boolean isPositionValid() {
    	
    	if (fArWorldCoordinatePosition == null)
    		return false;
    	
    	return true;
    }
    
    private void extractMultipleGeneMapping(final PathwayVertex pickedVertex) {
    	
		StringTokenizer tokenizer = new StringTokenizer(pickedVertex.getElementTitle());
		sLLMultipleGeneMappingID.clear();
		
		while (tokenizer.hasMoreTokens())
		{
			sLLMultipleGeneMappingID.addFirst(tokenizer.nextToken());
		}
    }
}
