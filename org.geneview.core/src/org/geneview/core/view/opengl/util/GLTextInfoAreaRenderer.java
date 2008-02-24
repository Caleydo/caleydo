package org.geneview.core.view.opengl.util;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;




public class GLTextInfoAreaRenderer 
{

	TextRenderer textRenderer;
	
	ArrayList<String> sContent;
	Point pickedPoint;
	
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
	}
	
	public void renderInfoArea(GL gl)
	{		
		float[] fArWorldCoords = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
		
		float fXOrigin = fArWorldCoords[0];
		float fYOrigin = fArWorldCoords[1];
		
		Iterator<String> contentIterator = sContent.iterator();
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		
		float fHeight = 0;
		float fWidth = 0;
		
		Rectangle2D box;
		
		
		
		String sCurrent;
		float fXLowerLeft = fXOrigin + 0.2f;
		float fYLowerLeft = fYOrigin + 0.2f;
		textRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
		textRenderer.begin3DRendering();
		while(contentIterator.hasNext())
		{
			sCurrent = contentIterator.next();
			
			box = textRenderer.getBounds(sCurrent).getBounds2D();
			fHeight += box.getHeight() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR;
			fWidth = (float)box.getWidth() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR;
			
			textRenderer.draw3D(sCurrent,
						fXLowerLeft, fYLowerLeft, 0.002f,
						ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
		}
		textRenderer.end3DRendering();	
	
		
		gl.glColor4f(0.1f, 0.1f, 0.1f, 0.7f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, 0);
		//gl.glVertex3f(fXLowerLeft, fYLowerLeft, 0);
		gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft, 0);
		gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft + fHeight, 0);
		gl.glVertex3f(fXLowerLeft, fYLowerLeft + fHeight, 0);
		gl.glVertex3f(fXLowerLeft, fYLowerLeft + 0.05f, 0);
		//gl.glVertex3f(fXOrigin, fYOrigin, 0);
		gl.glEnd();
	}
	
}
