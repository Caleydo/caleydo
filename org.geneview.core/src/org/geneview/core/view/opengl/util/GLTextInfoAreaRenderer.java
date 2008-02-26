package org.geneview.core.view.opengl.util;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.geneview.core.view.opengl.miniview.AGLMiniView;

import com.sun.opengl.util.j2d.TextRenderer;




public class GLTextInfoAreaRenderer 
{

	private TextRenderer textRenderer;
	
	private ArrayList<String> sContent;
	private Point pickedPoint;
	private AGLMiniView miniView;
	
	private float fHeight = 0;
	private float fWidth = 0;
	private float fTextWidth;
	private float fSpacing = 0.02f;
	private float fZValue = 0.001f;
	
	public GLTextInfoAreaRenderer()
	{
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false); 
	}
	/**
	 * Set the data to be rendered.
	 * 
	 * @param sContent An ArrayList of Strings
	 * @param pickedPoint the picked point in Screen Coordinates!
	 */
	public void setData(ArrayList<String> sContent, Point pickedPoint) 
	{
		this.sContent = sContent;
		this.pickedPoint = pickedPoint;
		miniView = null;
	}
	
	/**
	 * Set the data to be rendered.
	 * 
	 * @param sContent An ArrayList of Strings
	 * @param pickedPoint the picked point in Screen Coordinates!
	 */
	public void setData(ArrayList<String> sContent, Point pickedPoint, AGLMiniView miniView) 
	{
		setData(sContent, pickedPoint);
		this.miniView = miniView;	
	}
	
	public void renderInfoArea(GL gl)
	{		
		fHeight = 0;
		fWidth = 0;
		float[] fArWorldCoords = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
		
		calculateWidthAndHeight();
		
		float fXOrigin = fArWorldCoords[0];
		float fYOrigin = fArWorldCoords[1];
		float fTextHeight = 0;
		
		
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);		
		gl.glBegin(GL.GL_LINE);
		gl.glVertex3f(0, 0, 0);
		
		//gl.glVertex3f(fXOrigin, fYOrigin, 0);
		gl.glEnd();
		gl.glVertex3f(0, 0, 0);	
		
		
		String sCurrent;
		float fXLowerLeft = fXOrigin + 0.2f;
		float fYLowerLeft = fYOrigin + 0.2f;
		//gl.glColor4f(0.1f, 0.1f, 0.1f, 0.7f);
		//textRenderer.setColor(0f, 0f, 0f, 1);
		//textRenderer.
		
		textRenderer.begin3DRendering();
		
		Iterator<String> contentIterator = sContent.iterator();
		int iCount = 0;
		while(contentIterator.hasNext())
		{
			
			sCurrent = contentIterator.next();			
			fTextHeight += (float)textRenderer.getBounds(sCurrent).getHeight() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR;
			textRenderer.draw3D(sCurrent,
						fXLowerLeft  + fSpacing, fYLowerLeft + fTextHeight + fSpacing * iCount, fZValue,
						ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
			iCount++;
		}
		textRenderer.end3DRendering();	
		
		if(miniView != null)
			miniView.render(gl, fXLowerLeft + fTextWidth + fSpacing, fYLowerLeft + fSpacing);
	
		
		gl.glColor4f(0.1f, 0.1f, 0.1f, 0.7f);
		gl.glBegin(GL.GL_POLYGON);
//		gl.glVertex3f(fXOrigin, fYOrigin, 0);
		
		gl.glVertex3f(fXLowerLeft, fYLowerLeft, 0);
		gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft, 0);
		gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft + fHeight, 0);
		gl.glVertex3f(fXLowerLeft, fYLowerLeft + fHeight, 0);
		gl.glVertex3f(fXLowerLeft, fYLowerLeft + 0.05f, 0);
		//gl.glVertex3f(fXOrigin, fYOrigin, 0);
		gl.glEnd();
	}
	
	public void calculateWidthAndHeight()
	{
		String sCurrent;
		
		Rectangle2D box;
		float fTemp;
		
		Iterator<String> contentIterator = sContent.iterator();
		while(contentIterator.hasNext())
		{
			sCurrent = contentIterator.next();
			
			box = textRenderer.getBounds(sCurrent).getBounds2D();
			fHeight += (box.getHeight() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
			
			fTemp = ((float)box.getWidth() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
		
			if(fTemp > fWidth)
			{
				fWidth = fTemp;
			}
			fHeight += fSpacing;
			
		}
		fWidth += 2 * fSpacing;
		fHeight += 2 * fSpacing;
		
		fTextWidth = fWidth;
		if (miniView != null)
		{
			fWidth += (miniView.getWidth() + fSpacing * 2);
			
			if(fHeight < miniView.getHeight())
				fHeight = miniView.getHeight();
			
			fHeight += fSpacing * 2;
		}
		
	}
	
}
	
	
//	package org.geneview.core.view.opengl.util;
//
//	import java.awt.Font;
//import java.awt.Point;
//import java.util.ArrayList;
//import java.util.Iterator;
//
//import javax.media.opengl.GL;
//import javax.media.opengl.glu.GLU;
//
//import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
//import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
//import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
//import org.geneview.core.data.view.rep.renderstyle.PathwayRenderStyle;
//import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
//import org.geneview.util.graph.EGraphItemProperty;
//
//import com.sun.opengl.util.j2d.TextRenderer;
//
//	public class GLTextInfoAreaRenderer {
//		
//		private float[] fArWorldCoordinatePosition;
//		
//		private float fScaleFactor = 0.0f;
//
//			
//		private float fZValue = 1f;
//				
//		private float fHeight = 0.5f;
//		private float fWidth = 1.7f;
//		
//		private TextRenderer textRenderer;
//		
//		private PathwayRenderStyle refRenderStyle;
//		
//		private boolean bEnableColorMapping = false;
//		
//		ArrayList<String> sContent;
//		Point pickedPoint;
//		
//		public GLTextInfoAreaRenderer(final IGeneralManager refGeneralManager,
//				final GLPathwayManager refGLPathwayManager) 
//		{			
//
//			
//			textRenderer = new TextRenderer(new Font("Arial",
//					Font.BOLD, 16), false);
//			
//			refRenderStyle = new PathwayRenderStyle();
//		}
//		
//		public void setData(ArrayList<String> sContent, Point pickedPoint) 
//		{
//			this.sContent = sContent;
//			this.pickedPoint = pickedPoint;
//		}
//		
	    
//	    public void renderInfoArea(final GL gl)
//	    {
//	    	
//	    	
//	    	
//	    	if (fScaleFactor < 1.0)
//	    		fScaleFactor += 0.1f;
//	    	
//	    	
//	 
//	    }
	    
//	    public void renderInfoArea(final GL gl)
//	    {
//	    	
//	    	if (fScaleFactor < 1.0)
//	    		fScaleFactor += 0.06;
//	    	
//		
//			
//			drawPickedObjectInfoSingle(gl, true);
//	    }
//		
//	    private void drawPickedObjectInfoSingle(final GL gl,
//				final boolean bDrawDisplaced) {
//
//	    	if (fArWorldCoordinatePosition == null)
//	    		return;
//	    	
//	    	gl.glPushMatrix();		
//			
//			gl.glTranslatef(fArWorldCoordinatePosition[0], fArWorldCoordinatePosition[1], fZValue);
//					
//	    	if (bDrawDisplaced)
//	    	{   
//	    		float fOffsetX = 0.5f;
//	    		float fOffsetY = 0.7f;
//	    		
//	    		gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
//	        	
//				gl.glLineWidth(2);
//				gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
//				gl.glBegin(GL.GL_TRIANGLES);
//				gl.glVertex3f(0, 0, -fZValue);
//				gl.glVertex3f(fOffsetX, fOffsetY - fHeight, 0);
//				gl.glVertex3f(fOffsetX, fOffsetY, 0);
//				gl.glEnd();
//				
//				gl.glColor3f(0.2f, 0.2f, 0.2f);
//				gl.glBegin(GL.GL_LINE_LOOP);
//				gl.glVertex3f(0, 0, -fZValue);
//				gl.glVertex3f(fOffsetX, fOffsetY - fHeight, 0);
//				gl.glVertex3f(fOffsetX, fOffsetY, 0);
//				gl.glEnd();
//				
//				gl.glTranslatef(fOffsetX, fOffsetY, 0.0f);
//	    	}
//			
//			if (fScaleFactor < 1.0f)
//			{
//				gl.glPopMatrix();
//				return;
//			}		
//			
//			// FIXME: Workflow is not optimal
//			// Do composition of info label string only once and store them (heading + text)
//			float fMaxWidth = calculateInfoAreaWidth(sContent.get(0));
//			
//			if (fMaxWidth < fWidth)
//				fMaxWidth = fWidth;
//
//			gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
//			gl.glBegin(GL.GL_POLYGON);
//			gl.glVertex3f(0, 0, 0);
//			gl.glVertex3f(fMaxWidth, 0, 0);
//			gl.glVertex3f(fMaxWidth, -fHeight, 0);
//			gl.glVertex3f(0, -fHeight, 0);
//			gl.glEnd();
//			
//			gl.glColor3f(0.2f, 0.2f, 0.2f);
//			gl.glBegin(GL.GL_LINE_LOOP);
//			gl.glVertex3f(0, 0, 0);
//			gl.glVertex3f(fMaxWidth, 0, 0);
//			gl.glVertex3f(fMaxWidth, -fHeight, 0);
//			gl.glVertex3f(0, -fHeight, 0);
//			gl.glEnd();
//			
//			drawMappingAnnotation(gl);
//						
//			gl.glPopMatrix();
//		}
//	    
//	    private void drawPickedObjectInfoStar(final GL gl) {
//	    	
//	    	// Calculate star points by taking the gene number as edge count
//	    	float fStarRadius = llMultipleMappingGenes.size() / 4.0f;
//	    	
//	    	if (fStarRadius >= 1.2f)
//	    		fStarRadius = 1.2f;
//	    	
//			starEffectRenderer.calculateStarPoints(llMultipleMappingGenes.size(), fStarRadius, 0, 0);
//	    	
//			// Draw star effect 
//			Iterator<float[]>iterStarPoints = starEffectRenderer.getStarPoints().iterator();			
//			float[] fArTmpPosition; 
//			
//			gl.glPushMatrix();
//			
//	    	if (iterStarPoints.hasNext())
//			{
//				iterStarPoints.next();
//			}
//			
//	    	float fStarElementZDisplacement = 0f;
//			gl.glTranslatef(getWorldCoordinatePosition()[0], 
//					getWorldCoordinatePosition()[1], 0);
//	    	gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
//			GLStarEffectRenderer.drawStar(gl, starEffectRenderer.getStarPoints());
//			gl.glTranslatef(-getWorldCoordinatePosition()[0], 
//					-getWorldCoordinatePosition()[1], 0);
//			
//			while(iterStarPoints.hasNext()) 
//			{
//				fArTmpPosition = iterStarPoints.next();
//				gl.glTranslatef(fArTmpPosition[0]-fWidth/2f, 
//						fArTmpPosition[1]+fHeight/2f, fStarElementZDisplacement);				
//				drawPickedObjectInfoSingle(gl, false);
//				gl.glTranslatef(-fArTmpPosition[0]+fWidth/2f, 
//						-fArTmpPosition[1]-fHeight/2f, fStarElementZDisplacement);
//				
//				fStarElementZDisplacement += 0.002f;
//			}
//			
//			gl.glPopMatrix();
//
//	    }
//	    
//	    private float calculateInfoAreaWidth() 
//	    {
//	    	
//			textRenderer.begin3DRendering();
//			
//			float fMaxWidth = 0.0f;
//			String sElementId = pickedVertex.getName();
//			
//			// Save text length as new width if it bigger than previous one
//			float fCurrentWidth = 2.2f * (float)textRenderer.getBounds("ID: " 
//					+sElementId).getWidth() * GLPathwayManager.SCALING_FACTOR_X;
//			
//			if (fMaxWidth < fCurrentWidth)
//				fMaxWidth = fCurrentWidth;
//
//			String sTmp = "";
//			if (pickedVertex.getType().equals(EPathwayVertexType.gene))		
//			{
//				sTmp = "Gene short name:" +geneAnnotationMapper.getGeneShortNameByNCBIGeneId(sElementId);
//			}
//			else if (pickedVertex.getType().equals(EPathwayVertexType.map))
//			{
//				sTmp = pickedVertex.getName();
//				
//				// Remove "TITLE: "
//				if (sTmp.contains("TITLE:"))
//					sTmp = sTmp.substring(6);
//				
//				sTmp = "Pathway name: " + sTmp;
//			}
//			
//			// Save text length as new width if it bigger than previous one
//			fCurrentWidth = 2.2f * (float)textRenderer.getBounds(sTmp).getWidth() * GLPathwayManager.SCALING_FACTOR_X;
//			if (fMaxWidth < fCurrentWidth)
//				fMaxWidth = fCurrentWidth;
//			
//			textRenderer.end3DRendering();
//			
//	    	return fMaxWidth;
//	    }
//	    
//	    private void drawMappingAnnotation(final GL gl) {
//	    	
//			textRenderer.begin3DRendering();
//	    	
//			float fLineHeight = 2.8f * (float)textRenderer.getBounds("A").getHeight() * GLPathwayManager.SCALING_FACTOR_Y;
//			float fXOffset = 0.03f;
//			float fYOffset = -0.03f;
//			
//			PathwayVertexGraphItem tmpVertexGraphItem = llMultipleMappingGenes.getFirst();
//
//			String sElementId = llMultipleMappingGenes.getFirst().getName();		
//			String sName = "";
//			String sType = "";
//			
//			if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.gene))		
//			{
//				sType = EPathwayVertexType.gene.getName();
//				sName = geneAnnotationMapper.getGeneShortNameByNCBIGeneId(sElementId);
//			}
//			else if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.compound)) 
//			{
//				sType = EPathwayVertexType.compound.getName();
//				sName = ((PathwayVertexGraphItemRep)tmpVertexGraphItem.getAllItemsByProp(
//						EGraphItemProperty.ALIAS_CHILD).get(0)).getName();
//			}
//			else if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.enzyme)) 
//			{
//				sType = EPathwayVertexType.enzyme.getName();
//				sName = ((PathwayVertexGraphItemRep)tmpVertexGraphItem.getAllItemsByProp(
//						EGraphItemProperty.ALIAS_CHILD).get(0)).getName();
//			}
//			else if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.map))
//			{
//				sType = EPathwayVertexType.map.getName();
//				sName = ((PathwayVertexGraphItemRep)tmpVertexGraphItem.getAllItemsByProp(
//						EGraphItemProperty.ALIAS_CHILD).get(0)).getName();
//				
//				// Remove "TITLE: "
//				if (sName.contains("TITLE:"))
//					sName = sName.substring(6);			
//			}
//
//			gl.glColor3f(1, 1, 1);
//
//			textRenderer.draw3D("Type:",
//					fXOffset, 
//					fYOffset - fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			
//			textRenderer.draw3D(sType,
//					fXOffset + 0.3f, 
//					fYOffset - fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//
//			textRenderer.draw3D("ID:",
//					fXOffset, 
//					fYOffset - 2*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			
//			textRenderer.draw3D(sElementId,
//					fXOffset + 0.3f, 
//					fYOffset - 2*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//
//			textRenderer.draw3D("Name: ",
//					fXOffset, 
//					fYOffset - 3*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			
//			textRenderer.draw3D(sName,
//					fXOffset + 0.3f, 
//					fYOffset - 3*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			
//			if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.gene))		
//			{
//				String sAccessionCode = 
//					geneAnnotationMapper.getAccessionCodeByNCBIGeneIdCode(sElementId);
//				
//				textRenderer.draw3D("Acc.: ",
//					fXOffset, 
//					fYOffset - 4*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			
//				textRenderer.draw3D(sAccessionCode,
//					fXOffset + 0.3f, 
//					fYOffset - 4*fLineHeight, 
//					0.001f,
//					0.005f);  // scale factor
//			}
//			
//			textRenderer.end3DRendering();
//			
//			if (bEnableColorMapping) 
//			{
//				// Render mapping if available
//				gl.glTranslatef(1.3f, -2f*fLineHeight-0.05f, -0.045f);
//				gl.glScalef(6.0f, 6.0f, 6.0f);
//				if (tmpVertexGraphItem.getType().equals(EPathwayVertexType.gene))
//				{					
//					float fNodeWidth = refRenderStyle.getEnzymeNodeWidth(true);
//					float fNodeHeight = refRenderStyle.getEnzymeNodeHeight(true);
//					
//					refGLPathwayManager.mapExpressionByGeneId(
//							gl, llMultipleMappingGenes.get(0).getName(), 
//							fNodeWidth, fNodeHeight, true);
//					
//					llMultipleMappingGenes.remove(0);
//				}
//			}
//			gl.glScalef(1 / 6.0f, 1 / 6.0f, 1 / 6.0f);
//	    }
//	    
//	    public void convertWindowCoordinatesToWorldCoordinates(final GL gl, 
//	    		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY) {
//	    	
//			double mvmatrix[] = new double[16];
//			double projmatrix[] = new double[16];
//			int realy = 0;// GL y coord pos
//			double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords
//			int viewport[] = new int[4];
//			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//
//			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
//			gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
//			/* note viewport[3] is height of window in pixels */
//			realy = viewport[3] - iWindowCoordinatePositionY - 1;
//
////			System.out.println("Coordinates at cursor are (" + point.x + ", "
////					+ realy);
//			
//			GLU glu = new GLU();
//			glu.gluUnProject((double) iWindowCoordinatePositionX, (double) realy, 0.0, //
//					mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
//			
////			System.out.println("World coords at z=0.0 are ( " //
////					+ wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]);
//			
//			if (fArWorldCoordinatePosition == null)
//				fArWorldCoordinatePosition = new float[3];
//			
//			fArWorldCoordinatePosition[0] = (float)wcoord[0];
//			fArWorldCoordinatePosition[1] = (float)wcoord[1];
//			fArWorldCoordinatePosition[2] = (float)wcoord[2];
//	    }
//	    
//	    public void setWorldCoordinatePosition(float x, float y, float z) {
//	    	
//			if (fArWorldCoordinatePosition == null)
//				fArWorldCoordinatePosition = new float[3];
//			
//	    	fArWorldCoordinatePosition[0] = x;
//	    	fArWorldCoordinatePosition[1] = y;
//	    	fArWorldCoordinatePosition[2] = z;
//	    }
//	    
//	    public float[] getWorldCoordinatePosition() {
//	    	
//	    	return fArWorldCoordinatePosition;
//	    }
//	    
//	    public void resetAnimation() {
//
//	    	fScaleFactor = 0.0f;
//	    }
//	    
//	    public void resetPoint() {
//	    	
//	    	fArWorldCoordinatePosition = null;
//	    }
//	    
//	    public final boolean isPositionValid() {
//	    	
//	    	if (fArWorldCoordinatePosition == null)
//	    		return false;
//	    	
//	    	return true;
//	    }
//	    
//	    
//	    public void enableColorMappingArea(boolean bEnableColorMapping) {
//	    	
//	    	this.bEnableColorMapping = bEnableColorMapping;
//	    }
//	}
//	
