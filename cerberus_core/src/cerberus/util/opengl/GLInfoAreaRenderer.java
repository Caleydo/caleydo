package cerberus.util.opengl;

import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.org.apache.bcel.internal.generic.IALOAD;
import com.sun.org.apache.bcel.internal.generic.SALOAD;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import com.sun.opengl.*;
import com.sun.opengl.util.j2d.TextRenderer;
import cerberus.manager.IGeneralManager;
import cerberus.util.mapping.GeneAnnotationMapper;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayManager;

public class GLInfoAreaRenderer {

	private IGeneralManager refGeneralManager;
	
	private float[] fArWorldCoordinatePosition;
	
	private float fScaleFactor = 0.0f;

	private GLStarEffectRenderer starEffectRenderer;
	
	private float fZValue = 1f;
	
	private GLPathwayManager refGLPathwayManager;
	
	private LinkedList<String> sLLMultipleGeneMappingID;
	
	private GeneAnnotationMapper geneAnnotationMapper;
	
	private float fHeight = 0.4f;
	private float fWidth = 1.0f;
	
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
		
		gl.glTranslatef(fArWorldCoordinatePosition[0], fArWorldCoordinatePosition[1], fZValue);
				
    	if (bDrawDisplaced)
    	{   
    		float fOffsetX = 1.0f;
    		float fOffsetY = 1.0f;
    		
    		gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
        	
			gl.glLineWidth(2);
			gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3f(0, 0, -fZValue);
			gl.glVertex3f(fOffsetX, fOffsetY - fHeight, 0);
			gl.glVertex3f(fOffsetX, fOffsetY, 0);
			gl.glEnd();
			
			gl.glColor3f(0.2f, 0.2f, 0.2f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, -fZValue);
			gl.glVertex3f(fOffsetX, fOffsetY - fHeight, 0);
			gl.glVertex3f(fOffsetX, fOffsetY, 0);
			gl.glEnd();
			
			gl.glTranslatef(fOffsetX, fOffsetY, 0.0f);
    	}
		
		if (fScaleFactor < 1.0f)
		{
			gl.glPopMatrix();
			return;
		}		
		
		// FIXME: Workflow is not optimal
		// Do composition of info label string only once and store them (heading + text)
		float fMaxWidth = calculateInfoAreaWidth(pickedVertex);

		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fMaxWidth, 0, 0);
		gl.glVertex3f(fMaxWidth, -fHeight, 0);
		gl.glVertex3f(0, -fHeight, 0);
		gl.glEnd();
		
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fMaxWidth, 0, 0);
		gl.glVertex3f(fMaxWidth, -fHeight, 0);
		gl.glVertex3f(0, -fHeight, 0);
		gl.glEnd();
		
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
			gl.glTranslatef(fArTmpPosition[0]-fWidth/2f, 
					fArTmpPosition[1]+fHeight/2f, 0);				
			drawPickedObjectInfoSingle(gl, pickedVertex, false);
			gl.glTranslatef(-fArTmpPosition[0]+fWidth/2f, 
					-fArTmpPosition[1]-fHeight/2f, 0);
		}
		
		gl.glPopMatrix();

    }
    
    private float calculateInfoAreaWidth(final PathwayVertex pickedVertex) {
    	
		TextRenderer textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		textRenderer.begin3DRendering();
		
		float fMaxWidth = 0.0f;
		String sElementId;
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))
			sElementId = sLLMultipleGeneMappingID.getFirst();
		else
	    	sElementId = pickedVertex.getElementTitle();
		
		// Save text length as new width if it bigger than previous one
		float fCurrentWidth = 2.2f * (float)textRenderer.getBounds("ID: " +sElementId).getWidth() * GLPathwayManager.SCALING_FACTOR_X;
		if (fMaxWidth < fCurrentWidth)
			fMaxWidth = fCurrentWidth;

		String sTmp = "";
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))		
		{
			sTmp = "Gene short name:" +geneAnnotationMapper.getGeneShortNameByNCBIGeneId(sElementId);
		}
		else if (pickedVertex.getVertexType().equals(PathwayVertexType.map))
		{
			sTmp = pickedVertex.getVertexRepByIndex(0).getName();
			
			// Remove "TITLE: "
			if (sTmp.contains("TITLE:"))
				sTmp = sTmp.substring(6);
			
			sTmp = "Pathway name: " + sTmp;
		}
		
		// Save text length as new width if it bigger than previous one
		fCurrentWidth = 2.2f * (float)textRenderer.getBounds(sTmp).getWidth() * GLPathwayManager.SCALING_FACTOR_X;
		if (fMaxWidth < fCurrentWidth)
			fMaxWidth = fCurrentWidth;
		
		textRenderer.end3DRendering();
		
    	return fMaxWidth;
    }
    
    private void drawMappingAnnotation(final GL gl,
    		final PathwayVertex pickedVertex) {
    	
		TextRenderer textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		textRenderer.begin3DRendering();
    	
		float fLineHeight = 2.3f * (float)textRenderer.getBounds("A").getHeight() * GLPathwayManager.SCALING_FACTOR_Y;
		float fXOffset = 0.03f;
		float fYOffset = -0.03f;
		
		String sElementId;
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))
			sElementId = sLLMultipleGeneMappingID.getFirst();
		else
	    	sElementId = pickedVertex.getElementTitle();
		
		gl.glColor3f(1, 1, 1);
//		GLTextUtils.renderText(gl, "ID: " +sElementId, 12,
//				-fHalfWidth + 0.05f, 
//				-fHalfHeight + 0.09f, -0.01f);
		textRenderer.draw3D("ID: " +sElementId,
				fXOffset, 
				fYOffset - fLineHeight, 
				0.01f,
				0.005f);  // scale factor
		
		String sTmp = "";
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))		
		{
			sTmp = "Gene short name:" +geneAnnotationMapper.getGeneShortNameByNCBIGeneId(sElementId);
		}
		else if (pickedVertex.getVertexType().equals(PathwayVertexType.map))
		{
			sTmp = pickedVertex.getVertexRepByIndex(0).getName();
			
			// Remove "TITLE: "
			if (sTmp.contains("TITLE:"))
				sTmp = sTmp.substring(6);
			
			sTmp = "Pathway name: " + sTmp;
		}
		textRenderer.draw3D(sTmp,
				fXOffset, 
				fYOffset - 2*fLineHeight, 
				0.01f,
				0.005f);  // scale factor
		
		textRenderer.end3DRendering();
		
		// Render mapping if available
		gl.glTranslatef(10*fXOffset, -3.6f*fLineHeight, 0.02f);
		gl.glScalef(3.0f, 3.0f, 3.0f);
		if (pickedVertex.getVertexType().equals(PathwayVertexType.gene))
		{					
			float fNodeWidth = pickedVertex.getVertexRepByIndex(0).getWidth() / 2.0f 
				* GLPathwayManager.SCALING_FACTOR_X;
			
			refGLPathwayManager.mapExpressionByGeneId(
					gl, sLLMultipleGeneMappingID.removeFirst(), fNodeWidth);
		}
//		else
//		{
//	    	refGLPathwayManager.mapExpression(gl, pickedVertex, fNodeWidth);
//		}
		gl.glScalef(1 / 3.0f, 1 / 3.0f, 1 / 3.0f);
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
